package com.diws.wordzip.domain.repository

import com.diws.wordzip.domain.model.Proverb
import kotlinx.coroutines.flow.Flow

interface ProverbRepository {
    fun getAllProverbs(): Flow<List<Proverb>>
    fun getProverbFlow(id: String): Flow<Proverb?>
    suspend fun getProverbById(id: String): Proverb?
    fun getFavoriteProverbs(): Flow<List<Proverb>>
    fun getProverbsByCategory(category: String): Flow<List<Proverb>>
    fun searchProverbs(query: String): Flow<List<Proverb>>
    suspend fun getDailyProverb(): Proverb?
    fun getDailyProverbFlow(): Flow<Proverb?>
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)
    suspend fun syncProverbsWithRemote(): Result<Int>
}
