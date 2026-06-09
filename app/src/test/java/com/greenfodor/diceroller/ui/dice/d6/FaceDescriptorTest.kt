package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.ui.graphics.Color
import com.greenfodor.diceroller.ui.theme.DiceColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FaceDescriptorTest {
    @Test
    fun `DotLayouts covers all six face values`() {
        assertEquals(6, DotLayouts.positions.size)
        assertTrue(DotLayouts.positions.keys.containsAll((1..6).toSet()))
    }

    @Test
    fun `every face uses the red face1 color regardless of value`() {
        // Distinct per-face colors so a regression to colorForValue would be caught.
        val colors = DiceColors(
            face1 = Color.Red,
            face2 = Color.Green,
            face3 = Color.Blue,
            face4 = Color.Yellow,
            face5 = Color.Cyan,
            face6 = Color.Magenta
        )

        val descriptors = createDiceFaceDescriptors(colors)

        assertEquals(6, descriptors.size)
        assertTrue(
            "All D6 faces should share the red face1 color",
            descriptors.all { it.baseColor == colors.face1 }
        )
    }

    @Test
    fun `descriptors preserve each face value as its dot count`() {
        val descriptors = createDiceFaceDescriptors(DiceColors())

        assertEquals((1..6).toSet(), descriptors.map { it.dotCount }.toSet())
    }

    @Test
    fun `each face value N has exactly N pip positions`() {
        for (value in 1..6) {
            val positions = DotLayouts.positions[value]
            assertNotNull("Face $value should have a pip layout", positions)
            assertEquals("Face $value should have exactly $value pips", value, positions!!.size)
        }
    }
}
