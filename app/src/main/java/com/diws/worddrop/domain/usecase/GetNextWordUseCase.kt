package com.diws.worddrop.domain.usecase

import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.domain.repository.WordRepository
import javax.inject.Inject

class GetNextWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(): Word? {
        val unlearned = repository.getUnlearnedWordsForNotification(1)
        return unlearned.firstOrNull() ?: repository.getRandomWord()
    }
}
