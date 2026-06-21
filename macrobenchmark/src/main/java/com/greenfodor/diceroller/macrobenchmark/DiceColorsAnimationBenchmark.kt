package com.greenfodor.diceroller.macrobenchmark

import android.os.SystemClock
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Frame-timing benchmark for the dice-colors single↔per-die toggle animation.
 *
 * Run: ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class DiceColorsAnimationBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun toggleSingleColor() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            navigateToDiceColors()
        }
    ) {
        repeat(TOGGLES_PER_ITERATION) {
            val switch = device.wait(Until.findObject(By.checkable(true)), UI_TIMEOUT_MS)
            checkNotNull(switch) { "Could not find the single-color switch" }
            switch.click()
            SystemClock.sleep(ANIMATION_SETTLE_MS)
        }
    }

    private fun MacrobenchmarkScope.navigateToDiceColors() {
        val settings = device.wait(Until.findObject(By.desc(OPEN_SETTINGS_DESC)), UI_TIMEOUT_MS)
        checkNotNull(settings) { "Could not find the open-settings action" }
        settings.click()

        val diceColorsRow = device.wait(Until.findObject(By.text(DICE_COLORS_TITLE)), UI_TIMEOUT_MS)
        checkNotNull(diceColorsRow) { "Could not find the Dice colors row" }
        diceColorsRow.click()

        device.wait(Until.hasObject(By.checkable(true)), UI_TIMEOUT_MS)
    }

    private companion object {
        const val TARGET_PACKAGE = "com.greenfodor.diceroller"
        const val OPEN_SETTINGS_DESC = "Open settings"
        const val DICE_COLORS_TITLE = "Dice colors"

        const val ITERATIONS = 10
        const val TOGGLES_PER_ITERATION = 6
        const val ANIMATION_SETTLE_MS = 700L
        const val UI_TIMEOUT_MS = 5_000L
    }
}
