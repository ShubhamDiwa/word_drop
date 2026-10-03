package com.diws.wordzip.domain.usecase

import com.diws.wordzip.domain.repository.ProverbRepository
import javax.inject.Inject

class ToggleProverbFavoriteUseCase @Inject constructor(
    private val proverbRepository: ProverbRepository
) {
    suspend operator fun invoke(id: String, isFavorite: Boolean) {
        proverbRepository.toggleFavorite(id, isFavorite)
    }
}
