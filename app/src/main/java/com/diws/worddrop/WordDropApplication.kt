package com.diws.worddrop

import android.app.Application
import android.util.Log
import com.diws.worddrop.domain.repository.WordRepository
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class WordDropApplication : Application() {

    @Inject
    lateinit var wordRepository: WordRepository

    override fun onCreate() {
        super.onCreate()
        try {
            // ✅ setRequestConfiguration MUST be called before initialize()
            val testConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(
                    AdRequest.DEVICE_ID_EMULATOR,
                    // TODO: Replace with your real device test ID from Logcat.
                    // Look for: "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("XXXX"))"
                    // "ADD_YOUR_DEVICE_TEST_ID_HERE"
                ))
                .build()
            MobileAds.setRequestConfiguration(testConfig)

            MobileAds.initialize(this) { status ->
                Log.d("AdMob", "MobileAds initialized: ${status.adapterStatusMap}")
            }
        } catch (e: Exception) {
            Log.e("AdMob", "Error initializing MobileAds", e)
        }

        CoroutineScope(Dispatchers.IO).launch {
            wordRepository.seedInitialDataIfNeeded()
            // syncVocabularyWithRemote() is already launched in the background
            // inside seedInitialDataIfNeeded() — no need to call it again here.
        }
    }
}
