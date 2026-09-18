package com.diws.worddrop.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.diws.worddrop.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Manages loading and displaying AdMob Interstitial Ads (e.g. before launching a quiz).
 */
object InterstitialAdManager {
    private const val TAG = "InterstitialAd"
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun loadAd(context: Context, adUnitId: String = BuildConfig.ADMOB_INTERSTITIAL_ID) {
        if (interstitialAd != null || isLoading) return
        isLoading = true

        InterstitialAd.load(
            context.applicationContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d(TAG, "Interstitial ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.e(TAG, "Failed to load interstitial ad: ${error.message}")
                }
            }
        )
    }

    fun showAd(activity: Activity?, onAdClosed: () -> Unit) {
        val ad = interstitialAd
        if (activity != null && ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd(activity)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.e(TAG, "Failed to show interstitial ad: ${error.message}")
                    interstitialAd = null
                    loadAd(activity)
                    onAdClosed()
                }
            }
            ad.show(activity)
        } else {
            // If ad is not ready yet, continue seamlessly without blocking user
            if (activity != null) {
                loadAd(activity)
            }
            onAdClosed()
        }
    }
}
