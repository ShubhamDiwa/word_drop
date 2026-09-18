package com.diws.worddrop.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    const val ACTION_TRIGGER_VOCAB_NOTIFICATION = "com.diws.worddrop.ACTION_TRIGGER_VOCAB_NOTIFICATION"
    private const val REQUEST_CODE_BASE = 2000

    /**
     * Schedules daily vocabulary notifications using AlarmManager with inexact scheduling
     * to preserve battery and avoid unnecessary exact alarm permissions.
     */
    fun scheduleDailyNotifications(context: Context, times: List<String>) {
        cancelNotifications(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        times.forEachIndexed { index, timeString ->
            val parts = timeString.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toIntOrNull() ?: 8
                val minute = parts[1].toIntOrNull() ?: 0

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    // Handle times that have already passed today by scheduling for tomorrow
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                val intent = Intent(context, VocabularyNotificationReceiver::class.java).apply {
                    action = ACTION_TRIGGER_VOCAB_NOTIFICATION
                    putExtra("time_slot", timeString)
                }

                val requestCode = REQUEST_CODE_BASE + index
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Use standard inexact alarm (alarmManager.set) as vocabulary notifications do not require exact second precision
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    /**
     * Cancels all scheduled Word Drop alarms deterministically.
     */
    fun cancelNotifications(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Check a generous range of potential request codes to ensure all slot alarms are cleared
        for (i in 0 until 20) {
            val intent = Intent(context, VocabularyNotificationReceiver::class.java).apply {
                action = ACTION_TRIGGER_VOCAB_NOTIFICATION
            }
            val requestCode = REQUEST_CODE_BASE + i
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    /**
     * Reschedules the next occurrence for a specific time slot after delivery.
     */
    fun rescheduleNextOccurrence(context: Context, timeString: String) {
        val parts = timeString.split(":")
        if (parts.size == 2) {
            val hour = parts[0].toIntOrNull() ?: 8
            val minute = parts[1].toIntOrNull() ?: 0

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // Always schedule for tomorrow upon delivery of today's slot
                add(Calendar.DAY_OF_YEAR, 1)
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, VocabularyNotificationReceiver::class.java).apply {
                action = ACTION_TRIGGER_VOCAB_NOTIFICATION
                putExtra("time_slot", timeString)
            }

            val requestCode = REQUEST_CODE_BASE + Math.abs(timeString.hashCode() % 100)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}
