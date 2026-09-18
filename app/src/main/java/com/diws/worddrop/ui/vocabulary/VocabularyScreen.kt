package com.diws.worddrop.ui.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diws.worddrop.domain.model.WordDifficulty
import com.diws.worddrop.ui.components.AppLoader
import com.diws.worddrop.ui.components.BannerAd
import com.diws.worddrop.ui.components.WordCard
import com.diws.worddrop.ui.theme.AdvancedColor
import com.diws.worddrop.ui.theme.BeginnerColor
import com.diws.worddrop.ui.theme.DarkBackground
import com.diws.worddrop.ui.theme.DarkSurfaceContainer
import com.diws.worddrop.ui.theme.DarkSurfaceHigh
import com.diws.worddrop.ui.theme.IntermediateColor
import com.diws.worddrop.ui.theme.PrimaryContainerPurple
import com.diws.worddrop.ui.theme.PrimaryPurple
import com.diws.worddrop.ui.theme.SecondaryTeal
import com.diws.worddrop.ui.theme.TextPrimary
import com.diws.worddrop.ui.theme.TextSecondary

@Composable
fun VocabularyScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: VocabularyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vocabulary",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                IconButton(
                    onClick = { viewModel.refreshVocabularyFromFirebase() },
                    enabled = !state.isRefreshing
                ) {
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = PrimaryPurple,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Sync Firebase Vocabulary",
                            tint = PrimaryPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search words...", color = TextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceContainer,
                    unfocusedContainerColor = DarkSurfaceContainer,
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = DarkSurfaceHigh,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Difficulty Filters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = state.selectedDifficulty == null,
                        onClick = { viewModel.onDifficultyFilterSelected(null) },
                        label = { Text("All") },
                        colors = filterChipColors()
                    )
                }
                items(WordDifficulty.entries.toTypedArray()) { diff ->
                    val diffColor = when (diff) {
                        WordDifficulty.BEGINNER     -> BeginnerColor
                        WordDifficulty.INTERMEDIATE -> IntermediateColor
                        WordDifficulty.ADVANCED     -> AdvancedColor
                        else                        -> PrimaryContainerPurple
                    }
                    FilterChip(
                        selected = state.selectedDifficulty == diff,
                        onClick = {
                            viewModel.onDifficultyFilterSelected(
                                if (state.selectedDifficulty == diff) null else diff
                            )
                        },
                        label = { Text(diff.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = filterChipColors(activeColor = diffColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Learned Status Filter
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(LearnedFilter.entries.toTypedArray()) { filter ->
                    FilterChip(
                        selected = state.selectedLearnedFilter == filter,
                        onClick = { viewModel.onLearnedFilterSelected(filter) },
                        label = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = filterChipColors(activeColor = SecondaryTeal)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading) {
                AppLoader(subtitle = "Syncing vocabulary...")
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (state.words.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No words found",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "Try adjusting your search or filters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.words, key = { it.id }) { word ->
                                WordCard(
                                    word = word,
                                    onClick = { onNavigateToDetail(word.id) }
                                )
                            }
                        }
                    }
                }

                // Fixed Banner Ad at the bottom of the screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    BannerAd()
                }
            }
        }
    }
}

@Composable
private fun filterChipColors(
    activeColor: androidx.compose.ui.graphics.Color = PrimaryContainerPurple
) = FilterChipDefaults.filterChipColors(
    // Unselected: dark surface, muted label
    containerColor = DarkSurfaceContainer,
    labelColor = TextSecondary,
    // Selected: fully filled with accent color + white label for max contrast
    selectedContainerColor = activeColor,
    selectedLabelColor = DarkBackground
)
