package com.diws.wordzip.data.remote.translation

import com.diws.wordzip.domain.model.WordTranslation

interface TranslationDataSource {
    suspend fun translateText(
        text: String,
        sourceLanguage: String = "en",
        targetLanguage: String
    ): Result<String>

    suspend fun translateWord(
        wordId: String,
        englishMeaning: String,
        targetLanguages: List<String>
    ): List<WordTranslation>
}
