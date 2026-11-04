package com.greenfodor.diceroller.ui.components

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShouldShowDiceRailTest {
    @Test
    fun anExpandedLandscapeWindowShowsTheRail() {
        assertTrue(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Expanded,
                isLandscape = true
            )
        )
    }

    @Test
    fun anExpandedPortraitWindowDoesNotShowTheRail() {
        assertFalse(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Expanded,
                isLandscape = false
            )
        )
    }

    @Test
    fun aCompactLandscapeWindowDoesNotShowTheRail() {
        assertFalse(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Compact,
                isLandscape = true
            )
        )
    }

    @Test
    fun aMediumLandscapeWindowDoesNotShowTheRail() {
        assertFalse(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Medium,
                isLandscape = true
            )
        )
    }

    @Test
    fun aCompactPortraitWindowDoesNotShowTheRail() {
        assertFalse(
            shouldShowDiceRail(
                widthSizeClass = WindowWidthSizeClass.Compact,
                isLandscape = false
            )
        )
    }
}
