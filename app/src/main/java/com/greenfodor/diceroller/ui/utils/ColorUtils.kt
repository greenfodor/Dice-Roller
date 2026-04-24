package com.greenfodor.diceroller.ui.utils

import androidx.compose.ui.graphics.Color

/**
 * Multiplies the RGB channels of this color by [intensity] while preserving the alpha channel.
 * Used for dynamic diffuse shading on 3D surfaces.
 *
 * @param intensity Shading factor (usually between 0.0 and 1.0).
 * @return A new [Color] with adjusted brightness.
 */
fun Color.shade(intensity: Float) =
    Color(
        red = red * intensity,
        green = green * intensity,
        blue = blue * intensity,
        alpha = alpha
    )
