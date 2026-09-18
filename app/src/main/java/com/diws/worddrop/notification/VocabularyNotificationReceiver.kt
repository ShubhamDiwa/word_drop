package com.diws.worddrop.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                wordRepository.seedInitialDataIfNeeded()
                val wordToShow = selectNextWordUseCase(isScheduledDelivery = true)

                if (wordToShow != null) {
                    notificationManager.showVocabularyNotification(wordToShow)
                    com.diws.worddrop.widget.WordWidgetProvider.updateAllWidgets(context)
                }

                NotificationScheduler.rescheduleNextOccurrence(context, timeSlot)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
