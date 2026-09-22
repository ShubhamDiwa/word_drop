package com.diws.worddrop.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords(): Flow<List<WordEntity>>

    // Lean query for home screen: unlearned first, cap at 20 to avoid loading the whole DB
    @Query("SELECT * FROM words ORDER BY isLearned ASC, timesShown ASC LIMIT 20")
    fun getHomeWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: String): WordEntity?

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    fun getWordByIdFlow(id: String): Flow<WordEntity?>

    @Query("SELECT * FROM words WHERE word LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%'")
    fun searchWords(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isLearned = :isLearned ORDER BY word ASC")
    fun getWordsByLearnedStatus(isLearned: Boolean): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE difficulty = :difficulty ORDER BY word ASC")
    fun getWordsByDifficulty(difficulty: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM daily_words WHERE date = :date LIMIT 1")
    fun getDailyWord(date: String): Flow<DailyWordEntity?>

    @Query("SELECT * FROM daily_words WHERE date = :date LIMIT 1")
    suspend fun getDailyWordSync(date: String): DailyWordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWord(dailyWord: DailyWordEntity)

    @Query("SELECT * FROM daily_words ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentDailyWords(limit: Int): List<DailyWordEntity>

    @Query("SELECT * FROM words WHERE isLearned = 0 ORDER BY timesShown ASC, RANDOM() LIMIT :limit")
    suspend fun getUnlearnedWordsForNotification(limit: Int): List<WordEntity>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): WordEntity?

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM words WHERE isLearned = 1")
    fun getLearnedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isLearned = 1")
    suspend fun getLearnedCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("DELETE FROM words WHERE id IN (:ids)")
    suspend fun deleteWordsByIds(ids: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("UPDATE words SET isLearned = :isLearned WHERE id = :id")
    suspend fun updateLearnedStatus(id: String, isLearned: Boolean)

    @Query("UPDATE words SET timesShown = timesShown + 1, lastShownAt = :timestamp WHERE id = :id")
    suspend fun updateWordShownStats(id: String, timestamp: Long)
}
