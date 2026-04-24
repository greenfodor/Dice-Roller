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
import com.greenfodor.diceroller.ui.DiceConstants

@Immutable
data class DiceColors(
    val face1: Color = Color.Unspecified,
    val face2: Color = Color.Unspecified,
    val face3: Color = Color.Unspecified,
    val face4: Color = Color.Unspecified,
    val face5: Color = Color.Unspecified,
    val face6: Color = Color.Unspecified
)

val LocalDiceColors = staticCompositionLocalOf { DiceColors() }

private val DarkDiceColors =
    DiceColors(
        face1 = DiceRedDark,
        face2 = DiceTealDark,
        face3 = DiceYellowDark,
        face4 = DiceGreenDark,
        face5 = DiceMintDark,
        face6 = DiceLavenderDark
    )

private val LightDiceColors =
    DiceColors(
        face1 = DiceRed,
        face2 = DiceTeal,
        face3 = DiceYellow,
        face4 = DiceGreen,
        face5 = DiceMint,
        face6 = DiceLavender
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40
    )

private fun getTargetColorScheme(
    isDark: Boolean,
    dynamicColor: Boolean,
    context: Context
): ColorScheme =
    when {
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
        onSecondaryContainer =
            animateColorProp(
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
 * @param content The composable content to be themed.
 */
@Composable
fun DiceRollerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = darkTheme, label = "ThemeTransition")
    val duration = DiceConstants.THEME_TRANSITION_DURATION_MILLIS

    val colorScheme = transition.animateColorScheme(dynamicColor)

    val diceColors =
        DiceColors(
            face1 =
                transition
                    .animateColor(label = "face1", transitionSpec = { tween(duration) }) { isDark ->
                        if (isDark) LightDiceColors.face1 else DarkDiceColors.face1
                    }.value,
            face2 =
                transition
                    .animateColor(label = "face2", transitionSpec = { tween(duration) }) { isDark ->
                        if (isDark) LightDiceColors.face2 else DarkDiceColors.face2
                    }.value,
            face3 =
                transition
                    .animateColor(label = "face3", transitionSpec = { tween(duration) }) { isDark ->
                        if (isDark) LightDiceColors.face3 else DarkDiceColors.face3
                    }.value,
            face4 =
                transition
                    .animateColor(label = "face4", transitionSpec = { tween(duration) }) { isDark ->
                        if (isDark) LightDiceColors.face4 else DarkDiceColors.face4
                    }.value,
            face5 =
                transition
                    .animateColor(label = "face5", transitionSpec = { tween(duration) }) { isDark ->
                        if (isDark) LightDiceColors.face5 else DarkDiceColors.face5
                    }.value,
            face6 =
                transition
                    .animateColor(label = "face6", transitionSpec = { tween(duration) }) { isDark ->
                        if (isDark) LightDiceColors.face6 else DarkDiceColors.face6
                    }.value
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
