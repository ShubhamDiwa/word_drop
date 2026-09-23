package com.diws.wordzip.domain.usecase

import com.diws.wordzip.domain.repository.WordRepository
import javax.inject.Inject

class MarkWordLearnedUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(wordId: String, isLearned: Boolean = true) {
        repository.setLearnedStatus(wordId, isLearned)
    }
}
