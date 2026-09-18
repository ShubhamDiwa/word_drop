package com.diws.worddrop.domain.model

data class UserLevelInfo(
    val level: Int,
    val title: String,
    val currentXp: Int,
    val xpForCurrentLevel: Int,
    val xpForNextLevel: Int,
    val progress: Float
)

object XpLevelManager {

    fun getLevelInfo(totalXp: Int): UserLevelInfo {
        return when {
            totalXp < 100 -> UserLevelInfo(
                level = 1,
                title = "Word Novice",
                currentXp = totalXp,
                xpForCurrentLevel = 0,
                xpForNextLevel = 100,
                progress = (totalXp.toFloat() / 100f).coerceIn(0f, 1f)
            )
            totalXp < 300 -> UserLevelInfo(
                level = 2,
                title = "Vocab Explorer",
                currentXp = totalXp,
                xpForCurrentLevel = 100,
                xpForNextLevel = 300,
                progress = ((totalXp - 100).toFloat() / 200f).coerceIn(0f, 1f)
            )
            totalXp < 700 -> UserLevelInfo(
                level = 3,
                title = "Word Smith",
                currentXp = totalXp,
                xpForCurrentLevel = 300,
                xpForNextLevel = 700,
                progress = ((totalXp - 300).toFloat() / 400f).coerceIn(0f, 1f)
            )
            totalXp < 1500 -> UserLevelInfo(
                level = 4,
                title = "Linguist",
                currentXp = totalXp,
                xpForCurrentLevel = 700,
                xpForNextLevel = 1500,
                progress = ((totalXp - 700).toFloat() / 800f).coerceIn(0f, 1f)
            )
            else -> UserLevelInfo(
                level = 5,
                title = "Vocabulary Master",
                currentXp = totalXp,
                xpForCurrentLevel = 1500,
                xpForNextLevel = 2500,
                progress = 1f
            )
        }
    }
}
