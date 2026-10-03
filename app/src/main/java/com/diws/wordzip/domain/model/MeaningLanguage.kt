package com.diws.wordzip.domain.model

data class MeaningLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val flagEmoji: String = ""
) {
    val displayName: String
        get() = "$englishName ($nativeName)"

    companion object {
        val HINDI = MeaningLanguage(
            code = "hi",
            englishName = "Hindi",
            nativeName = "हिन्दी"
        )

        val GUJARATI = MeaningLanguage(
            code = "gu",
            englishName = "Gujarati",
            nativeName = "ગુજરાતી"
        )

        val MARATHI = MeaningLanguage(
            code = "mr",
            englishName = "Marathi",
            nativeName = "मराठी"
        )

        val BENGALI = MeaningLanguage(
            code = "bn",
            englishName = "Bengali",
            nativeName = "বাংলা"
        )

        val TELUGU = MeaningLanguage(
            code = "te",
            englishName = "Telugu",
            nativeName = "తెలుగు"
        )

        val TAMIL = MeaningLanguage(
            code = "ta",
            englishName = "Tamil",
            nativeName = "தமிழ்"
        )

        val KANNADA = MeaningLanguage(
            code = "kn",
            englishName = "Kannada",
            nativeName = "ಕನ್ನಡ"
        )

        val MALAYALAM = MeaningLanguage(
            code = "ml",
            englishName = "Malayalam",
            nativeName = "മലയാളം"
        )

        val PUNJABI = MeaningLanguage(
            code = "pa",
            englishName = "Punjabi",
            nativeName = "ਪੰਜਾਬੀ"
        )

        val ODIA = MeaningLanguage(
            code = "or",
            englishName = "Odia",
            nativeName = "ଓଡ଼ିଆ"
        )

        val URDU = MeaningLanguage(
            code = "ur",
            englishName = "Urdu",
            nativeName = "اردو"
        )

        // Major Indian languages list
        val SUPPORTED_LANGUAGES: List<MeaningLanguage> = listOf(
            HINDI,
            GUJARATI,
            MARATHI,
            BENGALI,
            TELUGU,
            TAMIL,
            KANNADA,
            MALAYALAM,
            PUNJABI,
            ODIA,
            URDU
        )

        fun fromCode(code: String?): MeaningLanguage? {
            if (code == null) return null
            return SUPPORTED_LANGUAGES.firstOrNull { it.code.equals(code, ignoreCase = true) }
        }

        fun getDisplayName(code: String): String {
            return fromCode(code)?.englishName ?: code.uppercase()
        }
    }
}

data class WordTranslation(
    val languageCode: String,
    val meaning: String,
    val example: String? = null
)

data class ResolvedMeaning(
    val languageCode: String,
    val languageName: String,
    val flagEmoji: String = "",
    val meaning: String,
    val isFallback: Boolean = false
)
