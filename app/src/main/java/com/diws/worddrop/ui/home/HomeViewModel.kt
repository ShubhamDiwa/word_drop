package com.diws.worddrop.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.worddrop.data.preferences.UserPreferencesRepository
import com.diws.worddrop.domain.model.UserLevelInfo
import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.domain.model.XpLevelManager
import com.diws.worddrop.domain.repository.WordRepository
import com.diws.worddrop.domain.usecase.GetProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val wordOfTheDay: Word? = null,
    val todayWords: List<Word> = emptyList(),
    val streak: Int = 7,
    val streakShields: Int = 0,
    val levelInfo: UserLevelInfo = XpLevelManager.getLevelInfo(0),
    val learnedToday: Int = 3,
    val totalTodayTarget: Int = 5,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    getProgressUseCase: GetProgressUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        wordRepository.getWordOfTheDay(),
        wordRepository.getHomeWords(),   // lean: max 20 rows, unlearned-first
        getProgressUseCase(),
        userPreferencesRepository.userXpFlow,
        userPreferencesRepository.streakShieldsFlow
    ) { wordOfTheDay, allWords, progress, userXp, streakShields ->
        
        // Show up to 10 words on the Home Screen for daily learning
        // Only show unlearned words for the daily target if possible
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
            todayWords = todaysList,
            streak = progress.streakDays,
            streakShields = streakShields,
            levelInfo = XpLevelManager.getLevelInfo(userXp),
            learnedToday = learnedCount,
            totalTodayTarget = todaysList.size.coerceAtLeast(1)
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
}
