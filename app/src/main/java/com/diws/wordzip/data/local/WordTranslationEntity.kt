package com.diws.wordzip.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "word_translations",
    primaryKeys = ["wordId", "languageCode"],
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("wordId"),
        Index("languageCode")
    ]
)
data class WordTranslationEntity(
    val wordId: String,
    val languageCode: String,
    val meaning: String,
    val example: String? = null
)
