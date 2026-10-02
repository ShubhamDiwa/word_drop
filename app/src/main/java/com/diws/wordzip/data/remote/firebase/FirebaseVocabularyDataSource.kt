package com.diws.wordzip.data.remote.firebase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseVocabularyDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore?
) : VocabularyRemoteDataSource {

    companion object {
        const val TAG = "FirebaseSync"
        const val COLLECTION_WORDS = "words"
        val CANDIDATE_COLLECTIONS = listOf(
            "words",
            "Words",
            "vocabulary",
            "Vocabulary",
            "wordzip",
            "worddrop",
            "words_list"
        )
    }

    override suspend fun getChangedWordsSince(lastSyncedAt: Long): List<FirebaseWordDto> {
        if (!isFirebaseAvailable()) {
            Log.w(TAG, "Firebase is not available on this device/app")
        }

        val db = firestore ?: try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseFirestore.getInstance failed", e)
            return emptyList()
        }

        return try {
            val allParsedDtos = mutableListOf<FirebaseWordDto>()
            val seenIds = mutableSetOf<String>()

            for (collName in CANDIDATE_COLLECTIONS) {
                try {
                    val snapshot = db.collection(collName).get().await()
                    if (!snapshot.isEmpty) {
                        Log.d(TAG, "Collection '$collName' returned ${snapshot.documents.size} raw documents")
                        for (doc in snapshot.documents) {
                            val dtosFromDoc = parseDocumentOrList(doc)
                            for (dto in dtosFromDoc) {
                                val key = dto.id.ifBlank { dto.word.lowercase() }
                                if (seenIds.add(key)) {
                                    allParsedDtos.add(dto)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Fetch from collection '$collName' failed or not permitted: ${e.message}")
                }
            }

            if (allParsedDtos.isEmpty()) {
                Log.w(TAG, "No valid word documents found across collections: $CANDIDATE_COLLECTIONS")
                return emptyList()
            }

            Log.d(TAG, "Successfully extracted and parsed ${allParsedDtos.size} unique words from Firestore")
            allParsedDtos
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching words from Firestore", e)
            emptyList()
        }
    }

    override suspend fun getAllWords(): List<FirebaseWordDto> {
        return getChangedWordsSince(0L)
    }

    private fun parseDocumentOrList(doc: DocumentSnapshot): List<FirebaseWordDto> {
        val data = doc.data ?: return emptyList()

        // Check if this document contains an array of words
        val candidateArrayKeys = listOf("words", "Words", "vocabulary", "Vocabulary", "items", "Items", "list", "List", "data", "Data", "wordList", "wordsList")
        for (arrayKey in candidateArrayKeys) {
            val list = data[arrayKey] as? List<*>
            if (!list.isNullOrEmpty()) {
                val listDtos = list.mapNotNull { item ->
                    if (item is Map<*, *>) {
                        parseMapToDto(item, doc.id)
                    } else null
                }
                if (listDtos.isNotEmpty()) {
                    return listDtos
                }
            }
        }

        // Otherwise parse the document itself
        val singleDto = parseMapToDto(data, doc.id)
        return if (singleDto != null) listOf(singleDto) else emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseMapToDto(data: Map<*, *>, fallbackDocId: String): FirebaseWordDto? {
        return try {
            val id = (data["id"] as? String)?.takeIf { it.isNotBlank() }
                ?: (data["wordId"] as? String)?.takeIf { it.isNotBlank() }
                ?: (data["_id"] as? String)?.takeIf { it.isNotBlank() }
                ?: fallbackDocId

            var word = (data["word"] as? String)?.trim()
                ?: (data["Word"] as? String)?.trim()
                ?: (data["name"] as? String)?.trim()
                ?: (data["text"] as? String)?.trim()
                ?: (data["term"] as? String)?.trim()
                ?: (data["title"] as? String)?.trim()
                ?: (data["english"] as? String)?.trim()
                ?: (data["headword"] as? String)?.trim()
                ?: ""

            val definition = (data["definition"] as? String)?.trim()
                ?: (data["Definition"] as? String)?.trim()
                ?: (data["meaning"] as? String)?.trim()
                ?: (data["Meaning"] as? String)?.trim()
                ?: (data["simpleMeaning"] as? String)?.trim()
                ?: (data["simple_meaning"] as? String)?.trim()
                ?: (data["desc"] as? String)?.trim()
                ?: (data["description"] as? String)?.trim()
                ?: (data["Description"] as? String)?.trim()
                ?: (data["explanation"] as? String)?.trim()
                ?: (data["details"] as? String)?.trim()
                ?: ""

            if (word.isBlank() && definition.isBlank()) {
                return null
            }

            if (word.isBlank()) {
                word = id.replace(Regex("[^a-zA-Z]"), "").uppercase().ifBlank { "VOCABULARY" }
            }

            val finalId = if (id == fallbackDocId && word.isNotBlank()) word.lowercase() else id

            val pronunciation = (data["pronunciation"] as? String)
                ?: (data["Pronunciation"] as? String)
                ?: (data["phonetic"] as? String)
                ?: (data["phonetics"] as? String)
            val partOfSpeech = (data["partOfSpeech"] as? String)
                ?: (data["part_of_speech"] as? String)
                ?: (data["pos"] as? String)
                ?: (data["POS"] as? String)
                ?: (data["type"] as? String)
            val simpleMeaning = (data["simpleMeaning"] as? String)
                ?: (data["meaning"] as? String)
                ?: (data["shortMeaning"] as? String)
            val example = (data["example"] as? String)
                ?: (data["Example"] as? String)
                ?: (data["sentence"] as? String)
                ?: (data["usage"] as? String)
            val synonyms = data["synonyms"] ?: data["Synonyms"] ?: data["synonymList"] ?: data["similarWords"]

            var difficultyRaw = (data["difficulty"] as? String) ?: (data["level"] as? String) ?: "INTERMEDIATE"
            if (difficultyRaw.contains("BEGINNER", ignoreCase = true) || difficultyRaw.contains("EASY", ignoreCase = true)) difficultyRaw = "BEGINNER"
            else if (difficultyRaw.contains("ADVANCED", ignoreCase = true) || difficultyRaw.contains("HARD", ignoreCase = true)) difficultyRaw = "ADVANCED"
            else difficultyRaw = "INTERMEDIATE"

            val category = (data["category"] as? String) ?: (data["tag"] as? String) ?: "General"
            val audioUrl = (data["audioUrl"] as? String) ?: (data["audio"] as? String) ?: (data["sound"] as? String)

            val active = when (val a = data["active"] ?: data["isActive"] ?: data["status"]) {
                is Boolean -> a
                is String -> !a.equals("inactive", ignoreCase = true) && !a.equals("false", ignoreCase = true) && !a.equals("deleted", ignoreCase = true)
                is Number -> a.toInt() != 0
                null -> true
                else -> true
            }

            val version = (data["version"] as? Number)?.toLong() ?: 1L

            val updatedAtRaw = data["updatedAt"] ?: data["timestamp"] ?: data["createdAt"]
            val updatedAt = when (updatedAtRaw) {
                is Number -> updatedAtRaw.toLong()
                is Timestamp -> updatedAtRaw.seconds * 1000
                is String -> updatedAtRaw.toLongOrNull() ?: 0L
                else -> 0L
            }

            val translationsMap = mutableMapOf<String, String>()
            val rawTranslations = (data["translations"] ?: data["translationsMap"] ?: data["meanings"]) as? Map<*, *>
            rawTranslations?.forEach { (k, v) ->
                if (k is String && v is String && k.isNotBlank() && v.isNotBlank()) {
                    translationsMap[k.trim().lowercase()] = v.trim()
                }
            }

            // Direct language keys & aliases support
            val langKeys = mapOf(
                "hi" to listOf("hi", "hindi", "hindiMeaning", "hindi_meaning", "meaning_hi"),
                "gu" to listOf("gu", "gujarati", "gujaratiMeaning", "gujarati_meaning", "meaning_gu"),
                "mr" to listOf("mr", "marathi", "marathiMeaning", "marathi_meaning", "meaning_mr"),
                "bn" to listOf("bn", "bengali", "bengaliMeaning", "bengali_meaning", "meaning_bn"),
                "te" to listOf("te", "telugu", "teluguMeaning", "telugu_meaning", "meaning_te"),
                "ta" to listOf("ta", "tamil", "tamilMeaning", "tamil_meaning", "meaning_ta"),
                "kn" to listOf("kn", "kannada", "kannadaMeaning", "kannada_meaning", "meaning_kn"),
                "ml" to listOf("ml", "malayalam", "malayalamMeaning", "malayalam_meaning", "meaning_ml"),
                "pa" to listOf("pa", "punjabi", "punjabiMeaning", "punjabi_meaning", "meaning_pa"),
                "or" to listOf("or", "odia", "oriya", "odiaMeaning", "odia_meaning", "meaning_or"),
                "ur" to listOf("ur", "urdu", "urduMeaning", "urdu_meaning", "meaning_ur")
            )

            for ((langCode, aliases) in langKeys) {
                for (alias in aliases) {
                    val meaning = (data[alias] as? String)?.trim()
                    if (!meaning.isNullOrBlank()) {
                        translationsMap.putIfAbsent(langCode, meaning)
                        break
                    }
                }
            }

            FirebaseWordDto(
                id = id,
                word = word,
                pronunciation = pronunciation,
                partOfSpeech = partOfSpeech,
                definition = if (definition.isNotBlank()) definition else (simpleMeaning ?: "Vocabulary word"),
                simpleMeaning = simpleMeaning,
                example = example,
                synonyms = synonyms,
                difficulty = difficultyRaw,
                category = category,
                audioUrl = audioUrl,
                active = active,
                version = version,
                updatedAt = updatedAt,
                translations = if (translationsMap.isNotEmpty()) translationsMap else null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse document $fallbackDocId", e)
            null
        }
    }

    private fun isFirebaseAvailable(): Boolean {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseApp check failed", e)
            false
        }
    }
}
