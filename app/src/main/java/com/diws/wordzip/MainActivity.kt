package com.diws.wordzip

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.diws.wordzip.data.preferences.UserPreferencesRepository
import com.diws.wordzip.data.preferences.UserSettings
import com.diws.wordzip.navigation.AppNavigation
import com.diws.wordzip.navigation.Screen
import com.diws.wordzip.notification.VocabularyNotificationManager
import com.diws.wordzip.ui.theme.WordZipTheme
import com.diws.wordzip.util.HapticFeedbackHelper
import com.diws.wordzip.util.InAppUpdateHelper
import com.google.android.play.core.install.model.AppUpdateType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private var pendingWordId by mutableStateOf<String?>(null)
    private lateinit var gestureDetector: GestureDetector
    private lateinit var inAppUpdateHelper: InAppUpdateHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission result handled silently
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        inAppUpdateHelper = InAppUpdateHelper(this)
        inAppUpdateHelper.checkForUpdate(updateType = AppUpdateType.IMMEDIATE)

        window.decorView.isHapticFeedbackEnabled = true

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                HapticFeedbackHelper.performClick(window.decorView, this@MainActivity)
                return false
            }
        })

        pendingWordId = extractWordIdFromIntent(intent)
        com.diws.wordzip.widget.WordWidgetProvider.updateAllWidgets(this)
        com.diws.wordzip.util.InterstitialAdManager.loadAd(this)
        requestNotificationPermissionIfNeeded()

        setContent {
            val initialTheme = userPreferencesRepository.getCachedTheme()
            val userSettings by userPreferencesRepository.userSettingsFlow.collectAsStateWithLifecycle(
                initialValue = UserSettings(appTheme = initialTheme)
            )

            WordZipTheme(appTheme = userSettings.appTheme) {
                val controller = rememberNavController()

                AppNavigation(
                    navController = controller,
                    pendingWordId = pendingWordId,
                    onWordConsumed = { pendingWordId = null }
                )
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            gestureDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val wordId = extractWordIdFromIntent(intent)
        if (wordId != null) {
            pendingWordId = wordId
        }
    }

    override fun onResume() {
        super.onResume()
        if (::inAppUpdateHelper.isInitialized) {
            inAppUpdateHelper.onResume(updateType = AppUpdateType.IMMEDIATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::inAppUpdateHelper.isInitialized) {
            inAppUpdateHelper.onDestroy()
        }
    }

    private fun extractWordIdFromIntent(intent: Intent?): String? {
        return intent?.getStringExtra(VocabularyNotificationManager.EXTRA_WORD_ID)
            ?: intent?.getStringExtra("word_id")
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
