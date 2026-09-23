package com.diws.wordzip.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.model.WordDifficulty

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val id: String,
    val word: String,
    val pronunciation: String?,
    val partOfSpeech: String?,
    val definition: String,
    val simpleMeaning: String?,
    val example: String?,
    val synonyms: String?, // Stored as comma-separated string
    val difficulty: String,
    val category: String?,
    val audioUrl: String?,
    val isLearned: Boolean = false,
    val timesShown: Int = 0,
    val lastShownAt: Long? = null
) {
    fun toDomainModel(): Word {
        return Word(
            id = id,
            word = word,
            pronunciation = pronunciation,
            partOfSpeech = partOfSpeech,
            definition = definition,
            simpleMeaning = simpleMeaning,
            example = example,
            synonyms = synonyms?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
            difficulty = WordDifficulty.fromString(difficulty),
            category = category,
            audioUrl = audioUrl,
            isLearned = isLearned,
            timesShown = timesShown,
            lastShownAt = lastShownAt
        )
    }

    companion object {
        fun fromDomainModel(word: Word): WordEntity {
            return WordEntity(
                id = word.id,
                word = word.word,
                pronunciation = word.pronunciation,
                partOfSpeech = word.partOfSpeech,
                definition = word.definition,
                simpleMeaning = word.simpleMeaning,
                example = word.example,
                synonyms = word.synonyms.joinToString(","),
                difficulty = word.difficulty.name,
                category = word.category,
                audioUrl = word.audioUrl,
                isLearned = word.isLearned,
                timesShown = word.timesShown,
                lastShownAt = word.lastShownAt
            )
        }
    }
}
