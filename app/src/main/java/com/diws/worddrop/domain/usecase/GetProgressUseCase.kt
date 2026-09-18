package com.diws.worddrop.domain.usecase

import com.diws.worddrop.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

data class UserProgress(
    val totalWords: Int,
    val learnedWords: Int,
    val streakDays: Int = 0,
    val weeklyActivity: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0)
)

class GetProgressUseCase @Inject constructor(
    private val repository: WordRepository
) {
    operator fun invoke(): Flow<UserProgress> {
        return repository.getLearnedCount().combine(repository.getAllWords()) { learned, allWords ->
            val calendar = Calendar.getInstance(TimeZone.getDefault())

            // Get start of the current week (Monday)
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val weekActivity = MutableList(7) { 0 }
            val weekStartMs = calendar.timeInMillis
            val oneDayMs = 24 * 60 * 60 * 1000L

            for (i in 0 until 7) {
                val dayStart = weekStartMs + i * oneDayMs
                val dayEnd = dayStart + oneDayMs
                val countForDay = allWords.count { word ->
                    val timestamp = word.lastShownAt ?: 0L
                    timestamp in dayStart..<dayEnd
                }
                weekActivity[i] = countForDay
            }

            // Calculate active streak
            val todayCal = Calendar.getInstance(TimeZone.getDefault())
            todayCal.set(Calendar.HOUR_OF_DAY, 0)
            todayCal.set(Calendar.MINUTE, 0)
            todayCal.set(Calendar.SECOND, 0)
            todayCal.set(Calendar.MILLISECOND, 0)
            val todayStartMs = todayCal.timeInMillis

            var streak = 0
            var checkDayStartMs = todayStartMs

            val hasTodayActivity = allWords.any { (it.lastShownAt ?: 0L) in checkDayStartMs..<checkDayStartMs + oneDayMs }
            if (hasTodayActivity) {
                streak++
                checkDayStartMs -= oneDayMs
            } else {
                checkDayStartMs -= oneDayMs
            }

            while (checkDayStartMs > 0) {
                val hasDayActivity = allWords.any { (it.lastShownAt ?: 0L) in checkDayStartMs..<checkDayStartMs + oneDayMs }
                if (hasDayActivity) {
                    streak++
                    checkDayStartMs -= oneDayMs
                } else {
                    break
                }
            }

            UserProgress(
                totalWords = allWords.size,
                learnedWords = learned,
                streakDays = streak,
                weeklyActivity = weekActivity
            )
        }
    }
}
