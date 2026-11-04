package com.greenfodor.diceroller.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * The platform's dynamic (wallpaper-based) [ColorScheme] for the given theme, or `null` when the
 * platform has none.
 */
@Composable
internal expect fun dynamicColorScheme(darkTheme: Boolean): ColorScheme?

/** Styles the platform system bars to contrast with a light or dark app theme. */
@Composable
internal expect fun SystemBarAppearance(darkTheme: Boolean)
