package com.diws.wordzip.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProverbDao {
    @Query("SELECT * FROM proverbs ORDER BY englishText ASC")
    fun getAllProverbs(): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE id = :id LIMIT 1")
    suspend fun getProverbById(id: String): ProverbEntity?

    @Query("SELECT * FROM proverbs WHERE id = :id LIMIT 1")
    fun getProverbByIdFlow(id: String): Flow<ProverbEntity?>

    @Query("SELECT * FROM proverbs WHERE isFavorite = 1 ORDER BY englishText ASC")
    fun getFavoriteProverbs(): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE category = :category ORDER BY englishText ASC")
    fun getProverbsByCategory(category: String): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE englishText LIKE '%' || :query || '%' OR hindiText LIKE '%' || :query || '%' OR meaningEnglish LIKE '%' || :query || '%' OR meaningHindi LIKE '%' || :query || '%' OR (hindiEquivalent IS NOT NULL AND hindiEquivalent LIKE '%' || :query || '%')")
    fun searchProverbs(query: String): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM daily_proverbs WHERE date = :date LIMIT 1")
    suspend fun getDailyProverb(date: String): DailyProverbEntity?

    @Query("SELECT * FROM daily_proverbs WHERE date = :date LIMIT 1")
    fun getDailyProverbFlow(date: String): Flow<DailyProverbEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyProverb(dailyProverb: DailyProverbEntity)

    @Query("SELECT * FROM proverbs ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomProverb(): ProverbEntity?

    @Query("SELECT COUNT(*) FROM proverbs")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProverbs(proverbs: List<ProverbEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProverb(proverb: ProverbEntity)

    @Query("UPDATE proverbs SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("UPDATE proverbs SET timesShown = timesShown + 1, lastShownAt = :timestamp WHERE id = :id")
    suspend fun updateProverbShownStats(id: String, timestamp: Long)

    @Query("DELETE FROM proverbs WHERE id IN (:ids)")
    suspend fun deleteProverbsByIds(ids: List<String>)
}
