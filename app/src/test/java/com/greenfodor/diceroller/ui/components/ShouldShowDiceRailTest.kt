package com.greenfodor.diceroller.ui.components

import android.content.res.Configuration
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShouldShowDiceRailTest {
    @Test
    fun anExpandedLandscapeWindowShowsTheRail() {
        assertTrue(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Expanded,
                orientation = Configuration.ORIENTATION_LANDSCAPE
            )
        )
    }

    @Test
    fun anExpandedPortraitWindowDoesNotShowTheRail() {
        assertFalse(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Expanded,
                orientation = Configuration.ORIENTATION_PORTRAIT
            )
        )
    }

    @Test
    fun aCompactLandscapeWindowDoesNotShowTheRail() {
        assertFalse(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Compact,
                orientation = Configuration.ORIENTATION_LANDSCAPE
            )
        )
    }

    @Test
    fun aMediumLandscapeWindowDoesNotShowTheRail() {
        assertFalse(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Medium,
                orientation = Configuration.ORIENTATION_LANDSCAPE
            )
        )
    }

    @Test
    fun aCompactPortraitWindowDoesNotShowTheRail() {
        assertFalse(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Compact,
                orientation = Configuration.ORIENTATION_PORTRAIT
            )
        )
    }
}
