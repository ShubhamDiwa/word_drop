package com.diws.wordzip.util

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Helper to manage Google Play In-App Updates (Flexible & Immediate).
 */
class InAppUpdateHelper(private val activity: ComponentActivity) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    private var onFlexibleUpdateDownloadedCallback: (() -> Unit)? = null

    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                Log.w(TAG, "Update flow failed or cancelled! Result code: ${result.resultCode}")
            }
        }

    /**
     * Checks for available updates and starts the update flow if an update is found.
     * @param updateType [AppUpdateType.FLEXIBLE] (default) or [AppUpdateType.IMMEDIATE]
     * @param onFlexibleUpdateDownloaded optional callback when a flexible update finishes downloading
     */
    fun checkForUpdate(
        updateType: Int = AppUpdateType.FLEXIBLE,
        onFlexibleUpdateDownloaded: (() -> Unit)? = null
    ) {
        this.onFlexibleUpdateDownloadedCallback = onFlexibleUpdateDownloaded

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isAllowed = appUpdateInfo.isUpdateTypeAllowed(updateType)

            if (isAvailable && isAllowed) {
                if (updateType == AppUpdateType.FLEXIBLE) {
                    registerInstallStateListener()
                }
                startUpdateFlow(appUpdateInfo, updateType)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to check for in-app update", e)
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.newBuilder(updateType).build()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error starting update flow", e)
        }
    }

    private fun registerInstallStateListener() {
        if (installStateUpdatedListener != null) return

        installStateUpdatedListener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                onFlexibleUpdateDownloadedCallback?.invoke()
            }
        }
        installStateUpdatedListener?.let { appUpdateManager.registerListener(it) }
    }

    /**
     * Call this in Activity's onResume to handle resumed / in-progress updates.
     */
    fun onResume(updateType: Int = AppUpdateType.FLEXIBLE) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (updateType == AppUpdateType.IMMEDIATE) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startUpdateFlow(appUpdateInfo, AppUpdateType.IMMEDIATE)
                }
            } else if (updateType == AppUpdateType.FLEXIBLE) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    onFlexibleUpdateDownloadedCallback?.invoke()
                }
            }
        }
    }

    /**
     * Completes the flexible update and restarts the app to apply the update.
     */
    fun completeFlexibleUpdate() {
        appUpdateManager.completeUpdate()
    }

    /**
     * Unregisters the install listener to prevent memory leaks.
     */
    fun onDestroy() {
        installStateUpdatedListener?.let {
            appUpdateManager.unregisterListener(it)
            installStateUpdatedListener = null
        }
    }

    companion object {
        private const val TAG = "InAppUpdateHelper"
    }
}
