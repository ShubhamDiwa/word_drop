package com.diws.wordzip.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import com.diws.wordzip.MainActivity
import com.diws.wordzip.R
import com.diws.wordzip.data.local.DailyWordEntity
import com.diws.wordzip.data.local.WordDatabase
import com.diws.wordzip.data.local.WordEntity
import com.diws.wordzip.notification.VocabularyNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AppWidgetProvider for the "Word of the Day" home screen widget.
 */
class WordWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, WordWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                updateWidgets(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.diws.wordzip.widget.ACTION_REFRESH_WIDGET"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, WordWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                updateWidgets(context, appWidgetManager, appWidgetIds)
            }
        }

        private fun updateWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = WordDatabase.getInstance(context)
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    var dailyEntity = db.wordDao().getDailyWordSync(today)

                    // If daily word entity doesn't exist yet for today, pick one or create one
                    if (dailyEntity == null) {
                        val allWords = db.wordDao().getAllWords().first()
                        val candidate = allWords.randomOrNull()
                        if (candidate != null) {
                            val newDaily = DailyWordEntity(date = today, wordId = candidate.id)
                            db.wordDao().insertDailyWord(newDaily)
                            dailyEntity = newDaily
                        }
                    }

                    val word: WordEntity? = dailyEntity?.let { db.wordDao().getWordById(it.wordId) }
                        ?: db.wordDao().getRandomWord()

                    for (widgetId in appWidgetIds) {
                        val views = RemoteViews(context.packageName, R.layout.widget_word_of_the_day)

                        if (word != null) {
                            views.setTextViewText(R.id.widget_word_text, word.word.uppercase())

                            val pronunciation = word.pronunciation
                            if (!pronunciation.isNullOrEmpty()) {
                                views.setTextViewText(R.id.widget_pronunciation_text, pronunciation)
                                views.setViewVisibility(R.id.widget_pronunciation_text, View.VISIBLE)
                            } else {
                                views.setViewVisibility(R.id.widget_pronunciation_text, View.GONE)
                            }

                            val definition = if (!word.simpleMeaning.isNullOrBlank()) {
                                word.simpleMeaning
                            } else {
                                word.definition
                            }
                            views.setTextViewText(R.id.widget_definition_text, definition ?: "")

                            val difficulty = word.difficulty.uppercase()
                            views.setTextViewText(
                                R.id.widget_difficulty_text,
                                word.difficulty.replaceFirstChar { it.uppercase() }
                            )
                            when (difficulty) {
                                "BEGINNER" -> views.setTextColor(R.id.widget_difficulty_text, Color.parseColor("#4DDCC6"))
                                "ADVANCED" -> views.setTextColor(R.id.widget_difficulty_text, Color.parseColor("#FF6B8B"))
                                else -> views.setTextColor(R.id.widget_difficulty_text, Color.parseColor("#FFB74D"))
                            }

                            // Click intent opens the app straight to that word's details
                            val clickIntent = Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                putExtra(VocabularyNotificationManager.EXTRA_WORD_ID, word.id)
                                putExtra("word_id", word.id)
                            }
                            val pendingIntent = PendingIntent.getActivity(
                                context,
                                widgetId,
                                clickIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
                        } else {
                            views.setTextViewText(R.id.widget_word_text, "WORDZIP")
                            views.setTextViewText(R.id.widget_definition_text, "Open app to discover today's word!")
                            val defaultIntent = Intent(context, MainActivity::class.java)
                            val pendingIntent = PendingIntent.getActivity(
                                context,
                                widgetId,
                                defaultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
                        }

                        appWidgetManager.updateAppWidget(widgetId, views)
                    }
                } catch (e: Exception) {
                    // Safe silent catch
                }
            }
        }
    }
}
