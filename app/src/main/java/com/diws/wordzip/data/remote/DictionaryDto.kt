package com.diws.wordzip.data.remote

import com.google.gson.annotations.SerializedName

data class DictionaryEntryDto(
    @SerializedName("word") val word: String?,
    @SerializedName("phonetic") val phonetic: String?,
    @SerializedName("phonetics") val phonetics: List<PhoneticDto>?,
    @SerializedName("meanings") val meanings: List<MeaningDto>?
)

data class PhoneticDto(
    @SerializedName("text") val text: String?,
    @SerializedName("audio") val audio: String?
)

data class MeaningDto(
    @SerializedName("partOfSpeech") val partOfSpeech: String?,
    @SerializedName("definitions") val definitions: List<DefinitionDto>?,
    @SerializedName("synonyms") val synonyms: List<String>?
)

data class DefinitionDto(
    @SerializedName("definition") val definition: String?,
    @SerializedName("example") val example: String?,
    @SerializedName("synonyms") val synonyms: List<String>?
)
