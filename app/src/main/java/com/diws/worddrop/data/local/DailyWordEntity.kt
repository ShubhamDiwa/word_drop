package com.diws.worddrop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_words")
data class DailyWordEntity(
    @PrimaryKey
    val date: String, // Format "YYYY-MM-DD"
    val wordId: String,
    val assignedAt: Long = System.currentTimeMillis()
)
