package com.diws.wordzip.data.remote.firebase

import com.diws.wordzip.data.local.WordEntity
import com.diws.wordzip.domain.model.WordDifficulty

data class FirebaseWordDto(
    val id: String = "",
    val word: String = "",
    val pronunciation: String? = null,
    val partOfSpeech: String? = null,
    val definition: String = "",
    val simpleMeaning: String? = null,
    val example: String? = null,
    val synonyms: Any? = null, // Can be List<String> or String
    val difficulty: String = "INTERMEDIATE",
    val category: String? = null,
    val audioUrl: String? = null,
    val active: Boolean = true,
    val version: Long = 1L,
    val updatedAt: Long = 0L
) {
    fun isValid(): Boolean {
        return id.isNotBlank() && word.isNotBlank() && definition.isNotBlank()
    }

    fun toEntity(existingEntity: WordEntity? = null): WordEntity {
        val synonymsString = when (synonyms) {
            is List<*> -> synonyms.filterIsInstance<String>().joinToString(",")
            is String -> synonyms
            else -> ""
        }

        val normalizedDifficulty = WordDifficulty.fromString(difficulty).name

        return WordEntity(
            id = id,
            word = word.trim(),
            pronunciation = pronunciation?.trim(),
            partOfSpeech = partOfSpeech?.trim()?.uppercase(),
            definition = definition.trim(),
            simpleMeaning = simpleMeaning?.trim(),
            example = example?.trim(),
            synonyms = synonymsString,
            difficulty = normalizedDifficulty,
            category = category?.trim(),
            audioUrl = audioUrl?.trim(),
            isLearned = existingEntity?.isLearned ?: false,
            timesShown = existingEntity?.timesShown ?: 0,
            lastShownAt = existingEntity?.lastShownAt
        )
    }
}
