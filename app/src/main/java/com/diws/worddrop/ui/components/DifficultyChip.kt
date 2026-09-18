package com.diws.worddrop.ui.components

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
import com.diws.worddrop.domain.model.WordDifficulty
import com.diws.worddrop.ui.theme.AdvancedColor
import com.diws.worddrop.ui.theme.BeginnerColor
import com.diws.worddrop.ui.theme.IntermediateColor

@Composable
fun DifficultyChip(
    difficulty: WordDifficulty,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val (chipColor, label) = when (difficulty) {
        WordDifficulty.BEGINNER -> BeginnerColor to "BEGINNER"
        WordDifficulty.INTERMEDIATE -> IntermediateColor to "INTERMEDIATE"
        WordDifficulty.ADVANCED -> AdvancedColor to "ADVANCED"
    }

    val backgroundColor = if (isSelected) chipColor.copy(alpha = 0.25f) else chipColor.copy(alpha = 0.12f)
    val borderColor = if (isSelected) chipColor else Color.Transparent

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
