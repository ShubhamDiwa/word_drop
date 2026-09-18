package com.diws.worddrop.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.diws.worddrop.MainActivity
import com.diws.worddrop.R
import com.diws.worddrop.data.local.WordDao
import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.domain.model.WordDifficulty
import android.content.res.Configuration
import com.diws.worddrop.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordDao: WordDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    companion object {
        const val CHANNEL_ID = "vocabulary_notification_channel"
        const val CHANNEL_NAME = "Vocabulary Notifications"
        const val CHANNEL_DESC = "Delivers English vocabulary words throughout the day"
        const val EXTRA_WORD_ID = "word_id"
        const val NOTIFICATION_ID = 1001
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    suspend fun showVocabularyNotification(word: Word) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_WORD_ID, word.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            word.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val settings = try {
            userPreferencesRepository.userSettingsFlow.first()
        } catch (_: Exception) {
            null
        }

        val isDark = when (settings?.appTheme) {
            "LIGHT" -> false
            "DARK" -> true
            else -> (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }

        val collapsedRes = if (isDark) R.layout.notification_word_collapsed else R.layout.notification_word_collapsed_light
        val expandedRes = if (isDark) R.layout.notification_word_expanded else R.layout.notification_word_expanded_light

        // Collapsed Custom Layout
        val collapsedLayout = RemoteViews(context.packageName, collapsedRes).apply {
            setTextViewText(R.id.notification_word_title, word.word.uppercase())
            setTextViewText(R.id.notification_word_meaning, word.simpleMeaning ?: word.definition)
            setTextViewText(R.id.notification_difficulty_chip, word.difficulty.name)

            if (!isDark) {
                val (badgeRes, textColor) = when (word.difficulty) {
                    WordDifficulty.BEGINNER -> Pair(R.drawable.badge_beginner_light, Color.parseColor("#0D8267"))
                    WordDifficulty.INTERMEDIATE -> Pair(R.drawable.badge_intermediate_light, Color.parseColor("#B45309"))
                    WordDifficulty.ADVANCED -> Pair(R.drawable.badge_advanced_light, Color.parseColor("#BE123C"))
                }
                setInt(R.id.notification_difficulty_chip, "setBackgroundResource", badgeRes)
                setTextColor(R.id.notification_difficulty_chip, textColor)
            }
        }

        // Expanded Custom Layout
        val expandedLayout = RemoteViews(context.packageName, expandedRes).apply {
            setTextViewText(R.id.notification_word_title, word.word.uppercase())
            
            // Difficulty badge background & text color
            val (badgeRes, textColor) = if (isDark) {
                when (word.difficulty) {
                    WordDifficulty.BEGINNER -> Pair(R.drawable.badge_beginner, Color.parseColor("#E2E8F0"))
                    WordDifficulty.INTERMEDIATE -> Pair(R.drawable.badge_intermediate, Color.parseColor("#26215C"))
                    WordDifficulty.ADVANCED -> Pair(R.drawable.badge_advanced, Color.parseColor("#FFE4E6"))
                }
            } else {
                when (word.difficulty) {
                    WordDifficulty.BEGINNER -> Pair(R.drawable.badge_beginner_light, Color.parseColor("#0D8267"))
                    WordDifficulty.INTERMEDIATE -> Pair(R.drawable.badge_intermediate_light, Color.parseColor("#B45309"))
                    WordDifficulty.ADVANCED -> Pair(R.drawable.badge_advanced_light, Color.parseColor("#BE123C"))
                }
            }
            setInt(R.id.notification_difficulty_badge, "setBackgroundResource", badgeRes)
            setTextViewText(R.id.notification_difficulty_badge, word.difficulty.name)
            setTextColor(R.id.notification_difficulty_badge, textColor)

            if (!word.pronunciation.isNull_or_empty()) {
                setTextViewText(R.id.notification_pronunciation, word.pronunciation)
                setViewVisibility(R.id.notification_pronunciation, View.VISIBLE)
            } else {
                setViewVisibility(R.id.notification_pronunciation, View.GONE)
            }

            setTextViewText(R.id.notification_definition, word.definition)

            if (!word.example.isNull_or_empty()) {
                setTextViewText(R.id.notification_example, "\"${word.example}\"")
                setViewVisibility(R.id.notification_example, View.VISIBLE)
            } else {
                setViewVisibility(R.id.notification_example, View.GONE)
            }

            // Fetch user streak & completion dots suspendly without blocking
            val learnedCount = try {
                wordDao.getLearnedCountSync()
            } catch (_: Exception) {
                0
            }
            val streak = if (learnedCount > 0) learnedCount.coerceAtMost(30) else 1
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(collapsedLayout)
            .setCustomBigContentView(expandedLayout)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
}
