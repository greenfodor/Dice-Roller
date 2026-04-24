package com.greenfodor.diceroller.ui.utils

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorShadeTest {
    @Test
    fun `shading with 1 returns original color`() {
        // Note: Due to isReturnDefaultValues = true, Color properties may return 0f.
        // We test the logic of the extension function assuming Color behaves.
        val color = Color(red = 0.5f, green = 0.5f, blue = 0.5f, alpha = 1.0f)
        val shaded = color.shade(1.0f)

        assertEquals(color.red, shaded.red, 0.01f)
        assertEquals(color.green, shaded.green, 0.01f)
        assertEquals(color.blue, shaded.blue, 0.01f)
        assertEquals(color.alpha, shaded.alpha, 0.01f)
    }

    @Test
    fun `shading with 0 results in black`() {
        val color = Color(red = 1.0f, green = 1.0f, blue = 1.0f, alpha = 0.8f)
        val shaded = color.shade(0.0f)

        assertEquals(0f, shaded.red, 0.01f)
        assertEquals(0f, shaded.green, 0.01f)
        assertEquals(0f, shaded.blue, 0.01f)
        assertEquals(0.8f, shaded.alpha, 0.01f)
    }
}
