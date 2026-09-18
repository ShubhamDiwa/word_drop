package com.diws.worddrop.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.worddrop.data.preferences.UserPreferencesRepository
import com.diws.worddrop.data.preferences.UserSettings
import com.diws.worddrop.domain.repository.WordRepository
import com.diws.worddrop.notification.NotificationScheduler
import com.diws.worddrop.notification.VocabularyNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val notificationManager: VocabularyNotificationManager,
    private val wordRepository: WordRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = preferencesRepository.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateNotificationsEnabled(enabled)
            if (enabled) {
                NotificationScheduler.scheduleDailyNotifications(context, userSettings.value.notificationTimes)
            } else {
                NotificationScheduler.cancelNotifications(context)
            }
        }
    }

    fun setWordsPerDay(count: Int) {
        viewModelScope.launch {
            preferencesRepository.updateWordsPerDay(count)
        }
    }

    fun setDifficultyFilter(difficulty: String) {
        viewModelScope.launch {
            preferencesRepository.updateDifficultyFilter(difficulty)
        }
    }

    fun setIncludeReviewWords(include: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateIncludeReviewWords(include)
        }
    }

    fun setShowOnLockScreen(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateShowOnLockScreen(show)
        }
    }

    fun updateNotificationTimes(times: List<String>) {
        viewModelScope.launch {
            val sortedTimes = times.sorted()
            preferencesRepository.updateNotificationTimes(sortedTimes)
            if (userSettings.value.notificationsEnabled) {
                NotificationScheduler.scheduleDailyNotifications(context, sortedTimes)
            }
        }
    }

    fun addNotificationTime(time: String) {
        val current = userSettings.value.notificationTimes.toMutableList()
        if (!current.contains(time) && current.size < 10) {
            current.add(time)
            updateNotificationTimes(current)
        }
    }

    fun editNotificationTime(oldTime: String, newTime: String) {
        val current = userSettings.value.notificationTimes.toMutableList()
        val index = current.indexOf(oldTime)
        if (index != -1) {
            current[index] = newTime
        } else if (!current.contains(newTime)) {
            current.add(newTime)
        }
        updateNotificationTimes(current)
    }

    fun removeNotificationTime(time: String) {
        val current = userSettings.value.notificationTimes.toMutableList()
        if (current.size > 1) {
            current.remove(time)
            updateNotificationTimes(current)
        }
    }

    fun triggerTestNotification() {
        viewModelScope.launch {
            wordRepository.seedInitialDataIfNeeded()
            val word = wordRepository.getUnlearnedWordsForNotification(1).firstOrNull()
                ?: wordRepository.getRandomWord()
            if (word != null) {
                notificationManager.showVocabularyNotification(word)
            }
        }
    }
}
