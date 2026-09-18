package com.diws.worddrop.ui.worddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.domain.usecase.GetWordUseCase
import com.diws.worddrop.domain.usecase.MarkWordLearnedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordDetailUiState(
    val isLoading: Boolean = true,
    val word: Word? = null,
    val error: String? = null
)

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    private val getWordUseCase: GetWordUseCase,
    private val markWordLearnedUseCase: MarkWordLearnedUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val wordId: String = savedStateHandle.get<String>("wordId") ?: ""

    val uiState: StateFlow<WordDetailUiState> = getWordUseCase(wordId)
        .map { word ->
            if (word != null) {
                WordDetailUiState(isLoading = false, word = word)
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
