package com.diws.wordzip.domain.model

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
    val lastShownAt: Long? = null,
    val translations: Map<String, String> = emptyMap()
) {
    /**
     * Resolves the meaning for a single language code, returning null if missing.
     */
    fun getMeaningForLanguage(languageCode: String): String? {
        val direct = translations[languageCode.lowercase().trim()]
        if (!direct.isNullOrBlank()) return direct
        return null
    }

    /**
     * Resolves meanings for the given list of selected language codes.
     * If a translation is missing for a selected language, gracefully falls back to the English meaning.
     * If selectedLanguageCodes is empty, returns the English meaning.
     */
    fun resolveMeanings(selectedLanguageCodes: List<String>): List<ResolvedMeaning> {
        val englishMeaning = if (!simpleMeaning.isNullOrBlank()) simpleMeaning else definition

        if (selectedLanguageCodes.isEmpty()) {
            return listOf(
                ResolvedMeaning(
                    languageCode = "en",
                    languageName = "English",
                    meaning = englishMeaning,
                    isFallback = false
                )
            )
        }

        return selectedLanguageCodes.map { langCode ->
            val cleanCode = langCode.lowercase().trim()
            val language = MeaningLanguage.fromCode(cleanCode)
            val langName = language?.englishName ?: cleanCode.uppercase()

            val translatedMeaning = translations[cleanCode]
            if (!translatedMeaning.isNullOrBlank()) {
                ResolvedMeaning(
                    languageCode = cleanCode,
                    languageName = langName,
                    meaning = translatedMeaning,
                    isFallback = false
                )
            } else {
                ResolvedMeaning(
                    languageCode = cleanCode,
                    languageName = langName,
                    meaning = englishMeaning,
                    isFallback = true
                )
            }
        }
    }

    /**
     * Formats concise meaning text suitable for notifications.
     */
    fun resolveNotificationMeaning(selectedLanguageCodes: List<String>): String {
        val englishMeaning = if (!simpleMeaning.isNullOrBlank()) simpleMeaning else definition
        if (selectedLanguageCodes.isEmpty()) {
            return englishMeaning
        }

        val resolved = resolveMeanings(selectedLanguageCodes)
        return if (resolved.size == 1) {
            resolved.first().meaning
        } else {
            resolved.joinToString("\n") { "${it.languageName}: ${it.meaning}" }
        }
    }
}
