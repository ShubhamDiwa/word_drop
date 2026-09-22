package com.diws.worddrop.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

object NotificationScheduler {

    const val ACTION_TRIGGER_VOCAB_NOTIFICATION = "com.diws.worddrop.ACTION_TRIGGER_VOCAB_NOTIFICATION"
    private const val REQUEST_CODE_BASE = 2000

    /**
     * Unique, deterministic request code for any HH:mm time.
     * Maps each minute of the day (0..1439) to REQUEST_CODE_BASE + minuteOfDay.
     */
    fun getRequestCodeForTime(timeString: String): Int {
        val parts = timeString.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return REQUEST_CODE_BASE + (hour * 60 + minute)
    }

    /**
     * Schedules daily vocabulary notifications using AlarmManager with setExactAndAllowWhileIdle
     * to guarantee delivery on time even when the device is locked or in Doze/sleep mode.
     */
    fun scheduleDailyNotifications(context: Context, times: List<String>) {
        cancelNotifications(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        times.forEach { timeString ->
            val parts = timeString.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toIntOrNull() ?: 8
                val minute = parts[1].toIntOrNull() ?: 0

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    // If time has already passed today, schedule for tomorrow
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                val intent = Intent(context, VocabularyNotificationReceiver::class.java).apply {
                    action = ACTION_TRIGGER_VOCAB_NOTIFICATION
                    putExtra("time_slot", timeString)
                }

                val requestCode = getRequestCodeForTime(timeString)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                Log.d("WordDropScheduler", "Scheduling alarm for $timeString (triggerAt=${calendar.time}, reqCode=$requestCode)")
                scheduleExact(alarmManager, calendar.timeInMillis, pendingIntent)
            }
        }
    }

    /**
     * Cancels all scheduled Wordzip alarms deterministically.
     */
    fun cancelNotifications(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Clear all possible minute-of-day slots (0..1439) and legacy indices (0..50)
        for (minuteOfDay in 0 until 1440) {
            val intent = Intent(context, VocabularyNotificationReceiver::class.java).apply {
                action = ACTION_TRIGGER_VOCAB_NOTIFICATION
            }
            val requestCode = REQUEST_CODE_BASE + minuteOfDay
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
     * Cancels the alarm for a specific time slot.
     */
    fun cancelNotificationForTime(context: Context, timeString: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, VocabularyNotificationReceiver::class.java).apply {
            action = ACTION_TRIGGER_VOCAB_NOTIFICATION
        }
        val requestCode = getRequestCodeForTime(timeString)
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

    /**
     * Reschedules the next occurrence for a specific time slot after delivery (24 hours later).
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

            val requestCode = getRequestCodeForTime(timeString)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.d("WordDropScheduler", "Rescheduled alarm for $timeString (triggerAt=${calendar.time}, reqCode=$requestCode)")
            scheduleExact(alarmManager, calendar.timeInMillis, pendingIntent)
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun scheduleExact(
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Fallback for devices where exact alarm permission is restricted:
            // setAndAllowWhileIdle wakes the device from Doze mode without requiring exact alarm permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        }
    }
}

