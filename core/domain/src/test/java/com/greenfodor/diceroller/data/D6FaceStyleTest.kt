package com.greenfodor.diceroller.data

import org.junit.Assert.assertEquals
import org.junit.Test

class D6FaceStyleTest {
    @Test
    fun `fromName maps each enum name back to its value`() {
        D6FaceStyle.entries.forEach { style ->
            assertEquals(style, D6FaceStyle.fromName(style.name))
        }
    }

    @Test
    fun `fromName falls back to PIPS for null, blank, unknown, or wrong-case input`() {
        assertEquals(D6FaceStyle.PIPS, D6FaceStyle.fromName(null))
        assertEquals(D6FaceStyle.PIPS, D6FaceStyle.fromName(""))
        assertEquals(D6FaceStyle.PIPS, D6FaceStyle.fromName("nonsense"))
        assertEquals(D6FaceStyle.PIPS, D6FaceStyle.fromName("numbers"))
    }
}
