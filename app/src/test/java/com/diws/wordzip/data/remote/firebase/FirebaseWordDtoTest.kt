package com.diws.wordzip.data.remote.firebase

import com.diws.wordzip.data.local.WordEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseWordDtoTest {

    @Test
    fun `isValid returns true when required fields are present`() {
        val dto = FirebaseWordDto(
            id = "word_100",
            word = "Serendipity",
            definition = "Finding valuable things by chance"
        )
        assertTrue(dto.isValid())
    }

    @Test
    fun `isValid returns false when id or word or definition is blank`() {
        val blankId = FirebaseWordDto(id = "", word = "Test", definition = "Def")
        val blankWord = FirebaseWordDto(id = "1", word = "", definition = "Def")
        val blankDef = FirebaseWordDto(id = "1", word = "Test", definition = " ")

        assertFalse(blankId.isValid())
        assertFalse(blankWord.isValid())
        assertFalse(blankDef.isValid())
    }

    @Test
    fun `toEntity converts list synonyms and preserves existing user progress`() {
        val dto = FirebaseWordDto(
            id = "word_100",
            word = "Serendipity",
            definition = "Finding valuable things by chance",
            synonyms = listOf("fluke", "chance", "luck"),
            difficulty = "ADVANCED"
        )

        val existingEntity = WordEntity(
            id = "word_100",
            word = "Old Word",
            pronunciation = null,
            partOfSpeech = null,
            definition = "Old Def",
            simpleMeaning = null,
            example = null,
            synonyms = null,
            difficulty = "BEGINNER",
            category = null,
            audioUrl = null,
            isLearned = true,
            timesShown = 5,
            lastShownAt = 123456789L
        )

        val mappedEntity = dto.toEntity(existingEntity)

        assertEquals("word_100", mappedEntity.id)
        assertEquals("Serendipity", mappedEntity.word)
        assertEquals("fluke,chance,luck", mappedEntity.synonyms)
        assertEquals("ADVANCED", mappedEntity.difficulty)
        assertTrue(mappedEntity.isLearned)
        assertEquals(5, mappedEntity.timesShown)
        assertEquals(123456789L, mappedEntity.lastShownAt)
    }
}
