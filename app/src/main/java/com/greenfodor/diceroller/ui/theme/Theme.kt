package com.greenfodor.diceroller.ui.theme

import android.app.Activity
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

private val DarkDiceColors = DiceColors(
    face1 = DiceRedDark,
    face2 = DiceTealDark,
    face3 = DiceYellowDark,
    face4 = DiceGreenDark,
    face5 = DiceMintDark,
    face6 = DiceLavenderDark
)

private val LightDiceColors = DiceColors(
    face1 = DiceRed,
    face2 = DiceTeal,
    face3 = DiceYellow,
    face4 = DiceGreen,
    face5 = DiceMint,
    face6 = DiceLavender
)

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

/**
 * Animates all color scheme properties using a single transition to ensure they are perfectly in sync.
 */
@Composable
private fun Transition<Boolean>.animateColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme {
    val context = LocalContext.current
    val duration = DiceConstants.THEME_TRANSITION_DURATION_MILLIS

    // Determine target based on state
    fun getColorScheme(isDark: Boolean): ColorScheme {
        return when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            isDark -> DarkColorScheme
            else -> LightColorScheme
        }
    }

    val target = getColorScheme(darkTheme)
    val initial = getColorScheme(!darkTheme)

    // Helper to animate a specific property
    @Composable
    fun animateColorProp(label: String, prop: (ColorScheme) -> Color): Color {
        return animateColor(
            transitionSpec = { tween(duration) },
            label = label
        ) { isDark -> prop(getColorScheme(isDark)) }.value
    }

    return target.copy(
        primary = animateColorProp("primary") { it.primary },
        onPrimary = animateColorProp("onPrimary") { it.onPrimary },
        primaryContainer = animateColorProp("primaryContainer") { it.primaryContainer },
        onPrimaryContainer = animateColorProp("onPrimaryContainer") { it.onPrimaryContainer },
        inversePrimary = animateColorProp("inversePrimary") { it.inversePrimary },
        secondary = animateColorProp("secondary") { it.secondary },
        onSecondary = animateColorProp("onSecondary") { it.onSecondary },
        secondaryContainer = animateColorProp("secondaryContainer") { it.secondaryContainer },
        onSecondaryContainer = animateColorProp("onSecondaryContainer") { it.onSecondaryContainer },
        tertiary = animateColorProp("tertiary") { it.tertiary },
        onTertiary = animateColorProp("onTertiary") { it.onTertiary },
        tertiaryContainer = animateColorProp("tertiaryContainer") { it.tertiaryContainer },
        onTertiaryContainer = animateColorProp("onTertiaryContainer") { it.onTertiaryContainer },
        background = animateColorProp("background") { it.background },
        onBackground = animateColorProp("onBackground") { it.onBackground },
        surface = animateColorProp("surface") { it.surface },
        onSurface = animateColorProp("onSurface") { it.onSurface },
        surfaceVariant = animateColorProp("surfaceVariant") { it.surfaceVariant },
        onSurfaceVariant = animateColorProp("onSurfaceVariant") { it.onSurfaceVariant },
        surfaceTint = animateColorProp("surfaceTint") { it.surfaceTint },
        inverseSurface = animateColorProp("inverseSurface") { it.inverseSurface },
        inverseOnSurface = animateColorProp("inverseOnSurface") { it.inverseOnSurface },
        error = animateColorProp("error") { it.error },
        onError = animateColorProp("onError") { it.onError },
        errorContainer = animateColorProp("errorContainer") { it.errorContainer },
        onErrorContainer = animateColorProp("onErrorContainer") { it.onErrorContainer },
        outline = animateColorProp("outline") { it.outline },
        outlineVariant = animateColorProp("outlineVariant") { it.outlineVariant },
        scrim = animateColorProp("scrim") { it.scrim }
    )
}

@Composable
fun DiceRollerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(targetState = darkTheme, label = "ThemeTransition")
    val duration = DiceConstants.THEME_TRANSITION_DURATION_MILLIS

    val colorScheme = transition.animateColorScheme(darkTheme, dynamicColor)

    // Invert contrast: Use vibrant colors in Dark Mode and deeper colors in Light Mode
    val diceColors = DiceColors(
        face1 = transition.animateColor(label = "face1", transitionSpec = { tween(duration) }) { isDark ->
            if (isDark) LightDiceColors.face1 else DarkDiceColors.face1
        }.value,
        face2 = transition.animateColor(label = "face2", transitionSpec = { tween(duration) }) { isDark ->
            if (isDark) LightDiceColors.face2 else DarkDiceColors.face2
        }.value,
        face3 = transition.animateColor(label = "face3", transitionSpec = { tween(duration) }) { isDark ->
            if (isDark) LightDiceColors.face3 else DarkDiceColors.face3
        }.value,
        face4 = transition.animateColor(label = "face4", transitionSpec = { tween(duration) }) { isDark ->
            if (isDark) LightDiceColors.face4 else DarkDiceColors.face4
        }.value,
        face5 = transition.animateColor(label = "face5", transitionSpec = { tween(duration) }) { isDark ->
            if (isDark) LightDiceColors.face5 else DarkDiceColors.face5
        }.value,
        face6 = transition.animateColor(label = "face6", transitionSpec = { tween(duration) }) { isDark ->
            if (isDark) LightDiceColors.face6 else DarkDiceColors.face6
        }.value
    )

    CompositionLocalProvider(LocalDiceColors provides diceColors) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
