package com.diws.wordzip.ui.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.diws.wordzip.domain.model.WordDifficulty
import com.diws.wordzip.ui.components.AppLoader
import com.diws.wordzip.ui.components.BannerAd
import com.diws.wordzip.ui.components.WordCard
import com.diws.wordzip.ui.theme.AdvancedColorDark
import com.diws.wordzip.ui.theme.AdvancedColorLight
import com.diws.wordzip.ui.theme.BeginnerColorDark
import com.diws.wordzip.ui.theme.BeginnerColorLight
import com.diws.wordzip.ui.theme.DarkBackground
import com.diws.wordzip.ui.theme.DarkSurfaceContainer
import com.diws.wordzip.ui.theme.DarkSurfaceHigh
import com.diws.wordzip.ui.theme.IntermediateColorDark
import com.diws.wordzip.ui.theme.IntermediateColorLight
import com.diws.wordzip.ui.theme.LearnedColorLight
import com.diws.wordzip.ui.theme.PrimaryPurple
import com.diws.wordzip.ui.theme.TertiarySuccess
import com.diws.wordzip.ui.theme.TextPrimaryDark
import com.diws.wordzip.ui.theme.TextSecondaryDark

@Composable
fun VocabularyScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: VocabularyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage
        if (message != null) {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Retry",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.refreshVocabularyFromFirebase()
            }
        }
    }

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

            Spacer(modifier = Modifier.height(10.dp))

            // No Internet / Offline Warning Banner
            AnimatedVisibility(
                visible = state.isOffline || state.errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0xFF382313) else Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFFB45309) else Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WifiOff,
                            contentDescription = "No internet connection",
                            tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = state.errorMessage ?: "No internet connection. Offline mode.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { viewModel.refreshVocabularyFromFirebase() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Retry",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
                            )
                        }
                    }
                }
            }

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
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (state.isOffline || state.errorMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDark) Color(0xFF382313) else Color(0xFFFEF3C7)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.WifiOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(34.dp),
                                        tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No Internet Connection",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Please connect to the internet to download words and sync vocabulary.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { viewModel.refreshVocabularyFromFirebase() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Retry",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        )
    }
}

@Composable
private fun filterChipColors(
    activeColor: Color = MaterialTheme.colorScheme.primary,
    isDark: Boolean = MaterialTheme.colorScheme.background == DarkBackground
) = FilterChipDefaults.filterChipColors(
    containerColor = if (isDark) DarkSurfaceContainer else MaterialTheme.colorScheme.surface,
    labelColor = if (isDark) TextPrimaryDark else Color(0xFF1C1917),
    iconColor = if (isDark) TextSecondaryDark else Color(0xFF1C1917),
    selectedContainerColor = activeColor,
    selectedLabelColor = Color.White,
    selectedLeadingIconColor = Color.White
)
