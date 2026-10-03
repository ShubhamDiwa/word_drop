package com.diws.wordzip.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateHelper(private val activity: Activity) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private val updateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
        }
    }

    init {
        try {
            appUpdateManager.registerListener(updateListener)
        } catch (e: Exception) {
            Log.e("InAppUpdateHelper", "Failed to register listener", e)
        }
    }

    fun checkForUpdate(updateType: Int = AppUpdateType.IMMEDIATE) {
        try {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(updateType)
                ) {
                    try {
                        appUpdateManager.startUpdateFlow(
                            appUpdateInfo,
                            activity,
                            AppUpdateOptions.newBuilder(updateType).build()
                        )
                    } catch (e: Exception) {
                        Log.e("InAppUpdateHelper", "Failed to start update flow", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("InAppUpdateHelper", "Error checking for update", e)
        }
    }

    fun onResume(updateType: Int = AppUpdateType.IMMEDIATE) {
        try {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (updateType == AppUpdateType.IMMEDIATE &&
                    appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    try {
                        appUpdateManager.startUpdateFlow(
                            appUpdateInfo,
                            activity,
                            AppUpdateOptions.newBuilder(updateType).build()
                        )
                    } catch (e: Exception) {
                        Log.e("InAppUpdateHelper", "Failed to resume update", e)
                    }
                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    appUpdateManager.completeUpdate()
                }
            }
        } catch (e: Exception) {
            Log.e("InAppUpdateHelper", "Error in onResume", e)
        }
    }

    fun onDestroy() {
        try {
            appUpdateManager.unregisterListener(updateListener)
        } catch (e: Exception) {
            Log.e("InAppUpdateHelper", "Error unregistering listener", e)
        }
    }
}
