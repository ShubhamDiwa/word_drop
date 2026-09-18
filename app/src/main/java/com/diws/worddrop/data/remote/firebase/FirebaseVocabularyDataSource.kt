package com.diws.worddrop.data.remote.firebase

import android.content.Context
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
    }

    override suspend fun getChangedWordsSince(lastSyncedAt: Long): List<FirebaseWordDto> {
        return try {
            if (!isFirebaseAvailable()) {
                Log.w(TAG, "Firebase is not available on this device/app")
                return emptyList()
            }
            val db = firestore ?: FirebaseFirestore.getInstance()

            Log.d(TAG, "Fetching words from Firestore collection '$COLLECTION_WORDS' (lastSyncedAt = $lastSyncedAt)...")

            val querySnapshot = if (lastSyncedAt > 0) {
                try {
                    db.collection(COLLECTION_WORDS)
                        .whereGreaterThan("updatedAt", lastSyncedAt)
                        .get()
                        .await()
                } catch (e: Exception) {
                    Log.w(TAG, "Query with lastSyncedAt on '$COLLECTION_WORDS' failed: ${e.message}")
                    db.collection(COLLECTION_WORDS).get().await()
                }
            } else {
                db.collection(COLLECTION_WORDS).get().await()
            }

            val dtos = querySnapshot.documents.mapNotNull { doc ->
                parseDocument(doc)
            }

            Log.d(TAG, "Parsed ${dtos.size} FirebaseWordDto objects from '$COLLECTION_WORDS'")
            dtos
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching words from Firestore", e)
            emptyList()
        }
    }

    override suspend fun getAllWords(): List<FirebaseWordDto> {
        return getChangedWordsSince(0L)
    }

    private fun parseDocument(doc: DocumentSnapshot): FirebaseWordDto? {
        return try {
            val data = doc.data ?: return null
            val id = (data["id"] as? String)?.takeIf { it.isNotBlank() } ?: doc.id
            var word = (data["word"] as? String)?.trim() ?: ""
            val definition = (data["definition"] as? String)?.trim() ?: ""

            // Fallback for word if missing
            if (word.isBlank()) {
                word = (data["simpleMeaning"] as? String)?.split(" ")?.firstOrNull()?.replace(Regex("[^a-zA-Z]"), "")?.uppercase()
                    ?: "RESILIENCE"
            }

            val pronunciation = data["pronunciation"] as? String
            val partOfSpeech = data["partOfSpeech"] as? String
            val simpleMeaning = data["simpleMeaning"] as? String
            val example = data["example"] as? String
            val synonyms = data["synonyms"]

            var difficultyRaw = (data["difficulty"] as? String) ?: "INTERMEDIATE"
            if (difficultyRaw.contains("BEGINNER", ignoreCase = true)) difficultyRaw = "BEGINNER"
            else if (difficultyRaw.contains("ADVANCED", ignoreCase = true)) difficultyRaw = "ADVANCED"
            else difficultyRaw = "INTERMEDIATE"

            val category = data["category"] as? String
            val audioUrl = data["audioUrl"] as? String
            val active = (data["active"] as? Boolean) ?: true
            val version = (data["version"] as? Number)?.toLong() ?: 1L

            val updatedAtRaw = data["updatedAt"]
            val updatedAt = when (updatedAtRaw) {
                is Number -> updatedAtRaw.toLong()
                is Timestamp -> updatedAtRaw.seconds * 1000
                is String -> updatedAtRaw.toLongOrNull() ?: 0L
                else -> 0L
            }

            FirebaseWordDto(
                id = id,
                word = word,
                pronunciation = pronunciation,
                partOfSpeech = partOfSpeech,
                definition = definition,
                simpleMeaning = simpleMeaning,
                example = example,
                synonyms = synonyms,
                difficulty = difficultyRaw,
                category = category,
                audioUrl = audioUrl,
                active = active,
                version = version,
                updatedAt = updatedAt
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse document ${doc.id}", e)
            null
        }
    }

    private fun isFirebaseAvailable(): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseApp check failed", e)
            false
        }
    }
}
