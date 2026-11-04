package com.greenfodor.diceroller.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** Always `null`: desktop has no dynamic color scheme. */
@Composable
internal actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null

/** No-op on desktop. */
@Composable
internal actual fun SystemBarAppearance(darkTheme: Boolean) = Unit
