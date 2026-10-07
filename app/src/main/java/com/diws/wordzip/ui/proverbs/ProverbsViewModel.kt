package com.diws.wordzip.ui.proverbs

import android.util.Log
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

enum class ProverbTab {
    ALL,
    FAVORITES
}

data class ProverbsUiState(
    val searchQuery: String = "",
    val selectedTab: ProverbTab = ProverbTab.ALL,
    val proverbs: List<Proverb> = emptyList(),
    val totalCount: Int = 0,
    val favoritesCount: Int = 0,
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
    private val _selectedTab = MutableStateFlow(ProverbTab.ALL)
    private val _isSyncing = MutableStateFlow(false)

    val uiState: StateFlow<ProverbsUiState> = combine(
        getProverbsUseCase(),
        _searchQuery,
        _selectedTab,
        _isSyncing
    ) { allProverbs, query, selectedTab, syncing ->
        Log.d("ProverbsViewModel", "UI proverb count = ${allProverbs.size}")
        val favoritesCount = allProverbs.count { it.isFavorite }
        val totalCount = allProverbs.size

        val tabFiltered = when (selectedTab) {
            ProverbTab.ALL -> allProverbs
            ProverbTab.FAVORITES -> allProverbs.filter { it.isFavorite }
        }

        val filtered = tabFiltered.filter { proverb ->
            query.isBlank() ||
                    proverb.englishText.contains(query, ignoreCase = true) ||
                    proverb.hindiText.contains(query, ignoreCase = true) ||
                    (proverb.hindiEquivalent?.contains(query, ignoreCase = true) == true) ||
                    proverb.meaningEnglish.contains(query, ignoreCase = true) ||
                    proverb.meaningHindi.contains(query, ignoreCase = true)
        }

        ProverbsUiState(
            searchQuery = query,
            selectedTab = selectedTab,
            proverbs = filtered,
            totalCount = totalCount,
            favoritesCount = favoritesCount,
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

    fun setTab(tab: ProverbTab) {
        _selectedTab.value = tab
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
