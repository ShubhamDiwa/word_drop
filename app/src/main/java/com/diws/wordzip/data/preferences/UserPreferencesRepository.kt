package com.diws.wordzip.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val notificationsEnabled: Boolean = true,
    val wordsPerDay: Int = 5,
    val difficultyFilter: String = "MIXED",
    val includeReviewWords: Boolean = true,
    val showOnLockScreen: Boolean = true,
    val notificationTimes: List<String> = listOf("08:00", "12:00", "16:00", "20:00"),
    val appTheme: String = "LIGHT"
)

class UserPreferencesRepository(private val context: Context) {

    private val themePrefs = context.getSharedPreferences("theme_cache", Context.MODE_PRIVATE)

    fun getCachedTheme(): String {
        return themePrefs.getString("app_theme", "LIGHT") ?: "LIGHT"
    }

    private object PreferencesKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val WORDS_PER_DAY = intPreferencesKey("words_per_day")
        val DIFFICULTY_FILTER = stringPreferencesKey("difficulty_filter")
        val INCLUDE_REVIEW_WORDS = booleanPreferencesKey("include_review_words")
        val SHOW_ON_LOCK_SCREEN = booleanPreferencesKey("show_on_lock_screen")
        val NOTIFICATION_TIMES = stringPreferencesKey("notification_times")
        val APP_THEME = stringPreferencesKey("app_theme")
        val LAST_VOCABULARY_SYNC_TIMESTAMP = longPreferencesKey("last_vocabulary_sync_timestamp")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val USER_XP = intPreferencesKey("user_xp")
        val STREAK_SHIELDS = intPreferencesKey("streak_shields")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val timesString = preferences[PreferencesKeys.NOTIFICATION_TIMES] ?: "08:00,12:00,16:00,20:00"
        val cached = themePrefs.getString("app_theme", "LIGHT") ?: "LIGHT"
        val theme = preferences[PreferencesKeys.APP_THEME] ?: cached
        UserSettings(
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            wordsPerDay = preferences[PreferencesKeys.WORDS_PER_DAY] ?: 5,
            difficultyFilter = preferences[PreferencesKeys.DIFFICULTY_FILTER] ?: "MIXED",
            includeReviewWords = preferences[PreferencesKeys.INCLUDE_REVIEW_WORDS] ?: true,
            showOnLockScreen = preferences[PreferencesKeys.SHOW_ON_LOCK_SCREEN] ?: true,
            notificationTimes = timesString.split(",").filter { it.isNotBlank() },
            appTheme = theme
        )
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateWordsPerDay(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORDS_PER_DAY] = count
        }
    }

    suspend fun updateDifficultyFilter(difficulty: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DIFFICULTY_FILTER] = difficulty
        }
    }

    suspend fun updateIncludeReviewWords(include: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.INCLUDE_REVIEW_WORDS] = include
        }
    }

    suspend fun updateShowOnLockScreen(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ON_LOCK_SCREEN] = show
        }
    }

    suspend fun updateAppTheme(theme: String) {
        themePrefs.edit().putString("app_theme", theme).apply()
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme
        }
    }

    suspend fun updateNotificationTimes(times: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_TIMES] = times.joinToString(",")
        }
    }

    val lastVocabularySyncTimestampFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_VOCABULARY_SYNC_TIMESTAMP] ?: 0L
    }

    suspend fun updateLastVocabularySyncTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_VOCABULARY_SYNC_TIMESTAMP] = timestamp
        }
    }

    // Onboarding — shown only on first launch
    val hasSeenOnboardingFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_SEEN_ONBOARDING] ?: false
    }

    suspend fun markOnboardingSeen() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_ONBOARDING] = true
        }
    }

    // XP Points & Level System
    val userXpFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_XP] ?: 0
    }

    suspend fun addXp(amount: Int): Int {
        var newXp = 0
        context.dataStore.edit { preferences ->
            val currentXp = preferences[PreferencesKeys.USER_XP] ?: 0
            newXp = (currentXp + amount).coerceAtLeast(0)
            preferences[PreferencesKeys.USER_XP] = newXp
        }
        return newXp
    }

    // Streak Shields System (Max 2 active shields stored)
    val streakShieldsFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.STREAK_SHIELDS] ?: 0
    }

    suspend fun addStreakShield(count: Int = 1): Int {
        var newCount = 0
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.STREAK_SHIELDS] ?: 0
            newCount = (current + count).coerceIn(0, 2)
            preferences[PreferencesKeys.STREAK_SHIELDS] = newCount
        }
        return newCount
    }

    suspend fun useStreakShield(): Boolean {
        var used = false
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.STREAK_SHIELDS] ?: 0
            if (current > 0) {
                preferences[PreferencesKeys.STREAK_SHIELDS] = current - 1
                used = true
            }
        }
        return used
    }
}
