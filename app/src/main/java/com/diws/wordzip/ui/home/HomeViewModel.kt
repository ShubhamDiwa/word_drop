package com.diws.wordzip.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.wordzip.data.preferences.UserPreferencesRepository
import com.diws.wordzip.domain.model.Proverb
import com.diws.wordzip.domain.model.UserLevelInfo
import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.model.XpLevelManager
import com.diws.wordzip.domain.repository.WordRepository
import com.diws.wordzip.domain.usecase.GetDailyProverbUseCase
import com.diws.wordzip.domain.usecase.GetProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val wordOfTheDay: Word? = null,
    val proverbOfTheDay: Proverb? = null,
    val todayWords: List<Word> = emptyList(),
    val streak: Int = 7,
    val streakShields: Int = 0,
    val levelInfo: UserLevelInfo = XpLevelManager.getLevelInfo(0),
    val learnedToday: Int = 3,
    val totalTodayTarget: Int = 5,
    val selectedMeaningLanguages: List<String> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getDailyProverbUseCase: GetDailyProverbUseCase,
    getProgressUseCase: GetProgressUseCase
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getDailyProverbUseCase.getDaily()
            combine(wordRepository.getHomeWords(), userPreferencesRepository.selectedMeaningLanguagesFlow) { words, languages ->
                val targetLang = languages.firstOrNull() ?: ""
                if (targetLang.isNotBlank() && !targetLang.equals("en", ignoreCase = true)) {
                    words.forEach { word ->
                        if (word.translations[targetLang.lowercase()].isNullOrBlank()) {
                            wordRepository.getOrFetchTranslation(word.id, targetLang)
                        }
                    }
                }
            }.collect {}
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        combine(
            wordRepository.getWordOfTheDay(),
            wordRepository.getHomeWords(),
            getProgressUseCase(),
            getDailyProverbUseCase()
        ) { wordOfTheDay, allWords, progress, proverbOfTheDay ->
            Quadruple(wordOfTheDay, allWords, progress, proverbOfTheDay)
        },
        userPreferencesRepository.userXpFlow,
        userPreferencesRepository.streakShieldsFlow,
        userPreferencesRepository.selectedMeaningLanguagesFlow
    ) { (wordOfTheDay, allWords, progress, proverbOfTheDay), userXp, streakShields, selectedLanguages ->

        val unlearnedWords = allWords.filter { !it.isLearned }
        val todaysList = if (unlearnedWords.size >= 10) {
            unlearnedWords.take(10)
        } else {
            (unlearnedWords + allWords.filter { it.isLearned }).take(10).distinctBy { it.id }
        }

        val learnedCount = todaysList.count { it.isLearned }
        HomeUiState(
            isLoading = false,
            wordOfTheDay = wordOfTheDay ?: todaysList.firstOrNull(),
            proverbOfTheDay = proverbOfTheDay,
            todayWords = todaysList,
            streak = progress.streakDays,
            streakShields = streakShields,
            levelInfo = XpLevelManager.getLevelInfo(userXp),
            learnedToday = learnedCount,
            totalTodayTarget = todaysList.size.coerceAtLeast(1),
            selectedMeaningLanguages = selectedLanguages
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun toggleLearned(wordId: String, isLearned: Boolean) {
        viewModelScope.launch {
            wordRepository.setLearnedStatus(wordId, isLearned)
        }
    }

    private data class Quadruple<T1, T2, T3, T4>(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4
    )
}
