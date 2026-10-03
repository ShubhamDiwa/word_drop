package com.diws.wordzip.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordTranslationTest {

    private val sampleWord = Word(
        id = "word_2",
        word = "RESILIENT",
        pronunciation = "/rɪˈzɪliənt/",
        partOfSpeech = "ADJECTIVE",
        definition = "Able to withstand or recover quickly from difficult conditions.",
        simpleMeaning = "Able to bounce back from failure or hardship.",
        translations = mapOf(
            "hi" to "कठिन परिस्थितियों से जल्दी उबरने वाला।",
            "gu" to "મુશ્કેલ પરિસ્થિતિમાંથી ઝડપથી બહાર આવનાર.",
            "mr" to "संकटातून लवकर सावरणारा व खंबीर राहणारा.",
            "bn" to "কঠিন পরিস্থিতি থেকে দ্রুত ঘুরে দাঁড়াতে সক্ষম।"
        )
    )

    @Test
    fun `single language - Hindi selected returns Hindi meaning`() {
        val selectedLanguages = listOf("hi")
        val resolved = sampleWord.resolveMeanings(selectedLanguages)

        assertEquals(1, resolved.size)
        assertEquals("hi", resolved[0].languageCode)
        assertEquals("Hindi", resolved[0].languageName)
        assertEquals("कठिन परिस्थितियों से जल्दी उबरने वाला।", resolved[0].meaning)
        assertFalse(resolved[0].isFallback)
    }

    @Test
    fun `single language - Marathi selected returns Marathi meaning`() {
        val selectedLanguages = listOf("mr")
        val resolved = sampleWord.resolveMeanings(selectedLanguages)

        assertEquals(1, resolved.size)
        assertEquals("mr", resolved[0].languageCode)
        assertEquals("Marathi", resolved[0].languageName)
        assertEquals("संकटातून लवकर सावरणारा व खंबीर राहणारा.", resolved[0].meaning)
        assertFalse(resolved[0].isFallback)
    }

    @Test
    fun `single language - Bengali selected returns Bengali meaning`() {
        val selectedLanguages = listOf("bn")
        val resolved = sampleWord.resolveMeanings(selectedLanguages)

        assertEquals(1, resolved.size)
        assertEquals("bn", resolved[0].languageCode)
        assertEquals("Bengali", resolved[0].languageName)
        assertEquals("কঠিন পরিস্থিতি থেকে দ্রুত ঘুরে দাঁড়াতে সক্ষম।", resolved[0].meaning)
        assertFalse(resolved[0].isFallback)
    }

    @Test
    fun `single language - Gujarati selected returns Gujarati meaning`() {
        val selectedLanguages = listOf("gu")
        val resolved = sampleWord.resolveMeanings(selectedLanguages)

        assertEquals(1, resolved.size)
        assertEquals("gu", resolved[0].languageCode)
        assertEquals("Gujarati", resolved[0].languageName)
        assertEquals("મુશ્કેલ પરિસ્થિતિમાંથી ઝડપથી બહાર આવનાર.", resolved[0].meaning)
        assertFalse(resolved[0].isFallback)
    }

    @Test
    fun `multiple languages - Hindi and Gujarati selected returns both meanings`() {
        val selectedLanguages = listOf("hi", "gu")
        val resolved = sampleWord.resolveMeanings(selectedLanguages)

        assertEquals(2, resolved.size)
        assertEquals("Hindi", resolved[0].languageName)
        assertEquals("कठिन परिस्थितियों से जल्दी उबरने वाला।", resolved[0].meaning)

        assertEquals("Gujarati", resolved[1].languageName)
        assertEquals("મુશ્કેલ પરિસ્થિતિમાંથી ઝડપથી બહાર આવનાર.", resolved[1].meaning)
    }

    @Test
    fun `language removal - Hindi and Gujarati selected then remove Hindi leaves Gujarati`() {
        val initialSelection = mutableListOf("hi", "gu")
        initialSelection.remove("hi")

        val resolved = sampleWord.resolveMeanings(initialSelection)
        assertEquals(1, resolved.size)
        assertEquals("gu", resolved[0].languageCode)
        assertEquals("Gujarati", resolved[0].languageName)
        assertEquals("મુશ્કેલ પરિસ્થિતિમાંથી ઝડપથી બહાર આવનાર.", resolved[0].meaning)
    }

    @Test
    fun `translation fallback - when translation is missing falls back to English meaning`() {
        val wordWithOnlyHindi = Word(
            id = "word_test",
            word = "CANDID",
            definition = "Truthful and straightforward; frank.",
            simpleMeaning = "Honest and open without holding back.",
            translations = mapOf("hi" to "स्पष्टवादी, खरा और निष्कपट।")
        )

        // Request Hindi and Gujarati (Gujarati is missing)
        val resolved = wordWithOnlyHindi.resolveMeanings(listOf("hi", "gu"))

        assertEquals(2, resolved.size)
        // Hindi is found
        assertEquals("hi", resolved[0].languageCode)
        assertEquals("स्पष्टवादी, खरा और निष्कपट।", resolved[0].meaning)
        assertFalse(resolved[0].isFallback)

        // Gujarati gracefully falls back to English simpleMeaning / definition
        assertEquals("gu", resolved[1].languageCode)
        assertEquals("Honest and open without holding back.", resolved[1].meaning)
        assertTrue(resolved[1].isFallback)
    }

    @Test
    fun `fallback when no languages selected returns English meaning`() {
        val resolved = sampleWord.resolveMeanings(emptyList())

        assertEquals(1, resolved.size)
        assertEquals("en", resolved[0].languageCode)
        assertEquals("Able to bounce back from failure or hardship.", resolved[0].meaning)
    }

    @Test
    fun `English word always remains in English`() {
        val selectedLanguages = listOf("hi", "gu")
        val resolved = sampleWord.resolveMeanings(selectedLanguages)

        // English word string is never translated or modified
        assertEquals("RESILIENT", sampleWord.word)
        assertEquals(2, resolved.size)
    }

    @Test
    fun `notification formatting for single and multiple languages`() {
        // Single language notification text
        val singleResult = sampleWord.resolveNotificationMeaning(listOf("hi"))
        assertEquals("कठिन परिस्थितियों से जल्दी उबरने वाला।", singleResult)

        // Multiple languages notification text
        val multiResult = sampleWord.resolveNotificationMeaning(listOf("hi", "gu"))
        assertTrue(multiResult.contains("Hindi: कठिन परिस्थितियों से जल्दी उबरने वाला।"))
        assertTrue(multiResult.contains("Gujarati: મુશ્કેલ પરિસ્થિતિમાંથી ઝડપથી બહાર આવનાર."))

        // Empty selection fallback
        val emptyResult = sampleWord.resolveNotificationMeaning(emptyList())
        assertEquals("Able to bounce back from failure or hardship.", emptyResult)
    }
}
