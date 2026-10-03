package com.diws.wordzip.domain.model

data class Proverb(
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
)
