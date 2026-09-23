package com.diws.wordzip.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.diws.wordzip.data.preferences.UserPreferencesRepository
import com.diws.wordzip.notification.NotificationScheduler
import com.diws.wordzip.ui.components.BottomNavBar
import com.diws.wordzip.ui.home.HomeScreen
import com.diws.wordzip.ui.onboarding.OnboardingScreen
import com.diws.wordzip.ui.practice.PracticeScreen
import com.diws.wordzip.ui.progress.ProgressScreen
import com.diws.wordzip.ui.settings.SettingsScreen
import com.diws.wordzip.ui.vocabulary.VocabularyScreen
import com.diws.wordzip.ui.worddetail.WordDetailScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// ---------- Animation constants ----------

private const val TRANSITION_DURATION = 250
private const val HOME_ENTER_DURATION = 350

@Composable
fun AppNavigation(
    navController: NavHostController,
    pendingWordId: String? = null,
    onWordConsumed: () -> Unit = {}
) {
    val context = LocalContext.current

    // Read the onboarding flag synchronously on first composition to avoid any flash.
    val hasSeenOnboarding = remember {
        runBlocking {
            UserPreferencesRepository(context).hasSeenOnboardingFlow.first()
        }
    }

    val startDestination = if (hasSeenOnboarding) Screen.Home.route else Screen.Onboarding.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Vocabulary.route,
        Screen.Progress.route,
        Screen.Settings.route
    )

    LaunchedEffect(pendingWordId, navBackStackEntry) {
        if (pendingWordId != null && navBackStackEntry != null) {
            navController.navigate(Screen.WordDetail.createRoute(pendingWordId)) {
                launchSingleTop = true
            }
            onWordConsumed()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            // Default transitions for bottom bar destinations and general routes (instantaneous, no lag)
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            // ── Onboarding ────────────────────────────────────────────────
            composable(
                route = Screen.Onboarding.route,
                // Onboarding itself fades in normally on first launch
                enterTransition = {
                    fadeIn(tween(TRANSITION_DURATION))
                },
                // When navigating away (→ Home), onboarding shrinks + fades out
                exitTransition = {
                    scaleOut(
                        targetScale = 0.88f,
                        animationSpec = tween(HOME_ENTER_DURATION, easing = FastOutSlowInEasing)
                    ) + fadeOut(tween(HOME_ENTER_DURATION, easing = FastOutSlowInEasing))
                }
            ) {
                OnboardingScreen(
                    onFinished = { notificationTimes ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val repo = UserPreferencesRepository(context)
                            repo.markOnboardingSeen()
                            repo.updateNotificationTimes(notificationTimes.sorted())
                            NotificationScheduler.scheduleDailyNotifications(
                                context, notificationTimes.sorted()
                            )
                        }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Home ──────────────────────────────────────────────────────
            composable(
                route = Screen.Home.route,
                // When coming from Onboarding → Home rises up from below + fades in
                enterTransition = {
                    if (initialState.destination.route == Screen.Onboarding.route) {
                        slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight / 5 },
                            animationSpec = tween(HOME_ENTER_DURATION, easing = FastOutSlowInEasing)
                        ) + scaleIn(
                            initialScale = 0.94f,
                            animationSpec = tween(HOME_ENTER_DURATION, easing = FastOutSlowInEasing)
                        ) + fadeIn(tween(HOME_ENTER_DURATION, easing = FastOutSlowInEasing))
                    } else {
                        EnterTransition.None
                    }
                },
                exitTransition = {
                    ExitTransition.None
                },
                popEnterTransition = {
                    EnterTransition.None
                },
                popExitTransition = {
                    ExitTransition.None
                }
            ) {
                HomeScreen(
                    onNavigateToDetail = { wordId ->
                        navController.navigate(Screen.WordDetail.createRoute(wordId))
                    },
                    onNavigateToPractice = {
                        navController.navigate(Screen.Practice.route)
                    }
                )
            }

            // ── Vocabulary ────────────────────────────────────────────────
            composable(Screen.Vocabulary.route) {
                VocabularyScreen(
                    onNavigateToDetail = { wordId ->
                        navController.navigate(Screen.WordDetail.createRoute(wordId))
                    }
                )
            }

            // ── Word Detail ───────────────────────────────────────────────
            composable(
                route = Screen.WordDetail.route,
                arguments = listOf(navArgument("wordId") { type = NavType.StringType }),
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(TRANSITION_DURATION))
                },
                popExitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it / 6 },
                        animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing)
                    ) + fadeOut(tween(TRANSITION_DURATION))
                }
            ) {
                WordDetailScreen(
                    onBackClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ── Practice & Quiz ───────────────────────────────────────────
            composable(
                route = Screen.Practice.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(TRANSITION_DURATION))
                },
                popExitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it / 6 },
                        animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing)
                    ) + fadeOut(tween(TRANSITION_DURATION))
                }
            ) {
                PracticeScreen(
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ── Progress ──────────────────────────────────────────────────
            composable(Screen.Progress.route) {
                ProgressScreen()
            }

            // ── Settings ──────────────────────────────────────────────────
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
