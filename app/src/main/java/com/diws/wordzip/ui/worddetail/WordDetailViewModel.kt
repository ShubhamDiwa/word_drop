package com.diws.wordzip.ui.worddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.wordzip.data.preferences.UserPreferencesRepository
import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.repository.WordRepository
import com.diws.wordzip.domain.usecase.GetWordUseCase
import com.diws.wordzip.domain.usecase.MarkWordLearnedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordDetailUiState(
    val isLoading: Boolean = true,
    val word: Word? = null,
    val selectedMeaningLanguages: List<String> = emptyList(),
    val isTranslating: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    private val getWordUseCase: GetWordUseCase,
    private val markWordLearnedUseCase: MarkWordLearnedUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val wordRepository: WordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val wordId: String = savedStateHandle.get<String>("wordId") ?: ""
    private val _isTranslating = kotlinx.coroutines.flow.MutableStateFlow(false)

    val uiState: StateFlow<WordDetailUiState> = combine(
        getWordUseCase(wordId),
        userPreferencesRepository.selectedMeaningLanguagesFlow,
        _isTranslating
    ) { word, selectedLanguages, isTranslating ->
        if (word != null) {
            WordDetailUiState(
                isLoading = false,
                word = word,
                selectedMeaningLanguages = selectedLanguages,
                isTranslating = isTranslating
            )
        } else {
            WordDetailUiState(isLoading = false, error = "Word not found")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WordDetailUiState(isLoading = true)
    )

    init {
        if (wordId.isNotEmpty()) {
            // Immediately start listening for selected languages and fetch translation
            viewModelScope.launch(Dispatchers.IO) {
                userPreferencesRepository.selectedMeaningLanguagesFlow.collect { languages ->
                    val lang = languages.firstOrNull()?.trim()?.lowercase() ?: ""
                    if (lang.isNotBlank() && !lang.equals("en", ignoreCase = true)) {
                        fetchTranslation(lang)
                    }
                }
            }

            // Background refresh of dictionary API for audio/pronunciation without blocking
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    getWordUseCase.refresh(wordId)
                } catch (_: Exception) {}
            }
        }
    }

    fun fetchTranslation(languageCode: String) {
        if (wordId.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val cleanLang = languageCode.trim().lowercase()
            if (cleanLang.isBlank() || cleanLang == "en") return@launch
            try {
                _isTranslating.value = true
                wordRepository.getOrFetchTranslation(wordId, cleanLang)
            } finally {
                _isTranslating.value = false
            }
        }
    }

    fun toggleLearned() {
        val currentWord = uiState.value.word ?: return
        viewModelScope.launch {
            markWordLearnedUseCase(currentWord.id, !currentWord.isLearned)
        }
    }
}

