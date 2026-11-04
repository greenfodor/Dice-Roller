package com.greenfodor.diceroller.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.toSize

/** Size of the window the app is drawn in, from the [LocalWindowInfo] container size. */
@Composable
private fun windowDpSize(): DpSize {
    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize
    return with(density) { containerSize.toSize().toDpSize() }
}

/** [WindowSizeClass] of the window the app is drawn in. */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun currentWindowSizeClass(): WindowSizeClass = WindowSizeClass.calculateFromSize(windowDpSize())

/** Whether the window the app is drawn in is wider than it is tall. */
@Composable
fun isLandscapeWindow(): Boolean {
    val size = windowDpSize()
    return size.width > size.height
}
