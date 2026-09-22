package com.diws.worddrop.data.repository

import com.diws.worddrop.data.local.DailyWordEntity
import com.diws.worddrop.data.local.WordDao
import com.diws.worddrop.data.local.WordEntity
import com.diws.worddrop.data.remote.DictionaryApi
import com.diws.worddrop.data.remote.DictionaryEntryDto
import com.diws.worddrop.data.remote.firebase.FirebaseWordDto
import com.diws.worddrop.data.remote.firebase.VocabularyRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordRepositorySyncTest {

    private class FakeWordDao : WordDao {
        val insertedWords = mutableListOf<WordEntity>()
        val deletedIds = mutableListOf<String>()

        override fun getAllWords(): Flow<List<WordEntity>> = flowOf(emptyList())
        override fun getHomeWords(): Flow<List<WordEntity>> = flowOf(emptyList())
        override suspend fun getWordById(id: String): WordEntity? = null
        override fun getWordByIdFlow(id: String): Flow<WordEntity?> = flowOf(null)
        override fun searchWords(query: String): Flow<List<WordEntity>> = flowOf(emptyList())
        override fun getWordsByLearnedStatus(isLearned: Boolean): Flow<List<WordEntity>> = flowOf(emptyList())
        override fun getWordsByDifficulty(difficulty: String): Flow<List<WordEntity>> = flowOf(emptyList())
        override fun getDailyWord(date: String): Flow<DailyWordEntity?> = flowOf(null)
        override suspend fun getDailyWordSync(date: String): DailyWordEntity? = null
        override suspend fun insertDailyWord(dailyWord: DailyWordEntity) {}
        override suspend fun getRecentDailyWords(limit: Int): List<DailyWordEntity> = emptyList()
        override suspend fun getUnlearnedWordsForNotification(limit: Int): List<WordEntity> = emptyList()
        override suspend fun getRandomWord(): WordEntity? = null
        override suspend fun getCount(): Int = 0
        override fun getLearnedCount(): Flow<Int> = flowOf(0)
        override suspend fun getLearnedCountSync(): Int = 0
        override suspend fun insertWords(words: List<WordEntity>) {
            insertedWords.addAll(words)
        }
        override suspend fun deleteWordsByIds(ids: List<String>) {
            deletedIds.addAll(ids)
        }
        override suspend fun insertWord(word: WordEntity) {}
        override suspend fun updateWord(word: WordEntity) {}
        override suspend fun updateLearnedStatus(id: String, isLearned: Boolean) {}
        override suspend fun updateWordShownStats(id: String, timestamp: Long) {}
    }

    private class FakeVocabularyRemoteDataSource(
        val dtosToReturn: List<FirebaseWordDto>
    ) : VocabularyRemoteDataSource {
        override suspend fun getChangedWordsSince(lastSyncedAt: Long): List<FirebaseWordDto> = dtosToReturn
        override suspend fun getAllWords(): List<FirebaseWordDto> = dtosToReturn
    }

    private class FakeDictionaryApi : DictionaryApi {
        override suspend fun getWordDefinition(word: String) = emptyList<DictionaryEntryDto>()
    }

    @Test
    fun `syncVocabularyWithRemote inserts active words and deletes inactive words`() = runTest {
        val dtos = listOf(
            FirebaseWordDto(
                id = "word_1",
                word = "Active Word",
                definition = "Active Def",
                active = true,
                updatedAt = 100L
            ),
            FirebaseWordDto(
                id = "word_2",
                word = "Inactive Word",
                definition = "Inactive Def",
                active = false,
                updatedAt = 100L
            )
        )

        val fakeDao = FakeWordDao()
        val fakeRemote = FakeVocabularyRemoteDataSource(dtos)
        val repository = WordRepositoryImpl(
            wordDao = fakeDao,
            dictionaryApi = FakeDictionaryApi(),
            vocabularyRemoteDataSource = fakeRemote,
            userPreferencesRepository = null
        )

        val result = repository.syncVocabularyWithRemote()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull())
        assertEquals(1, fakeDao.insertedWords.size)
        assertEquals("word_1", fakeDao.insertedWords[0].id)
        assertEquals(listOf("word_2"), fakeDao.deletedIds)
    }

    @Test
    fun `syncVocabularyWithRemote does nothing when no changed words returned`() = runTest {
        val fakeDao = FakeWordDao()
        val fakeRemote = FakeVocabularyRemoteDataSource(emptyList())
        val repository = WordRepositoryImpl(
            wordDao = fakeDao,
            dictionaryApi = FakeDictionaryApi(),
            vocabularyRemoteDataSource = fakeRemote,
            userPreferencesRepository = null
        )

        val result = repository.syncVocabularyWithRemote()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull())
        assertTrue(fakeDao.insertedWords.isEmpty())
        assertTrue(fakeDao.deletedIds.isEmpty())
    }
}
