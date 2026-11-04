package com.greenfodor.diceroller.data

import kotlin.test.Test
import kotlin.test.assertEquals

class DiceColorOptionTest {
    @Test
    fun `fromName maps each enum name back to its value`() {
        DiceColorOption.entries.forEach { option ->
            assertEquals(option, DiceColorOption.fromName(option.name))
        }
    }

    @Test
    fun `fromName falls back to RED for null blank unknown or wrong-case input`() {
        assertEquals(DiceColorOption.RED, DiceColorOption.fromName(null))
        assertEquals(DiceColorOption.RED, DiceColorOption.fromName(""))
        assertEquals(DiceColorOption.RED, DiceColorOption.fromName("nonsense"))
        assertEquals(DiceColorOption.RED, DiceColorOption.fromName("teal"))
    }
}
