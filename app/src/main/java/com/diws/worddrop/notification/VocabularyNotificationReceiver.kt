package com.diws.worddrop.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.diws.worddrop.domain.repository.WordRepository
import com.diws.worddrop.domain.usecase.SelectNextWordForNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VocabularyNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var wordRepository: WordRepository

    @Inject
    lateinit var notificationManager: VocabularyNotificationManager

    @Inject
    lateinit var selectNextWordUseCase: SelectNextWordForNotificationUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val timeSlot = intent.getStringExtra("time_slot") ?: "08:00"

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Wordzip:VocabularyNotificationWakeLock"
        )?.apply {
            setReferenceCounted(false)
            acquire(30_000L) // Safety timeout: 30s
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("WordzipAlarm", "Alarm triggered for slot: $timeSlot. Seeding and fetching word...")
                wordRepository.seedInitialDataIfNeeded()
                val wordToShow = selectNextWordUseCase(isScheduledDelivery = true)

                if (wordToShow != null) {
                    Log.d("WordzipAlarm", "Showing notification for word: ${wordToShow.word}")
                    notificationManager.showVocabularyNotification(wordToShow)
                    com.diws.worddrop.widget.WordWidgetProvider.updateAllWidgets(context)
                } else {
                    Log.w("WordzipAlarm", "No word found to show for slot: $timeSlot")
                }

                NotificationScheduler.rescheduleNextOccurrence(context, timeSlot)
            } catch (e: Exception) {
                Log.e("WordzipAlarm", "Error handling notification alarm", e)
            } finally {
                try {
                    if (wakeLock?.isHeld == true) {
                        wakeLock.release()
                    }
                } catch (_: Exception) {}
                pendingResult.finish()
            }
        }
    }
}
