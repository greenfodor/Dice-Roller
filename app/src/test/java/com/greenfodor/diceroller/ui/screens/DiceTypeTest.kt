package com.greenfodor.diceroller.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class DiceTypeTest {
    @Test
    fun `fromName maps each enum name back to its value`() {
        DiceType.entries.forEach { diceType ->
            assertEquals(diceType, DiceType.fromName(diceType.name))
        }
    }

    @Test
    fun `fromName falls back to SINGLE_D6 for null, blank, unknown, or wrong-case input`() {
        assertEquals(DiceType.SINGLE_D6, DiceType.fromName(null))
        assertEquals(DiceType.SINGLE_D6, DiceType.fromName(""))
        assertEquals(DiceType.SINGLE_D6, DiceType.fromName("nonsense"))
        assertEquals(DiceType.SINGLE_D6, DiceType.fromName("single_d20"))
    }
}
