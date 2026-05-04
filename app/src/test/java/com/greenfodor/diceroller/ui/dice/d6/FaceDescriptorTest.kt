package com.greenfodor.diceroller.ui.dice.d6

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
    fun `each face value N has exactly N pip positions`() {
        for (value in 1..6) {
            val positions = DotLayouts.positions[value]
            assertNotNull("Face $value should have a pip layout", positions)
            assertEquals("Face $value should have exactly $value pips", value, positions!!.size)
        }
    }
}
