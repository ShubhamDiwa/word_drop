package com.diws.worddrop.domain.usecase

import com.diws.worddrop.data.preferences.UserPreferencesRepository
import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.domain.model.WordDifficulty
import com.diws.worddrop.domain.repository.WordRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SelectNextWordForNotificationUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(isScheduledDelivery: Boolean = false): Word? {
        val settings = preferencesRepository.userSettingsFlow.first()
        val allWords = wordRepository.getAllWords().first()
        if (allWords.isEmpty()) return null

        val filteredWords = if (settings.difficultyFilter != "MIXED") {
            val targetDifficulty = WordDifficulty.fromString(settings.difficultyFilter)
            val matched = allWords.filter { it.difficulty == targetDifficulty }
            if (matched.isNotEmpty()) matched else allWords
        } else {
            allWords
        }

        val sortedWords = filteredWords.sortedWith(
            compareBy<Word> { it.isLearned }
                .thenBy { it.timesShown > 0 }
                .thenBy { it.lastShownAt ?: 0L }
        )

        val selectedWord = if (sortedWords.isNotEmpty()) {
            val topWord = sortedWords.first()
            val scoreKey: (Word) -> Triple<Boolean, Boolean, Long> = { w ->
                Triple(w.isLearned, w.timesShown > 0, w.lastShownAt ?: 0L)
            }
            val bestScore = scoreKey(topWord)
            val tiedWords = sortedWords.filter { scoreKey(it) == bestScore }
            tiedWords.random()
        } else {
            filteredWords.randomOrNull() ?: allWords.randomOrNull()
        }

        if (selectedWord != null && isScheduledDelivery) {
            wordRepository.recordWordShown(selectedWord.id)
        }

        return selectedWord
    }
}
