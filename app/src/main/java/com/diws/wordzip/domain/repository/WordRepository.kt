package com.diws.wordzip.domain.repository

import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.model.WordDifficulty
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllWords(): Flow<List<Word>>
    fun getHomeWords(): Flow<List<Word>>   // lean query — unlearned first, max 20
    suspend fun getWordById(id: String): Word?
    fun getWordFlow(id: String): Flow<Word?>
    fun searchWords(query: String): Flow<List<Word>>
    fun getWordsByLearnedStatus(isLearned: Boolean): Flow<List<Word>>
    fun getWordsByDifficulty(difficulty: WordDifficulty): Flow<List<Word>>
    fun getWordOfTheDay(): Flow<Word?>
    suspend fun getUnlearnedWordsForNotification(limit: Int): List<Word>
    suspend fun getRandomWord(): Word?
    fun getLearnedCount(): Flow<Int>
    suspend fun getTotalCount(): Int
    suspend fun setLearnedStatus(id: String, isLearned: Boolean)
    suspend fun recordWordShown(id: String)
    suspend fun refreshWordFromApi(wordId: String): Result<Word>
    suspend fun syncVocabularyWithRemote(): Result<Int>
    suspend fun seedInitialDataIfNeeded()
}
