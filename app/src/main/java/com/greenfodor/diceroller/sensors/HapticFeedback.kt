package com.greenfodor.diceroller.sensors

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

fun performRollHaptics(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (vibrator.hasVibrator().not()) return

    vibrator.vibrate(
        VibrationEffect.createWaveform(
            longArrayOf(0, 20, 104, 20, 114, 25, 122, 25, 141, 30, 159, 35, 196, 40, 232, 80),
            intArrayOf(0, 60, 0, 80, 0, 100, 0, 120, 0, 140, 0, 160, 0, 180, 0, 255),
            -1
        )
    )
}