package com.diws.wordzip.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.wordzip.domain.model.Word
import com.diws.wordzip.domain.model.WordDifficulty
import com.diws.wordzip.domain.repository.WordRepository
import com.diws.wordzip.domain.usecase.GetVocabularyUseCase
import com.diws.wordzip.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LearnedFilter {
    ALL,
    LEARNED,
    UNLEARNED
}

data class VocabularyUiState(
    val searchQuery: String = "",
    val selectedDifficulty: WordDifficulty? = null,
    val selectedLearnedFilter: LearnedFilter = LearnedFilter.ALL,
    val words: List<Word> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val getVocabularyUseCase: GetVocabularyUseCase,
    private val wordRepository: WordRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedDifficulty = MutableStateFlow<WordDifficulty?>(null)
    private val _selectedLearnedFilter = MutableStateFlow(LearnedFilter.ALL)
    private val _isRefreshing = MutableStateFlow(false)
    private val _isOffline = MutableStateFlow(!networkMonitor.isCurrentlyConnected())
    private val _errorMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                val wasOffline = _isOffline.value
                _isOffline.value = !isOnline
                if (isOnline) {
                    if (wasOffline) {
                        _errorMessage.value = null
                    }
                    // Auto-sync when network reconnects
                    refreshVocabularyFromFirebase()
                } else {
                    _errorMessage.value = "No internet connection. Offline mode."
                }
            }
        }
        refreshVocabularyFromFirebase()
    }

    val uiState: StateFlow<VocabularyUiState> = combine(
        combine(
            _searchQuery,
            _searchQuery.debounce(300L),
            _selectedDifficulty,
            _selectedLearnedFilter
        ) { rawQuery, debouncedQuery, difficulty, learnedFilter ->
            FiltersTuple(rawQuery, debouncedQuery, difficulty, learnedFilter)
        },
        combine(
            _isRefreshing,
            _isOffline,
            _errorMessage
        ) { isRefreshing, isOffline, errorMessage ->
            StatusTuple(isRefreshing, isOffline, errorMessage)
        },
        getVocabularyUseCase()
    ) { filters, status, allWords ->
        val filtered = allWords.filter { word ->
            val matchesQuery = filters.debouncedQuery.isBlank() ||
                    word.word.contains(filters.debouncedQuery, ignoreCase = true) ||
                    word.definition.contains(filters.debouncedQuery, ignoreCase = true)

            val matchesDifficulty = filters.difficulty == null || word.difficulty == filters.difficulty

            val matchesLearned = when (filters.learnedFilter) {
                LearnedFilter.ALL -> true
                LearnedFilter.LEARNED -> word.isLearned
                LearnedFilter.UNLEARNED -> !word.isLearned
            }

            matchesQuery && matchesDifficulty && matchesLearned
        }

        VocabularyUiState(
            searchQuery = filters.rawQuery,
            selectedDifficulty = filters.difficulty,
            selectedLearnedFilter = filters.learnedFilter,
            words = filtered,
            isLoading = false,
            isRefreshing = status.isRefreshing,
            isOffline = status.isOffline,
            errorMessage = status.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VocabularyUiState(
            isLoading = true,
            isOffline = !networkMonitor.isCurrentlyConnected()
        )
    )

    fun refreshVocabularyFromFirebase() {
        viewModelScope.launch {
            if (!networkMonitor.isCurrentlyConnected()) {
                _isOffline.value = true
                _errorMessage.value = "No internet connection. Please check your network and try again."
                return@launch
            }
            _isRefreshing.value = true
            _isOffline.value = false
            _errorMessage.value = null

            val result = wordRepository.syncVocabularyWithRemote()
            result.onFailure { error ->
                _errorMessage.value = if (!networkMonitor.isCurrentlyConnected()) {
                    "No internet connection. Showing offline vocabulary."
                } else {
                    error.localizedMessage ?: "Failed to sync vocabulary. Please try again."
                }
            }
            _isRefreshing.value = false
        }
    }

    fun dismissErrorMessage() {
        _errorMessage.value = null
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onDifficultyFilterSelected(difficulty: WordDifficulty?) {
        _selectedDifficulty.value = difficulty
    }

    fun onLearnedFilterSelected(filter: LearnedFilter) {
        _selectedLearnedFilter.value = filter
    }

    private data class FiltersTuple(
        val rawQuery: String,
        val debouncedQuery: String,
        val difficulty: WordDifficulty?,
        val learnedFilter: LearnedFilter
    )

    private data class StatusTuple(
        val isRefreshing: Boolean,
        val isOffline: Boolean,
        val errorMessage: String?
    )
}
