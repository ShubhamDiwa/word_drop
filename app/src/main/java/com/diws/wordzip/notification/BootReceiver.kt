package com.diws.wordzip.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.diws.wordzip.data.preferences.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val settings = userPreferencesRepository.userSettingsFlow.first()
                    if (settings.notificationsEnabled) {
                        NotificationScheduler.scheduleDailyNotifications(
                            context,
                            settings.notificationTimes
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
