package com.diws.wordzip.domain.usecase

import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    operator fun invoke(wordId: String): Flow<Word?> {
        return repository.getWordFlow(wordId)
    }

    suspend fun refresh(wordId: String) {
        repository.refreshWordFromApi(wordId)
    }
}
