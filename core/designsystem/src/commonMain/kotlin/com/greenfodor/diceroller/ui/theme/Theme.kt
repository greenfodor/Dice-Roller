package com.greenfodor.diceroller.ui.theme

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.greenfodor.diceroller.data.DiceColorSettings
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.ThemeMode
import com.greenfodor.diceroller.ui.DiceConstants

@Immutable
data class DiceColors(
    val byTarget: Map<DieColorTarget, Color> = emptyMap()
) {
    /** The resolved color for [target], or [Color.Unspecified] if none is provided. */
    fun colorFor(target: DieColorTarget): Color = byTarget[target] ?: Color.Unspecified
}

val LocalDiceColors = staticCompositionLocalOf { DiceColors() }

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
private fun getTargetColorScheme(
    isDark: Boolean,
    dynamicColor: Boolean
): ColorScheme = (if (dynamicColor) dynamicColorScheme(isDark) else null)
    ?: if (isDark) DarkColorScheme else LightColorScheme

@Composable
private fun Transition<Boolean>.animateColorProp(
    label: String,
    dynamicColor: Boolean,
    prop: (ColorScheme) -> Color
): Color {
    val duration = DiceConstants.THEME_TRANSITION_DURATION_MILLIS
    return animateColor(
        transitionSpec = { tween(duration) },
        label = label
    ) { isDark ->
        prop(getTargetColorScheme(isDark, dynamicColor))
    }.value
}

/**
 * Animates all colors in the [ColorScheme] based on the theme transition.
 *
 * This ensures that when the user toggles dark mode, the entire UI smoothly
 * transitions between color palettes instead of snapping instantly.
 */
@Composable
private fun Transition<Boolean>.animateColorScheme(dynamicColor: Boolean): ColorScheme {
    val target = getTargetColorScheme(targetState, dynamicColor)

    return target.copy(
        primary = animateColorProp("primary", dynamicColor) { it.primary },
        onPrimary = animateColorProp("onPrimary", dynamicColor) { it.onPrimary },
        primaryContainer = animateColorProp("primaryContainer", dynamicColor) { it.primaryContainer },
        onPrimaryContainer = animateColorProp("onPrimaryContainer", dynamicColor) { it.onPrimaryContainer },
        inversePrimary = animateColorProp("inversePrimary", dynamicColor) { it.inversePrimary },
        secondary = animateColorProp("secondary", dynamicColor) { it.secondary },
        onSecondary = animateColorProp("onSecondary", dynamicColor) { it.onSecondary },
        secondaryContainer = animateColorProp("secondaryContainer", dynamicColor) { it.secondaryContainer },
        onSecondaryContainer = animateColorProp("onSecondaryContainer", dynamicColor) { it.onSecondaryContainer },
        tertiary = animateColorProp("tertiary", dynamicColor) { it.tertiary },
        onTertiary = animateColorProp("onTertiary", dynamicColor) { it.onTertiary },
        tertiaryContainer = animateColorProp("tertiaryContainer", dynamicColor) { it.tertiaryContainer },
        onTertiaryContainer = animateColorProp("onTertiaryContainer", dynamicColor) { it.onTertiaryContainer },
        background = animateColorProp("background", dynamicColor) { it.background },
        onBackground = animateColorProp("onBackground", dynamicColor) { it.onBackground },
        surface = animateColorProp("surface", dynamicColor) { it.surface },
        onSurface = animateColorProp("onSurface", dynamicColor) { it.onSurface },
        surfaceVariant = animateColorProp("surfaceVariant", dynamicColor) { it.surfaceVariant },
        onSurfaceVariant = animateColorProp("onSurfaceVariant", dynamicColor) { it.onSurfaceVariant },
        surfaceTint = animateColorProp("surfaceTint", dynamicColor) { it.surfaceTint },
        inverseSurface = animateColorProp("inverseSurface", dynamicColor) { it.inverseSurface },
        inverseOnSurface = animateColorProp("inverseOnSurface", dynamicColor) { it.inverseOnSurface },
        error = animateColorProp("error", dynamicColor) { it.error },
        onError = animateColorProp("onError", dynamicColor) { it.onError },
        errorContainer = animateColorProp("errorContainer", dynamicColor) { it.errorContainer },
        onErrorContainer = animateColorProp("onErrorContainer", dynamicColor) { it.onErrorContainer },
        outline = animateColorProp("outline", dynamicColor) { it.outline },
        outlineVariant = animateColorProp("outlineVariant", dynamicColor) { it.outlineVariant },
        scrim = animateColorProp("scrim", dynamicColor) { it.scrim }
    )
}

/**
 * Animated theme for the Dice Roller app.
 *
 * It supports dynamic color (on Android 12+) and smoothly animates color changes
 * when switching between light and dark themes. It also provides [LocalDiceColors]
 * and [LocalSpacing] to the composition tree.
 *
 * @param darkTheme Whether the app should use a dark color scheme.
 * @param dynamicColor Whether to use dynamic color from the system (Android 12+).
 * @param diceColorSettings The user's per-die color configuration, resolved per theme.
 * @param content The composable content to be themed.
 */
@Composable
fun DiceRollerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    diceColorSettings: DiceColorSettings = DiceColorSettings(),
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = darkTheme, label = "ThemeTransition")
    val duration = DiceConstants.THEME_TRANSITION_DURATION_MILLIS

    val colorScheme = transition.animateColorScheme(dynamicColor)

    val diceColors =
        DiceColors(
            byTarget = DieColorTarget.entries.associateWith { target ->
                val option = diceColorSettings.optionFor(target)
                transition
                    .animateColor(label = "dice_${target.name}", transitionSpec = { tween(duration) }) { isDark ->
                        option.toColor(isDark)
                    }.value
            }
        )

    CompositionLocalProvider(
        LocalDiceColors provides diceColors,
        LocalSpacing provides Spacing(),
        LocalDiceSpecs provides DiceSpecs()
    ) {
        SystemBarAppearance(darkTheme)
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Resolves a [ThemeMode] to whether dark colors should be used.
 * [ThemeMode.FOLLOW_SYSTEM] defers to the device setting via [isSystemInDarkTheme].
 */
@Composable
fun resolveDarkTheme(themeMode: ThemeMode): Boolean = when (themeMode) {
    ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}
