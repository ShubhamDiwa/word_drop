package com.diws.wordzip.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diws.wordzip.domain.model.WordDifficulty
import com.diws.wordzip.ui.theme.AdvancedColorDark
import com.diws.wordzip.ui.theme.AdvancedColorLight
import com.diws.wordzip.ui.theme.BeginnerColorDark
import com.diws.wordzip.ui.theme.BeginnerColorLight
import com.diws.wordzip.ui.theme.DarkBackground
import com.diws.wordzip.ui.theme.IntermediateColorDark
import com.diws.wordzip.ui.theme.IntermediateColorLight

@Composable
fun DifficultyChip(
    difficulty: WordDifficulty,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val (chipColor, label) = when (difficulty) {
        WordDifficulty.BEGINNER -> (if (isDark) BeginnerColorDark else BeginnerColorLight) to "BEGINNER"
        WordDifficulty.INTERMEDIATE -> (if (isDark) IntermediateColorDark else IntermediateColorLight) to "INTERMEDIATE"
        WordDifficulty.ADVANCED -> (if (isDark) AdvancedColorDark else AdvancedColorLight) to "ADVANCED"
    }

    val backgroundColor = if (isSelected) {
        chipColor.copy(alpha = if (isDark) 0.25f else 0.18f)
    } else {
        chipColor.copy(alpha = if (isDark) 0.12f else 0.10f)
    }
    val borderColor = if (isSelected) chipColor else chipColor.copy(alpha = if (isDark) 0.3f else 0.25f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = chipColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
