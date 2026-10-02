package com.diws.wordzip.ui.worddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.wordzip.data.preferences.UserPreferencesRepository
import com.diws.wordzip.domain.model.Word
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
    val error: String? = null
)

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    private val getWordUseCase: GetWordUseCase,
    private val markWordLearnedUseCase: MarkWordLearnedUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val wordId: String = savedStateHandle.get<String>("wordId") ?: ""

    val uiState: StateFlow<WordDetailUiState> = combine(
        getWordUseCase(wordId),
        userPreferencesRepository.selectedMeaningLanguagesFlow
    ) { word, selectedLanguages ->
        if (word != null) {
            WordDetailUiState(
                isLoading = false,
                word = word,
                selectedMeaningLanguages = selectedLanguages
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
            viewModelScope.launch(Dispatchers.IO) {
                getWordUseCase.refresh(wordId)
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
