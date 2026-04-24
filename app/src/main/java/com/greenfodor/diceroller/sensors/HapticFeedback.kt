package com.greenfodor.diceroller.sensors

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

private val waveformTimings = longArrayOf(0, 20, 104, 20, 114, 25, 122, 25, 141, 30, 159, 35, 196, 40, 232, 80)
private val waveformAmplitudes = intArrayOf(0, 60, 0, 80, 0, 100, 0, 120, 0, 140, 0, 160, 0, 180, 0, 255)

fun Context.performRollHaptics() {
    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) return

    val vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

    if (vibrator.hasVibrator().not()) return

    vibrator.vibrate(
        VibrationEffect.createWaveform(
            // timings =
            waveformTimings,
            // amplitudes =
            waveformAmplitudes,
            // repeat =
            -1,
        ),
    )
}
