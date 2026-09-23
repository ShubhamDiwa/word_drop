package com.diws.wordzip.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Utility to perform subtle, tactile haptic feedback for user clicks and interactions.
 */
object HapticFeedbackHelper {

    fun performClick(view: View? = null, context: Context? = null) {
        try {
            // 1. Primary: Use Android View haptics (hardware-tuned tactile click)
            view?.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
            )

            // 2. Guaranteed fallback/reinforcement: Vibrator service ensures a crisp tactile click
            // is physically felt on every device regardless of system touch haptics slider
            val ctx = context ?: view?.context
            if (ctx != null) {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    manager?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }

                vibrator?.let {
                    if (it.hasVibrator()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            it.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            it.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            it.vibrate(20)
                        }
                    }
                }
            }
        } catch (_: Throwable) {
            // Failsafe on devices without vibrator or during tests
        }
    }

    /**
     * Celebratory rhythm haptic pulse (tap-tap-BUZZ!) for 100% quiz scores & level-ups.
     */
    fun performCelebrationFanfare(view: View? = null, context: Context? = null) {
        try {
            view?.performHapticFeedback(
                HapticFeedbackConstants.CONFIRM,
                HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
            )

            val ctx = context ?: view?.context
            if (ctx != null) {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    manager?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }

                vibrator?.let {
                    if (it.hasVibrator()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val timings = longArrayOf(0, 45, 75, 45, 90, 180)
                            val amplitudes = intArrayOf(0, 180, 0, 210, 0, 255)
                            it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                        } else {
                            @Suppress("DEPRECATION")
                            it.vibrate(longArrayOf(0, 45, 75, 45, 90, 180), -1)
                        }
                    }
                }
            }
        } catch (_: Throwable) {
        }
    }
}
