package com.diws.wordzip.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_proverbs")
data class DailyProverbEntity(
    @PrimaryKey
    val date: String, // Format "YYYY-MM-DD"
    val proverbId: String,
    val assignedAt: Long = System.currentTimeMillis()
)
