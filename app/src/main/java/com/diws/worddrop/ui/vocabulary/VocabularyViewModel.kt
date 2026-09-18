package com.diws.worddrop.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.worddrop.domain.model.Word
import com.diws.worddrop.domain.model.WordDifficulty
import com.diws.worddrop.domain.repository.WordRepository
import com.diws.worddrop.domain.usecase.GetVocabularyUseCase
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
    val isRefreshing: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val getVocabularyUseCase: GetVocabularyUseCase,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedDifficulty = MutableStateFlow<WordDifficulty?>(null)
    private val _selectedLearnedFilter = MutableStateFlow(LearnedFilter.ALL)
    private val _isRefreshing = MutableStateFlow(false)

    init {
        refreshVocabularyFromFirebase()
    }

    val uiState: StateFlow<VocabularyUiState> = combine(
        _searchQuery,
        _searchQuery.debounce(300L),
        _selectedDifficulty,
        _selectedLearnedFilter,
        _isRefreshing
    ) { rawQuery, debouncedQuery, difficulty, learnedFilter, isRefreshing ->
        Tuple5(rawQuery, debouncedQuery, difficulty, learnedFilter, isRefreshing)
    }.combine(
        getVocabularyUseCase()
    ) { (rawQuery, debouncedQuery, difficulty, learnedFilter, isRefreshing), allWords ->
        val filtered = allWords.filter { word ->
            val matchesQuery = debouncedQuery.isBlank() ||
                    word.word.contains(debouncedQuery, ignoreCase = true) ||
                    word.definition.contains(debouncedQuery, ignoreCase = true)

            val matchesDifficulty = difficulty == null || word.difficulty == difficulty

            val matchesLearned = when (learnedFilter) {
                LearnedFilter.ALL -> true
                LearnedFilter.LEARNED -> word.isLearned
                LearnedFilter.UNLEARNED -> !word.isLearned
            }

            matchesQuery && matchesDifficulty && matchesLearned
        }

        VocabularyUiState(
            searchQuery = rawQuery,
            selectedDifficulty = difficulty,
            selectedLearnedFilter = learnedFilter,
            words = filtered,
            isLoading = false,
            isRefreshing = isRefreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VocabularyUiState(isLoading = true)
    )

    fun refreshVocabularyFromFirebase() {
        viewModelScope.launch {
            _isRefreshing.value = true
            wordRepository.syncVocabularyWithRemote()
            _isRefreshing.value = false
        }
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

    private data class Tuple5<A, B, C, D, E>(
        val a: A, val b: B, val c: C, val d: D, val e: E
    )
}
