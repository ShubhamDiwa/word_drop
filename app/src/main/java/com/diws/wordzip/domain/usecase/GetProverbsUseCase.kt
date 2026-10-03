package com.diws.wordzip.domain.usecase

import com.diws.wordzip.domain.model.Proverb
import com.diws.wordzip.domain.repository.ProverbRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProverbsUseCase @Inject constructor(
    private val proverbRepository: ProverbRepository
) {
    operator fun invoke(): Flow<List<Proverb>> {
        return proverbRepository.getAllProverbs()
    }

    fun getByCategory(category: String): Flow<List<Proverb>> {
        return proverbRepository.getProverbsByCategory(category)
    }

    fun getFavorites(): Flow<List<Proverb>> {
        return proverbRepository.getFavoriteProverbs()
    }

    fun search(query: String): Flow<List<Proverb>> {
        return proverbRepository.searchProverbs(query)
    }

    fun getById(id: String): Flow<Proverb?> {
        return proverbRepository.getProverbFlow(id)
    }
}
