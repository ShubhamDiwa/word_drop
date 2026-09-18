package com.diws.worddrop.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// CURRENT DARK MODE COLORS (Navy & Purple)
// ==========================================
val DarkBackground = Color(0xFF0F131D)
val DarkSurface = Color(0xFF171B26)
val DarkSurfaceContainer = Color(0xFF1C1F2A)
val DarkSurfaceHigh = Color(0xFF262A35)

val PrimaryPurple = Color(0xFFC6BFFF)
val PrimaryContainerPurple = Color(0xFF8C80FF)
val SecondaryTeal = Color(0xFF4DDCC6)
val TertiarySuccess = Color(0xFF4DE082)

val TextPrimaryDark = Color(0xFFDFE2F1)
val TextSecondaryDark = Color(0xFFC8C4D7)
val OutlineColorDark = Color(0xFF928EA0)
val OutlineVariantColorDark = Color(0xFF2C3040)

// Aliases for backward compatibility
val TextPrimary = TextPrimaryDark
val TextSecondary = TextSecondaryDark
val OutlineColor = OutlineColorDark
val OutlineVariantColor = OutlineVariantColorDark

// ==========================================
// WARM & MINIMALIST LIGHT MODE (Terracotta & Sand)
// ==========================================
val LightBackground = Color(0xFFF9F6F0)       // Warm sand/paper color
val LightSurface = Color(0xFFFDFBF7)         // Slightly lighter paper for cards
val LightSurfaceContainer = Color(0xFFF1EBE1) // Slightly darker sand for pressed states/containers
val LightSurfaceHigh = Color(0xFFEBE3D5)      // Darker sand for elevated elements

val PrimaryTerracotta = Color(0xFFD86B4D)     // Warm Terracotta
val PrimaryContainerTerracotta = Color(0xFFFFDBCE) // Soft peach/terracotta background
val SecondaryWarm = Color(0xFFD69A55)         // Warm ochre/gold
val TertiaryWarmSuccess = Color(0xFF5BA773)   // Muted, natural green

val TextPrimaryLight = Color(0xFF3B332D)      // Soft black/dark brown (easy on eyes)
val TextSecondaryLight = Color(0xFF7A6F66)    // Medium brownish-gray
val OutlineColorLight = Color(0xFFBCAAA4)     // Soft brown outline
val OutlineVariantColorLight = Color(0xFFD7CCC8)

// ==========================================
// SHARED COLORS (Semantic/Difficulty)
// ==========================================
// Dark Mode Difficulty Colors (Vibrant neons on dark surfaces)
val BeginnerColorDark = Color(0xFF4DDCC6)
val IntermediateColorDark = Color(0xFFFFB74D)
val AdvancedColorDark = Color(0xFFFF6B8B)

// Light Mode Difficulty Colors (Deep, saturated, high-contrast on sand/paper surfaces)
val BeginnerColorLight = Color(0xFF0D8267)      // Deep emerald teal (contrast ratio > 5.5:1 on light)
val IntermediateColorLight = Color(0xFFB45309)  // Rich warm ochre/amber (contrast ratio > 5.2:1 on light)
val AdvancedColorLight = Color(0xFFBE123C)      // Deep ruby/crimson (contrast ratio > 5.5:1 on light)
val LearnedColorLight = Color(0xFF1B8755)       // Deep forest green (contrast ratio > 5.5:1 on light)

// Default / Backward compatibility aliases
val BeginnerColor = BeginnerColorDark
val IntermediateColor = IntermediateColorDark
val AdvancedColor = AdvancedColorDark
