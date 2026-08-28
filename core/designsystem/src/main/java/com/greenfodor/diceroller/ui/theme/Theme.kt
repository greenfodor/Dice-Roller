package com.greenfodor.diceroller.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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

private fun getTargetColorScheme(
    isDark: Boolean,
    dynamicColor: Boolean,
    context: Context
): ColorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    isDark -> DarkColorScheme
    else -> LightColorScheme
}

@Composable
private fun Transition<Boolean>.animateColorProp(
    label: String,
    dynamicColor: Boolean,
    context: Context,
    prop: (ColorScheme) -> Color
): Color {
    val duration = DiceConstants.THEME_TRANSITION_DURATION_MILLIS
    return animateColor(
        transitionSpec = { tween(duration) },
        label = label
    ) { isDark ->
        prop(getTargetColorScheme(isDark, dynamicColor, context))
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
    val context = LocalContext.current
    val target = getTargetColorScheme(targetState, dynamicColor, context)

    return target.copy(
        primary = animateColorProp("primary", dynamicColor, context) { it.primary },
        onPrimary = animateColorProp("onPrimary", dynamicColor, context) { it.onPrimary },
        primaryContainer = animateColorProp("primaryContainer", dynamicColor, context) { it.primaryContainer },
        onPrimaryContainer = animateColorProp("onPrimaryContainer", dynamicColor, context) { it.onPrimaryContainer },
        inversePrimary = animateColorProp("inversePrimary", dynamicColor, context) { it.inversePrimary },
        secondary = animateColorProp("secondary", dynamicColor, context) { it.secondary },
        onSecondary = animateColorProp("onSecondary", dynamicColor, context) { it.onSecondary },
        secondaryContainer = animateColorProp("secondaryContainer", dynamicColor, context) { it.secondaryContainer },
        onSecondaryContainer = animateColorProp(
            "onSecondaryContainer",
            dynamicColor,
            context
        ) { it.onSecondaryContainer },
        tertiary = animateColorProp("tertiary", dynamicColor, context) { it.tertiary },
        onTertiary = animateColorProp("onTertiary", dynamicColor, context) { it.onTertiary },
        tertiaryContainer = animateColorProp("tertiaryContainer", dynamicColor, context) { it.tertiaryContainer },
        onTertiaryContainer = animateColorProp("onTertiaryContainer", dynamicColor, context) { it.onTertiaryContainer },
        background = animateColorProp("background", dynamicColor, context) { it.background },
        onBackground = animateColorProp("onBackground", dynamicColor, context) { it.onBackground },
        surface = animateColorProp("surface", dynamicColor, context) { it.surface },
        onSurface = animateColorProp("onSurface", dynamicColor, context) { it.onSurface },
        surfaceVariant = animateColorProp("surfaceVariant", dynamicColor, context) { it.surfaceVariant },
        onSurfaceVariant = animateColorProp("onSurfaceVariant", dynamicColor, context) { it.onSurfaceVariant },
        surfaceTint = animateColorProp("surfaceTint", dynamicColor, context) { it.surfaceTint },
        inverseSurface = animateColorProp("inverseSurface", dynamicColor, context) { it.inverseSurface },
        inverseOnSurface = animateColorProp("inverseOnSurface", dynamicColor, context) { it.inverseOnSurface },
        error = animateColorProp("error", dynamicColor, context) { it.error },
        onError = animateColorProp("onError", dynamicColor, context) { it.onError },
        errorContainer = animateColorProp("errorContainer", dynamicColor, context) { it.errorContainer },
        onErrorContainer = animateColorProp("onErrorContainer", dynamicColor, context) { it.onErrorContainer },
        outline = animateColorProp("outline", dynamicColor, context) { it.outline },
        outlineVariant = animateColorProp("outlineVariant", dynamicColor, context) { it.outlineVariant },
        scrim = animateColorProp("scrim", dynamicColor, context) { it.scrim }
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
        val view = LocalView.current
        if (view.isInEditMode.not()) {
            SideEffect {
                val window = (view.context as Activity).window
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = darkTheme.not()
                insetsController.isAppearanceLightNavigationBars = darkTheme.not()
            }
        }
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
