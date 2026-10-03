package com.diws.wordzip.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MeaningLanguageTest {

    @Test
    fun `supported languages include major Indian languages`() {
        val languages = MeaningLanguage.SUPPORTED_LANGUAGES
        assertTrue(languages.size >= 11)

        val codes = languages.map { it.code }
        assertTrue(codes.contains("hi"))
        assertTrue(codes.contains("gu"))
        assertTrue(codes.contains("mr"))
        assertTrue(codes.contains("bn"))
        assertTrue(codes.contains("te"))
        assertTrue(codes.contains("ta"))
        assertTrue(codes.contains("kn"))
        assertTrue(codes.contains("ml"))
        assertTrue(codes.contains("pa"))
        assertTrue(codes.contains("or"))
        assertTrue(codes.contains("ur"))

        val hindi = languages.find { it.code == "hi" }
        assertNotNull(hindi)
        assertEquals("Hindi", hindi?.englishName)
        assertEquals("हिन्दी", hindi?.nativeName)

        val gujarati = languages.find { it.code == "gu" }
        assertNotNull(gujarati)
        assertEquals("Gujarati", gujarati?.englishName)
        assertEquals("ગુજરાતી", gujarati?.nativeName)
    }

    @Test
    fun `fromCode resolves case-insensitively`() {
        assertEquals("Hindi", MeaningLanguage.fromCode("HI")?.englishName)
        assertEquals("Hindi", MeaningLanguage.fromCode("hi")?.englishName)
        assertEquals("Gujarati", MeaningLanguage.fromCode("GU")?.englishName)
        assertEquals("Gujarati", MeaningLanguage.fromCode("gu")?.englishName)
        assertEquals("Marathi", MeaningLanguage.fromCode("MR")?.englishName)
        assertEquals("Bengali", MeaningLanguage.fromCode("bn")?.englishName)
        assertNull(MeaningLanguage.fromCode("unknown"))
    }
}
