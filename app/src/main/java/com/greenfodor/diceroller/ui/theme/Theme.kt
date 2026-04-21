package com.greenfodor.diceroller.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun DiceRollerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val targetColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Animate colors for a smooth transition
    val colorScheme = targetColorScheme.copy(
        primary = animateColorAsState(targetColorScheme.primary, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "primary").value,
        background = animateColorAsState(targetColorScheme.background, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "background").value,
        surface = animateColorAsState(targetColorScheme.surface, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "surface").value,
        onPrimary = animateColorAsState(targetColorScheme.onPrimary, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "onPrimary").value,
        onBackground = animateColorAsState(targetColorScheme.onBackground, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "onBackground").value,
        onSurface = animateColorAsState(targetColorScheme.onSurface, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "onSurface").value
    )

    // Invert contrast: Use the vibrant colors in Dark Mode and deeper colors in Light Mode
    val targetDiceColors = if (darkTheme) LightDiceColors else DarkDiceColors
    
    val diceColors = DiceColors(
        face1 = animateColorAsState(targetDiceColors.face1, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "face1").value,
        face2 = animateColorAsState(targetDiceColors.face2, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "face2").value,
        face3 = animateColorAsState(targetDiceColors.face3, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "face3").value,
        face4 = animateColorAsState(targetDiceColors.face4, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "face4").value,
        face5 = animateColorAsState(targetDiceColors.face5, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "face5").value,
        face6 = animateColorAsState(targetDiceColors.face6, tween(DiceConstants.THEME_TRANSITION_DURATION_MILLIS), label = "face6").value
    )

    CompositionLocalProvider(LocalDiceColors provides diceColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
