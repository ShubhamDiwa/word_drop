package com.diws.wordzip.domain.usecase

import com.diws.wordzip.domain.model.Proverb
import com.diws.wordzip.domain.repository.ProverbRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyProverbUseCase @Inject constructor(
    private val proverbRepository: ProverbRepository
) {
    operator fun invoke(): Flow<Proverb?> {
        return proverbRepository.getDailyProverbFlow()
    }

    suspend fun getDaily(): Proverb? {
        return proverbRepository.getDailyProverb()
    }
}
