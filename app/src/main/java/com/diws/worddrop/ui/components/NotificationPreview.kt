package com.diws.worddrop.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diws.worddrop.domain.model.WordDifficulty
import com.diws.worddrop.ui.theme.DarkBackground
import com.diws.worddrop.ui.theme.SecondaryTeal

@Composable
fun NotificationPreview(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isDark = MaterialTheme.colorScheme.background == DarkBackground

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notification Preview",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabButton(text = "Collapsed", selected = !isExpanded) { isExpanded = false }
                    TabButton(text = "Expanded", selected = isExpanded) { isExpanded = true }
                }
            }

            val cardBg = if (isDark) Color(0xFF161325) else Color(0xFFFDFBF7)
            val cardBorder = if (isDark) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            } else {
                BorderStroke(1.dp, Color(0xFFD86B4D).copy(alpha = 0.35f))
            }
            val brandColor = if (isDark) Color(0xFFC6BFFF) else Color(0xFFD86B4D)
            val brandSubtitleColor = if (isDark) Color(0xFF8C95A7) else Color(0xFF7A6F66)
            val titleColor = if (isDark) Color.White else Color(0xFF1C1917)
            val meaningColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF44403C)
            val accentColor = if (isDark) SecondaryTeal else Color(0xFFD86B4D)
            val definitionColor = if (isDark) Color(0xFFE4E4E7) else Color(0xFF292524)
            val quoteBg = if (isDark) Color(0xFF221F35) else Color(0xFFF1EBE1)
            val quoteTextColor = if (isDark) Color(0xFF9A9CA6) else Color(0xFF57534E)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = cardBorder
            ) {
                if (!isExpanded) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⚡ ", fontSize = 11.sp, color = brandColor)
                                Text(
                                    text = "WORDZIP",
                                    color = brandColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = " • DAILY",
                                    color = brandSubtitleColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            DifficultyPill(difficulty = WordDifficulty.ADVANCED, isDark = isDark)
                        }

                        Text(
                            text = "EPHEMERAL",
                            color = titleColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "Lasting for a very short time.",
                            color = meaningColor,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⚡ ", fontSize = 12.sp, color = brandColor)
                                Text(
                                    text = "wordzip",
                                    color = brandColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " · daily zip",
                                    color = brandSubtitleColor,
                                    fontSize = 12.sp
                                )
                            }
                            DifficultyPill(difficulty = WordDifficulty.INTERMEDIATE, isDark = isDark)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(24.dp)
                                    .background(accentColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "EPHEMERAL",
                                    color = titleColor,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "/ɪˈfemərəl/",
                                    color = accentColor,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Text(
                            text = "Lasting for a very short period of time.",
                            color = definitionColor,
                            fontSize = 14.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(quoteBg, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "\"The excitement of the discovery was ephemeral.\"",
                                color = quoteTextColor,
                                fontSize = 12.5.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "tap to open →",
                                color = accentColor,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DifficultyPill(difficulty: WordDifficulty, isDark: Boolean = true) {
    val (bgColor, textColor) = when (difficulty) {
        WordDifficulty.BEGINNER -> if (isDark) Pair(Color(0xFF234E39), Color(0xFFE2E8F0)) else Pair(Color(0xFFE6F4EA), Color(0xFF0D8267))
        WordDifficulty.INTERMEDIATE -> if (isDark) Pair(Color(0xFF3B3378), Color(0xFFC4B5FD)) else Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        WordDifficulty.ADVANCED -> if (isDark) Pair(Color(0xFF4C1D95), Color(0xFFF3E8FF)) else Pair(Color(0xFFFFE4E6), Color(0xFFBE123C))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = difficulty.name,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
