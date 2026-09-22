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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diws.worddrop.domain.model.WordDifficulty
import com.diws.worddrop.ui.components.AppLoader
import com.diws.worddrop.ui.components.BannerAd
import com.diws.worddrop.ui.components.WordCard
import com.diws.worddrop.ui.theme.AdvancedColorDark
import com.diws.worddrop.ui.theme.AdvancedColorLight
import com.diws.worddrop.ui.theme.BeginnerColorDark
import com.diws.worddrop.ui.theme.BeginnerColorLight
import com.diws.worddrop.ui.theme.DarkBackground
import com.diws.worddrop.ui.theme.DarkSurfaceContainer
import com.diws.worddrop.ui.theme.DarkSurfaceHigh
import com.diws.worddrop.ui.theme.IntermediateColorDark
import com.diws.worddrop.ui.theme.IntermediateColorLight
import com.diws.worddrop.ui.theme.LearnedColorLight
import com.diws.worddrop.ui.theme.PrimaryPurple
import com.diws.worddrop.ui.theme.TertiarySuccess
import com.diws.worddrop.ui.theme.TextPrimaryDark
import com.diws.worddrop.ui.theme.TextSecondaryDark

@Composable
fun VocabularyScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: VocabularyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(
                    onClick = { viewModel.refreshVocabularyFromFirebase() },
                    enabled = !state.isRefreshing
                ) {
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Sync Firebase Vocabulary",
                            tint = MaterialTheme.colorScheme.primary
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
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                placeholder = {
                    Text(
                        text = "Search words...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            val isDark = MaterialTheme.colorScheme.background == DarkBackground

            // Difficulty Filters
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isSelected = state.selectedDifficulty == null
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onDifficultyFilterSelected(null) },
                        label = {
                            Text(
                                text = "All",
                                color = if (isSelected) Color.White else if (isDark) TextPrimaryDark else Color(0xFF1C1917),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                            )
                        },
                        colors = filterChipColors(
                            activeColor = MaterialTheme.colorScheme.primary,
                            isDark = isDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isDark) DarkSurfaceHigh else Color(0xFFD4C9BC),
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
                items(WordDifficulty.entries.toTypedArray()) { diff ->
                    val isSelected = state.selectedDifficulty == diff
                    val diffColor = when (diff) {
                        WordDifficulty.BEGINNER     -> if (isDark) BeginnerColorDark else BeginnerColorLight
                        WordDifficulty.INTERMEDIATE -> if (isDark) IntermediateColorDark else IntermediateColorLight
                        WordDifficulty.ADVANCED     -> if (isDark) AdvancedColorDark else AdvancedColorLight
                        else                        -> MaterialTheme.colorScheme.primary
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.onDifficultyFilterSelected(
                                if (state.selectedDifficulty == diff) null else diff
                            )
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else diffColor)
                            )
                        },
                        label = {
                            Text(
                                text = diff.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (isSelected) Color.White else if (isDark) TextPrimaryDark else Color(0xFF1C1917),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                            )
                        },
                        colors = filterChipColors(activeColor = diffColor, isDark = isDark),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isDark) DarkSurfaceHigh else Color(0xFFD4C9BC),
                            selectedBorderColor = diffColor,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
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
                    val isSelected = state.selectedLearnedFilter == filter
                    val filterColor = when (filter) {
                        LearnedFilter.ALL -> MaterialTheme.colorScheme.primary
                        LearnedFilter.LEARNED -> if (isDark) TertiarySuccess else LearnedColorLight
                        LearnedFilter.UNLEARNED -> if (isDark) PrimaryPurple else MaterialTheme.colorScheme.secondary
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onLearnedFilterSelected(filter) },
                        leadingIcon = if (filter == LearnedFilter.LEARNED) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else filterColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        label = {
                            Text(
                                text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (isSelected) Color.White else if (isDark) TextPrimaryDark else Color(0xFF1C1917),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                            )
                        },
                        colors = filterChipColors(activeColor = filterColor, isDark = isDark),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isDark) DarkSurfaceHigh else Color(0xFFD4C9BC),
                            selectedBorderColor = filterColor,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Try adjusting your search or filters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
    activeColor: Color = MaterialTheme.colorScheme.primary,
    isDark: Boolean = MaterialTheme.colorScheme.background == DarkBackground
) = FilterChipDefaults.filterChipColors(
    // Unselected: crisp surface with clear deep dark text
    containerColor = if (isDark) DarkSurfaceContainer else MaterialTheme.colorScheme.surface,
    labelColor = if (isDark) TextPrimaryDark else Color(0xFF1C1917),
    iconColor = if (isDark) TextSecondaryDark else Color(0xFF1C1917),
    // Selected: filled with vibrant active color and pure white text
    selectedContainerColor = activeColor,
    selectedLabelColor = Color.White,
    selectedLeadingIconColor = Color.White
)
