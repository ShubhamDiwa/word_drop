package com.diws.wordzip.data.repository

import android.util.Log
import com.diws.wordzip.data.local.DailyWordEntity
import com.diws.wordzip.data.local.InitialSeedData
import com.diws.wordzip.data.local.WordDao
import com.diws.wordzip.data.local.WordEntity
import com.diws.wordzip.data.preferences.UserPreferencesRepository
import com.diws.wordzip.data.remote.DictionaryApi
import com.diws.wordzip.data.remote.firebase.VocabularyRemoteDataSource
import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.model.WordDifficulty
import com.diws.wordzip.domain.repository.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
    private val userPreferencesRepository: UserPreferencesRepository? = null
) : WordRepository {

    override fun getAllWords(): Flow<List<Word>> {
        return wordDao.getAllWords().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getHomeWords(): Flow<List<Word>> {
        return wordDao.getHomeWords().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getWordById(id: String): Word? {
        return wordDao.getWordById(id)?.toDomainModel()
    }

    override fun getWordFlow(id: String): Flow<Word?> {
        return wordDao.getWordByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun searchWords(query: String): Flow<List<Word>> {
        return wordDao.searchWords(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getWordsByLearnedStatus(isLearned: Boolean): Flow<List<Word>> {
        return wordDao.getWordsByLearnedStatus(isLearned).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getWordsByDifficulty(difficulty: WordDifficulty): Flow<List<Word>> {
        return wordDao.getWordsByDifficulty(difficulty.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getWordOfTheDay(): Flow<Word?> = flow {
        // Note: seeding is handled by Application.onCreate — do NOT call it here
        // to avoid blocking the first UI emit behind a Firebase network round-trip.
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

        val initialWord = dailyEntity?.let { wordDao.getWordById(it.wordId)?.toDomainModel() }
        emit(initialWord)

        wordDao.getDailyWord(today).collect { updatedDaily ->
            val updatedWord = updatedDaily?.let { wordDao.getWordById(it.wordId)?.toDomainModel() }
            emit(updatedWord)
        }
    }

    override suspend fun getUnlearnedWordsForNotification(limit: Int): List<Word> {
        val entities = wordDao.getUnlearnedWordsForNotification(limit)
        return if (entities.isNotEmpty()) {
            entities.map { it.toDomainModel() }
        } else {
            listOfNotNull(wordDao.getRandomWord()?.toDomainModel())
        }
    }

    override suspend fun getRandomWord(): Word? {
        return wordDao.getRandomWord()?.toDomainModel()
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
            val entry = response.firstOrNull() ?: return Result.success(entity.toDomainModel())

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
            Result.success(updatedEntity.toDomainModel())
        } catch (e: Exception) {
            val localEntity = wordDao.getWordById(wordId)
            if (localEntity != null) {
                Result.success(localEntity.toDomainModel())
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun syncVocabularyWithRemote(): Result<Int> {
        return try {
            val remoteDataSource = vocabularyRemoteDataSource ?: return Result.success(0)
            val changedDtos = remoteDataSource.getChangedWordsSince(0L) // ALWAYS FULL SYNC FOR NOW

            if (changedDtos.isEmpty()) {
                Log.d("FirebaseSync", "No changed words found on Firebase")
                return Result.success(0)
            }

            val validDtos = changedDtos.filter { it.isValid() }
            val activeDtos = validDtos.filter { it.active }
            val inactiveIds = validDtos.filter { !it.active }.map { it.id }

            if (activeDtos.isNotEmpty()) {
                val entitiesToInsert = activeDtos.map { dto ->
                    val existingEntity = wordDao.getWordById(dto.id)
                    dto.toEntity(existingEntity)
                }
                wordDao.insertWords(entitiesToInsert)
            }

            if (inactiveIds.isNotEmpty()) {
                wordDao.deleteWordsByIds(inactiveIds)
            }

            val maxUpdatedAt = validDtos.maxOfOrNull { it.updatedAt } ?: 0L
            if (maxUpdatedAt > 0) {
                userPreferencesRepository?.updateLastVocabularySyncTimestamp(maxUpdatedAt)
            }

            Log.d("FirebaseSync", "Successfully synced ${validDtos.size} words from Firebase into Room")
            Result.success(validDtos.size)
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
        // Run Firebase sync in the background so the UI never waits for a
        // network round-trip before showing locally-available words.
        CoroutineScope(Dispatchers.IO).launch {
            syncVocabularyWithRemote()
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
}
