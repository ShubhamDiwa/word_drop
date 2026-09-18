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
import com.diws.worddrop.ui.theme.DarkSurfaceContainer
import com.diws.worddrop.ui.theme.DarkSurfaceHigh
import com.diws.worddrop.ui.theme.PrimaryPurple
import com.diws.worddrop.ui.theme.SecondaryTeal
import com.diws.worddrop.ui.theme.TextSecondary

@Composable
fun NotificationPreview(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
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
                    color = PrimaryPurple
                )
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceHigh)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabButton(text = "Collapsed", selected = !isExpanded) { isExpanded = false }
                    TabButton(text = "Expanded", selected = isExpanded) { isExpanded = true }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161325)),
                border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.3f))
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
                                Text(text = "⚡ ", fontSize = 11.sp)
                                Text(
                                    text = "WORD DROP",
                                    color = Color(0xFFC6BFFF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = " • DAILY",
                                    color = Color(0xFF8C95A7),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            DifficultyPill(difficulty = WordDifficulty.ADVANCED)
                        }

                        Text(
                            text = "EPHEMERAL",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "Lasting for a very short time.",
                            color = Color(0xFFE2E8F0),
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
                                Text(text = "⚡ ", fontSize = 12.sp, color = Color(0xFFC4B5FD))
                                Text(
                                    text = "word drop",
                                    color = Color(0xFFC4B5FD),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " · daily drop",
                                    color = Color(0xFF6B6D76),
                                    fontSize = 12.sp
                                )
                            }
                            DifficultyPill(difficulty = WordDifficulty.INTERMEDIATE)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(24.dp)
                                    .background(SecondaryTeal)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "EPHEMERAL",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "/ɪˈfemərəl/",
                                    color = SecondaryTeal,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Text(
                            text = "Lasting for a very short period of time.",
                            color = Color(0xFFE4E4E7),
                            fontSize = 14.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF221F35), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "\"The excitement of the discovery was ephemeral.\"",
                                color = Color(0xFF9A9CA6),
                                fontSize = 12.5.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            /*Text(
                                text = "🔥 7 day streak",
                                color = Color(0xFFFAC775),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )*/
                            Text(
                                text = "tap to open →",
                                color = SecondaryTeal,
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
            .background(if (selected) PrimaryPurple else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) DarkBackground else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DifficultyPill(difficulty: WordDifficulty) {
    val (bgColor, textColor) = when (difficulty) {
        WordDifficulty.BEGINNER -> Pair(Color(0xFFE2E8F0), Color(0xFF1E293B))
        WordDifficulty.INTERMEDIATE -> Pair(Color(0xFF3B3378), Color(0xFFC4B5FD))
        WordDifficulty.ADVANCED -> Pair(Color(0xFF4C1D95), Color(0xFFF3E8FF))
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
