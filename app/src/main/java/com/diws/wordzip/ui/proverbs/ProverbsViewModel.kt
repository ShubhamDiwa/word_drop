package com.diws.wordzip.ui.proverbs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diws.wordzip.domain.model.Proverb
import com.diws.wordzip.domain.repository.ProverbRepository
import com.diws.wordzip.domain.usecase.GetProverbsUseCase
import com.diws.wordzip.domain.usecase.ToggleProverbFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProverbsUiState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val showFavoritesOnly: Boolean = false,
    val proverbs: List<Proverb> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false
)

@HiltViewModel
class ProverbsViewModel @Inject constructor(
    private val getProverbsUseCase: GetProverbsUseCase,
    private val toggleProverbFavoriteUseCase: ToggleProverbFavoriteUseCase,
    private val proverbRepository: ProverbRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _showFavoritesOnly = MutableStateFlow(false)
    private val _isSyncing = MutableStateFlow(false)

    val uiState: StateFlow<ProverbsUiState> = combine(
        getProverbsUseCase(),
        _searchQuery,
        _selectedCategory,
        _showFavoritesOnly,
        _isSyncing
    ) { allProverbs, query, category, favoritesOnly, syncing ->
        val categories = allProverbs.map { it.category }.distinct().sorted()

        val filtered = allProverbs.filter { proverb ->
            val matchesQuery = query.isBlank() ||
                    proverb.englishText.contains(query, ignoreCase = true) ||
                    proverb.hindiText.contains(query, ignoreCase = true) ||
                    (proverb.hindiEquivalent?.contains(query, ignoreCase = true) == true) ||
                    proverb.meaningEnglish.contains(query, ignoreCase = true) ||
                    proverb.meaningHindi.contains(query, ignoreCase = true)

            val matchesCategory = category == null || proverb.category.equals(category, ignoreCase = true)
            val matchesFavorites = !favoritesOnly || proverb.isFavorite

            matchesQuery && matchesCategory && matchesFavorites
        }

        ProverbsUiState(
            searchQuery = query,
            selectedCategory = category,
            showFavoritesOnly = favoritesOnly,
            proverbs = filtered,
            availableCategories = categories,
            isLoading = false,
            isSyncing = syncing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProverbsUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun toggleFavorite(proverb: Proverb) {
        viewModelScope.launch {
            toggleProverbFavoriteUseCase(proverb.id, !proverb.isFavorite)
        }
    }

    fun syncWithRemote() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                proverbRepository.syncProverbsWithRemote()
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
