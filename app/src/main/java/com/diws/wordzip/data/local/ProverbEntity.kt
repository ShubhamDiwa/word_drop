package com.diws.wordzip.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diws.wordzip.domain.model.Proverb

@Entity(tableName = "proverbs")
data class ProverbEntity(
    @PrimaryKey
    val id: String,
    val englishText: String,
    val hindiText: String,
    val hindiEquivalent: String? = null,
    val meaningEnglish: String,
    val meaningHindi: String,
    val example: String? = null,
    val category: String = "Wisdom",
    val isFavorite: Boolean = false,
    val timesShown: Int = 0,
    val lastShownAt: Long? = null
) {
    fun toDomainModel(): Proverb {
        return Proverb(
            id = id,
            englishText = englishText,
            hindiText = hindiText,
            hindiEquivalent = hindiEquivalent,
            meaningEnglish = meaningEnglish,
            meaningHindi = meaningHindi,
            example = example,
            category = category,
            isFavorite = isFavorite,
            timesShown = timesShown,
            lastShownAt = lastShownAt
        )
    }
}
