package com.diws.wordzip.domain.usecase

import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.model.WordDifficulty
import com.diws.wordzip.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class FakeWordRepository : WordRepository {
    private val words = mutableListOf(
        Word(
            id = "w1",
            word = "EPHEMERAL",
            definition = "Lasting for a very short period of time.",
            difficulty = WordDifficulty.ADVANCED,
            isLearned = false
        ),
        Word(
            id = "w2",
            word = "RESILIENT",
            definition = "Able to bounce back.",
            difficulty = WordDifficulty.INTERMEDIATE,
            isLearned = true
        )
    )

    override fun getAllWords(): Flow<List<Word>> = flowOf(words)
    override fun getHomeWords(): Flow<List<Word>> = flowOf(words)
    override suspend fun getWordById(id: String): Word? = words.find { it.id == id }
    override fun getWordFlow(id: String): Flow<Word?> = flowOf(words.find { it.id == id })
    override fun searchWords(query: String): Flow<List<Word>> = flowOf(words.filter { it.word.contains(query, ignoreCase = true) })
    override fun getWordsByLearnedStatus(isLearned: Boolean): Flow<List<Word>> = flowOf(words.filter { it.isLearned == isLearned })
    override fun getWordsByDifficulty(difficulty: WordDifficulty): Flow<List<Word>> = flowOf(words.filter { it.difficulty == difficulty })
    override fun getWordOfTheDay(): Flow<Word?> = flowOf(words.firstOrNull())
    override suspend fun getUnlearnedWordsForNotification(limit: Int): List<Word> = words.filter { !it.isLearned }.take(limit)
    override suspend fun getRandomWord(): Word? = words.randomOrNull()
    override fun getLearnedCount(): Flow<Int> = flowOf(words.count { it.isLearned })
    override suspend fun getTotalCount(): Int = words.size
    override suspend fun setLearnedStatus(id: String, isLearned: Boolean) {
        val idx = words.indexOfFirst { it.id == id }
        if (idx != -1) {
            words[idx] = words[idx].copy(isLearned = isLearned)
        }
    }
    override suspend fun recordWordShown(id: String) {
        val idx = words.indexOfFirst { it.id == id }
        if (idx != -1) {
            words[idx] = words[idx].copy(timesShown = words[idx].timesShown + 1, lastShownAt = System.currentTimeMillis())
        }
    }
    override suspend fun refreshWordFromApi(wordId: String): Result<Word> {
        val word = getWordById(wordId) ?: return Result.failure(Exception("Not found"))
        return Result.success(word)
    }
    override suspend fun syncVocabularyWithRemote(): Result<Int> = Result.success(0)
    override suspend fun seedInitialDataIfNeeded() {}
}

class GetNextWordUseCaseTest {

    private lateinit var repository: FakeWordRepository
    private lateinit var useCase: GetNextWordUseCase

    @Before
    fun setUp() {
        repository = FakeWordRepository()
        useCase = GetNextWordUseCase(repository)
    }

    @Test
    fun getNextWord_returnsUnlearnedWord() = runTest {
        val word = useCase()
        assertNotNull(word)
        assertEquals("w1", word?.id)
        assertEquals(false, word?.isLearned)
    }
}
