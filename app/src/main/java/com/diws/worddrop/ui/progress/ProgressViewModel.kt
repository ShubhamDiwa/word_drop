package com.diws.worddrop.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.worddrop.data.preferences.UserPreferencesRepository
import com.diws.worddrop.domain.model.UserLevelInfo
import com.diws.worddrop.domain.model.XpLevelManager
import com.diws.worddrop.domain.usecase.GetProgressUseCase
import com.diws.worddrop.domain.usecase.UserProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProgressUiState(
    val isLoading: Boolean = false,
    val progress: UserProgress = UserProgress(totalWords = 20, learnedWords = 5),
    val userLevelInfo: UserLevelInfo = XpLevelManager.getLevelInfo(0),
    val streakShields: Int = 0
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    getProgressUseCase: GetProgressUseCase,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = combine(
        getProgressUseCase(),
        userPreferencesRepository.userXpFlow,
        userPreferencesRepository.streakShieldsFlow
    ) { userProgress, xp, shields ->
        ProgressUiState(
            isLoading = false,
            progress = userProgress,
            userLevelInfo = XpLevelManager.getLevelInfo(xp),
            streakShields = shields
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressUiState(isLoading = true)
    )
}

