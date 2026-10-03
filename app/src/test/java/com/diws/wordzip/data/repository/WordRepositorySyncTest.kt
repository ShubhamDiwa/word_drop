package com.diws.wordzip.data.repository

import com.diws.wordzip.data.local.DailyWordEntity
import com.diws.wordzip.data.local.WordDao
import com.diws.wordzip.data.local.WordEntity
import com.diws.wordzip.data.remote.DictionaryApi
import com.diws.wordzip.data.remote.DictionaryEntryDto
import com.diws.wordzip.data.remote.firebase.FirebaseWordDto
import com.diws.wordzip.data.remote.firebase.VocabularyRemoteDataSource
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

        val insertedTranslations = mutableListOf<com.diws.wordzip.data.local.WordTranslationEntity>()
        override suspend fun insertTranslations(translations: List<com.diws.wordzip.data.local.WordTranslationEntity>) {
            insertedTranslations.addAll(translations)
        }
        override suspend fun insertTranslation(translation: com.diws.wordzip.data.local.WordTranslationEntity) {
            insertedTranslations.add(translation)
        }
        override fun getTranslationsForWord(wordId: String): Flow<List<com.diws.wordzip.data.local.WordTranslationEntity>> = flowOf(emptyList())
        override suspend fun getTranslationsForWordSync(wordId: String): List<com.diws.wordzip.data.local.WordTranslationEntity> = emptyList()
        override fun getAllTranslations(): Flow<List<com.diws.wordzip.data.local.WordTranslationEntity>> = flowOf(emptyList())
        override suspend fun getAllTranslationsSync(): List<com.diws.wordzip.data.local.WordTranslationEntity> = emptyList()
        override suspend fun getTranslationsForWords(wordIds: List<String>): List<com.diws.wordzip.data.local.WordTranslationEntity> = emptyList()
        override suspend fun deleteTranslationsForWord(wordId: String) {}
        override suspend fun deleteTranslationsForWords(wordIds: List<String>) {}
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
        assertEquals(1, result.getOrNull())
        assertEquals(1, fakeDao.insertedWords.size)
        assertEquals("word_1", fakeDao.insertedWords[0].id)
        assertEquals(listOf("word_2"), fakeDao.deletedIds)
    }

    @Test
    fun `syncVocabularyWithRemote syncs all 350 words into Room without artificial limit`() = runTest {
        val dtos = (1..350).map { i ->
            FirebaseWordDto(
                id = "word_$i",
                word = "Word $i",
                definition = "Definition for word $i",
                active = true,
                updatedAt = i.toLong()
            )
        }

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
        assertEquals(350, result.getOrNull())
        assertEquals(350, fakeDao.insertedWords.size)
        assertEquals("word_1", fakeDao.insertedWords.first().id)
        assertEquals("word_350", fakeDao.insertedWords.last().id)
    }

    @Test
    fun `sync handles duplicate and invalid entries without dropping valid entries`() = runTest {
        val dtos = listOf(
            FirebaseWordDto(id = "w1", word = "Valid Word 1", definition = "Def 1", active = true),
            FirebaseWordDto(id = "", word = "No ID", definition = "Def", active = true), // Invalid
            FirebaseWordDto(id = "w2", word = "", definition = "Def", active = true), // Invalid
            FirebaseWordDto(id = "w3", word = "Valid Word 3", definition = "", simpleMeaning = "Simple meaning", active = true), // Valid fallback
            FirebaseWordDto(id = "w4", word = "Inactive Word", definition = "Def 4", active = false) // Inactive
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
        assertEquals(2, fakeDao.insertedWords.size)
        assertEquals(listOf("w1", "w3"), fakeDao.insertedWords.map { it.id })
        assertEquals(listOf("w4"), fakeDao.deletedIds)
    }

    @Test
    fun `home query stays limited while vocabulary queries full dataset`() = runTest {
        val words = (1..350).map { i ->
            WordEntity(
                id = "word_$i",
                word = "Word $i",
                pronunciation = "/w$i/",
                partOfSpeech = "NOUN",
                definition = "Definition $i",
                simpleMeaning = "Simple $i",
                example = "Example $i",
                synonyms = "syn1,syn2",
                difficulty = "INTERMEDIATE",
                category = "General",
                audioUrl = null
            )
        }

        val inMemoryDao = object : WordDao {
            val db = words.toMutableList()
            override fun getAllWords(): Flow<List<WordEntity>> = flowOf(db)
            override fun getHomeWords(): Flow<List<WordEntity>> = flowOf(db.take(20))
            override suspend fun getWordById(id: String) = db.find { it.id == id }
            override fun getWordByIdFlow(id: String) = flowOf(db.find { it.id == id })
            override fun searchWords(query: String) = flowOf(db.filter { it.word.contains(query) })
            override fun getWordsByLearnedStatus(isLearned: Boolean) = flowOf(db.filter { it.isLearned == isLearned })
            override fun getWordsByDifficulty(difficulty: String) = flowOf(db.filter { it.difficulty == difficulty })
            override fun getDailyWord(date: String) = flowOf(null)
            override suspend fun getDailyWordSync(date: String) = null
            override suspend fun insertDailyWord(dailyWord: DailyWordEntity) {}
            override suspend fun getRecentDailyWords(limit: Int) = emptyList<DailyWordEntity>()
            override suspend fun getUnlearnedWordsForNotification(limit: Int) = db.take(limit)
            override suspend fun getRandomWord() = db.firstOrNull()
            override suspend fun getCount() = db.size
            override fun getLearnedCount() = flowOf(0)
            override suspend fun getLearnedCountSync() = 0
            override suspend fun insertWords(words: List<WordEntity>) { db.addAll(words) }
            override suspend fun deleteWordsByIds(ids: List<String>) { db.removeAll { it.id in ids } }
            override suspend fun insertWord(word: WordEntity) { db.add(word) }
            override suspend fun updateWord(word: WordEntity) {}
            override suspend fun updateLearnedStatus(id: String, isLearned: Boolean) {}
            override suspend fun updateWordShownStats(id: String, timestamp: Long) {}
            override suspend fun insertTranslations(translations: List<com.diws.wordzip.data.local.WordTranslationEntity>) {}
            override suspend fun insertTranslation(translation: com.diws.wordzip.data.local.WordTranslationEntity) {}
            override fun getTranslationsForWord(wordId: String) = flowOf(emptyList<com.diws.wordzip.data.local.WordTranslationEntity>())
            override suspend fun getTranslationsForWordSync(wordId: String) = emptyList<com.diws.wordzip.data.local.WordTranslationEntity>()
            override fun getAllTranslations() = flowOf(emptyList<com.diws.wordzip.data.local.WordTranslationEntity>())
            override suspend fun getAllTranslationsSync() = emptyList<com.diws.wordzip.data.local.WordTranslationEntity>()
            override suspend fun getTranslationsForWords(wordIds: List<String>) = emptyList<com.diws.wordzip.data.local.WordTranslationEntity>()
            override suspend fun deleteTranslationsForWord(wordId: String) {}
            override suspend fun deleteTranslationsForWords(wordIds: List<String>) {}
        }

        val repository = WordRepositoryImpl(
            wordDao = inMemoryDao,
            dictionaryApi = FakeDictionaryApi()
        )

        // Home should have exactly 20
        var homeCount = 0
        repository.getHomeWords().collect { homeWords ->
            homeCount = homeWords.size
        }
        assertEquals(20, homeCount)

        // Full vocabulary should have all 350
        var fullCount = 0
        repository.getAllWords().collect { allWords ->
            fullCount = allWords.size
        }
        assertEquals(350, fullCount)
    }
}
