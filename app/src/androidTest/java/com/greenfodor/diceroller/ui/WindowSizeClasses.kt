package com.greenfodor.diceroller.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/** Portrait phone window: compact width, so the dice screen keeps its floating action button. */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
val CompactWindowSizeClass: WindowSizeClass =
    WindowSizeClass.calculateFromSize(DpSize(width = 400.dp, height = 800.dp))
