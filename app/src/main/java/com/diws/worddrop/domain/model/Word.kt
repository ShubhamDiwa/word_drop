package com.diws.worddrop.domain.model

enum class WordDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    companion object {
        fun fromString(value: String?): WordDifficulty {
            return when (value?.uppercase()) {
                "BEGINNER" -> BEGINNER
                "INTERMEDIATE" -> INTERMEDIATE
                "ADVANCED" -> ADVANCED
                else -> INTERMEDIATE
            }
        }
    }
}

data class Word(
    val id: String,
    val word: String,
    val pronunciation: String? = null,
    val partOfSpeech: String? = null,
    val definition: String,
    val simpleMeaning: String? = null,
    val example: String? = null,
    val synonyms: List<String> = emptyList(),
    val difficulty: WordDifficulty = WordDifficulty.INTERMEDIATE,
    val category: String? = null,
    val audioUrl: String? = null,
    val isLearned: Boolean = false,
    val timesShown: Int = 0,
    val lastShownAt: Long? = null
)
