package com.diws.wordzip.data.repository

import android.util.Log
import com.diws.wordzip.data.local.DailyWordEntity
import com.diws.wordzip.data.local.InitialSeedData
import com.diws.wordzip.data.local.WordDao
import com.diws.wordzip.data.local.WordEntity
import com.diws.wordzip.data.local.WordTranslationEntity
import com.diws.wordzip.data.preferences.UserPreferencesRepository
import com.diws.wordzip.data.remote.DictionaryApi
import com.diws.wordzip.data.remote.firebase.VocabularyRemoteDataSource
import com.diws.wordzip.data.remote.translation.TranslationDataSource
import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.model.WordDifficulty
import com.diws.wordzip.domain.repository.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WordRepositoryImpl(
    private val wordDao: WordDao,
    private val dictionaryApi: DictionaryApi,
    private val vocabularyRemoteDataSource: VocabularyRemoteDataSource? = null,
    private val userPreferencesRepository: UserPreferencesRepository? = null,
    private val translationDataSource: TranslationDataSource? = null
) : WordRepository {

    override fun getAllWords(): Flow<List<Word>> {
        return combine(wordDao.getAllWords(), wordDao.getAllTranslations()) { entities, translations ->
            val translationMap = translations.groupBy { it.wordId }
                .mapValues { (_, list) -> list.associate { it.languageCode to it.meaning } }
            entities.map { it.toDomainModel(translationMap[it.id] ?: emptyMap()) }
        }
    }

    override fun getHomeWords(): Flow<List<Word>> {
        return combine(wordDao.getHomeWords(), wordDao.getAllTranslations()) { entities, translations ->
            val translationMap = translations.groupBy { it.wordId }
                .mapValues { (_, list) -> list.associate { it.languageCode to it.meaning } }
            entities.map { it.toDomainModel(translationMap[it.id] ?: emptyMap()) }
        }
    }

    override suspend fun getWordById(id: String): Word? {
        val entity = wordDao.getWordById(id) ?: return null
        val translations = wordDao.getTranslationsForWordSync(id).associate { it.languageCode to it.meaning }
        return entity.toDomainModel(translations)
    }

    override fun getWordFlow(id: String): Flow<Word?> {
        return combine(wordDao.getWordByIdFlow(id), wordDao.getTranslationsForWord(id)) { entity, translations ->
            entity?.toDomainModel(translations.associate { it.languageCode to it.meaning })
        }
    }

    override fun searchWords(query: String): Flow<List<Word>> {
        return combine(wordDao.searchWords(query), wordDao.getAllTranslations()) { entities, translations ->
            val translationMap = translations.groupBy { it.wordId }
                .mapValues { (_, list) -> list.associate { it.languageCode to it.meaning } }
            entities.map { it.toDomainModel(translationMap[it.id] ?: emptyMap()) }
        }
    }

    override fun getWordsByLearnedStatus(isLearned: Boolean): Flow<List<Word>> {
        return combine(wordDao.getWordsByLearnedStatus(isLearned), wordDao.getAllTranslations()) { entities, translations ->
            val translationMap = translations.groupBy { it.wordId }
                .mapValues { (_, list) -> list.associate { it.languageCode to it.meaning } }
            entities.map { it.toDomainModel(translationMap[it.id] ?: emptyMap()) }
        }
    }

    override fun getWordsByDifficulty(difficulty: WordDifficulty): Flow<List<Word>> {
        return combine(wordDao.getWordsByDifficulty(difficulty.name), wordDao.getAllTranslations()) { entities, translations ->
            val translationMap = translations.groupBy { it.wordId }
                .mapValues { (_, list) -> list.associate { it.languageCode to it.meaning } }
            entities.map { it.toDomainModel(translationMap[it.id] ?: emptyMap()) }
        }
    }

    override fun getWordOfTheDay(): Flow<Word?> = flow {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var dailyEntity = wordDao.getDailyWordSync(today)
        if (dailyEntity == null) {
            val allEntities = wordDao.getAllWords().first()
            val recentDailyWords = wordDao.getRecentDailyWords(3)
            val recentWordIds = recentDailyWords.map { it.wordId }.toSet()

            val available = allEntities.filter { it.id !in recentWordIds }
            val candidate = if (available.isNotEmpty()) available.random() else allEntities.randomOrNull()

            if (candidate != null) {
                val newDaily = DailyWordEntity(date = today, wordId = candidate.id)
                wordDao.insertDailyWord(newDaily)
                dailyEntity = newDaily
            }
        }

        val initialWord = dailyEntity?.let { getWordById(it.wordId) }
        emit(initialWord)

        wordDao.getDailyWord(today).collect { updatedDaily ->
            val updatedWord = updatedDaily?.let { getWordById(it.wordId) }
            emit(updatedWord)
        }
    }

    override suspend fun getUnlearnedWordsForNotification(limit: Int): List<Word> {
        val entities = wordDao.getUnlearnedWordsForNotification(limit)
        val chosenEntities = if (entities.isNotEmpty()) {
            entities
        } else {
            listOfNotNull(wordDao.getRandomWord())
        }

        val wordIds = chosenEntities.map { it.id }
        val translations = wordDao.getTranslationsForWords(wordIds)
        val translationMap = translations.groupBy { it.wordId }
            .mapValues { (_, list) -> list.associate { it.languageCode to it.meaning } }

        return chosenEntities.map { it.toDomainModel(translationMap[it.id] ?: emptyMap()) }
    }

    override suspend fun getRandomWord(): Word? {
        val entity = wordDao.getRandomWord() ?: return null
        val translations = wordDao.getTranslationsForWordSync(entity.id).associate { it.languageCode to it.meaning }
        return entity.toDomainModel(translations)
    }

    override fun getLearnedCount(): Flow<Int> {
        return wordDao.getLearnedCount()
    }

    override suspend fun getTotalCount(): Int {
        return wordDao.getCount()
    }

    override suspend fun setLearnedStatus(id: String, isLearned: Boolean) {
        wordDao.updateLearnedStatus(id, isLearned)
    }

    override suspend fun recordWordShown(id: String) {
        wordDao.updateWordShownStats(id, System.currentTimeMillis())
    }

    override suspend fun refreshWordFromApi(wordId: String): Result<Word> {
        return try {
            val entity = wordDao.getWordById(wordId) ?: return Result.failure(Exception("Word not found"))
            val response = dictionaryApi.getWordDefinition(entity.word.lowercase())
            val entry = response.firstOrNull() ?: return Result.success(getWordById(wordId) ?: entity.toDomainModel())

            val audioUrl = entry.phonetics?.firstOrNull { !it.audio.isNull_or_empty() }?.audio ?: entity.audioUrl
            val phoneticText = entry.phonetic ?: entry.phonetics?.firstOrNull { !it.text.isNull_or_empty() }?.text ?: entity.pronunciation
            val meaning = entry.meanings?.firstOrNull()
            val definition = meaning?.definitions?.firstOrNull()?.definition ?: entity.definition
            val example = meaning?.definitions?.firstOrNull()?.example ?: entity.example
            val synonymsList = meaning?.synonyms ?: meaning?.definitions?.flatMap { it.synonyms ?: emptyList() } ?: emptyList()
            val synonymsString = if (synonymsList.isNotEmpty()) synonymsList.take(5).joinToString(", ") else entity.synonyms

            val updatedEntity = entity.copy(
                pronunciation = phoneticText ?: entity.pronunciation,
                partOfSpeech = meaning?.partOfSpeech?.uppercase() ?: entity.partOfSpeech,
                definition = definition,
                example = example ?: entity.example,
                synonyms = synonymsString,
                audioUrl = audioUrl
            )
            wordDao.updateWord(updatedEntity)
            val updatedWord = getWordById(wordId) ?: updatedEntity.toDomainModel()
            Result.success(updatedWord)
        } catch (e: Exception) {
            val localWord = getWordById(wordId)
            if (localWord != null) {
                Result.success(localWord)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun syncVocabularyWithRemote(): Result<Int> {
        return try {
            val remoteDataSource = vocabularyRemoteDataSource ?: return Result.success(0)
            val changedDtos = remoteDataSource.getChangedWordsSince(0L) // ALWAYS FULL SYNC FOR NOW

            Log.d("FirebaseSync", "=== SYNC START ===")
            Log.d("FirebaseSync", "Total documents from Firestore: ${changedDtos.size}")

            if (changedDtos.isEmpty()) {
                Log.w("FirebaseSync", "No words returned from Firebase — check Firestore collection name and rules")
                return Result.success(0)
            }

            val validDtos = changedDtos.filter { it.isValid() }
            val invalidDtos = changedDtos.filter { !it.isValid() }
            val activeDtos = validDtos.filter { it.active }
            val inactiveIds = validDtos.filter { !it.active }.map { it.id }

            Log.d("FirebaseSync", "Valid DTOs (id+word+definition not blank): ${validDtos.size}")
            Log.d("FirebaseSync", "Invalid/dropped DTOs: ${invalidDtos.size}")
            if (invalidDtos.isNotEmpty()) {
                invalidDtos.take(10).forEach { dto ->
                    Log.w("FirebaseSync", "  INVALID: id='${dto.id}' word='${dto.word}' definition='${dto.definition.take(30)}'")
                }
            }
            Log.d("FirebaseSync", "Active DTOs to insert: ${activeDtos.size}")
            Log.d("FirebaseSync", "Inactive DTOs to delete: ${inactiveIds.size}")

            if (activeDtos.isNotEmpty()) {
                val entitiesToInsert = activeDtos.map { dto ->
                    val existingEntity = wordDao.getWordById(dto.id)
                    dto.toEntity(existingEntity)
                }
                wordDao.insertWords(entitiesToInsert)

                val translationsToInsert = activeDtos.flatMap { it.toTranslationEntities() }
                if (translationsToInsert.isNotEmpty()) {
                    wordDao.insertTranslations(translationsToInsert)
                }
                Log.d("FirebaseSync", "Inserted/updated ${entitiesToInsert.size} words into Room")
            }

            if (inactiveIds.isNotEmpty()) {
                wordDao.deleteWordsByIds(inactiveIds)
                wordDao.deleteTranslationsForWords(inactiveIds)
                Log.d("FirebaseSync", "Deleted ${inactiveIds.size} inactive words from Room")
            }

            val totalInRoom = wordDao.getCount()
            Log.d("FirebaseSync", "Room now has $totalInRoom total words")
            Log.d("FirebaseSync", "=== SYNC END ===")

            val maxUpdatedAt = validDtos.maxOfOrNull { it.updatedAt } ?: 0L
            if (maxUpdatedAt > 0) {
                userPreferencesRepository?.updateLastVocabularySyncTimestamp(maxUpdatedAt)
            }

            Result.success(activeDtos.size)
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Failed to sync vocabulary with Firebase", e)
            Result.failure(e)
        }
    }

    override suspend fun seedInitialDataIfNeeded() {
        val count = wordDao.getCount()
        if (count == 0) {
            wordDao.insertWords(InitialSeedData.getInitialWords())
        }
        // Always populate/update seed translations so newly supported Indian languages are immediately present offline
        wordDao.insertTranslations(InitialSeedData.getInitialTranslations())
        CoroutineScope(Dispatchers.IO).launch {
            syncVocabularyWithRemote()
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
}
