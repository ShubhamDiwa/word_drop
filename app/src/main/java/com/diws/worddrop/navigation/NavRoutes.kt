package com.diws.worddrop.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Vocabulary : Screen("vocabulary")
    object WordDetail : Screen("word_detail/{wordId}") {
        fun createRoute(wordId: String) = "word_detail/$wordId"
    }
    object Progress : Screen("progress")
    object Settings : Screen("settings")
    object Practice : Screen("practice")
}
