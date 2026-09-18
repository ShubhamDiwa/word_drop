package com.diws.worddrop.domain.usecase

import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.domain.model.WordDifficulty
import com.diws.worddrop.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetVocabularyUseCase @Inject constructor(
    private val repository: WordRepository
) {
    operator fun invoke(
        query: String = "",
        difficulty: WordDifficulty? = null,
        onlyLearned: Boolean? = null
    ): Flow<List<Word>> {
        return repository.getAllWords().map { words ->
            words.filter { word ->
                val matchesQuery = query.isBlank() ||
                        word.word.contains(query, ignoreCase = true) ||
                        word.definition.contains(query, ignoreCase = true) ||
                        (word.simpleMeaning?.contains(query, ignoreCase = true) == true)

                val matchesDifficulty = difficulty == null || word.difficulty == difficulty
                val matchesLearned = onlyLearned == null || word.isLearned == onlyLearned

                matchesQuery && matchesDifficulty && matchesLearned
            }
        }
    }
}
