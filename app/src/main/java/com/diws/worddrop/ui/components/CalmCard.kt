package com.diws.worddrop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.compose.material3.MaterialTheme

@Composable
fun CalmCard(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    cornerRadius: Dp = 20.dp,
    content: @Composable () -> Unit
) {
    val cardModifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(cornerRadius))
        .background(backgroundColor)
        .border(1.dp, borderColor.copy(alpha = 0.25f), RoundedCornerShape(cornerRadius))
        .let { if (onClick != null) it.clickable { onClick() } else it }

    Box(
        modifier = cardModifier.padding(20.dp)
    ) {
        content()
    }
}
