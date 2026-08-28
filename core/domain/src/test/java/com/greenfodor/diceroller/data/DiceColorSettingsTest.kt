package com.greenfodor.diceroller.data

import org.junit.Assert.assertEquals
import org.junit.Test

class DiceColorSettingsTest {
    @Test
    fun `default settings expose distinct per-die colors`() {
        val settings = DiceColorSettings()

        DieColorTarget.entries.forEach { target ->
            assertEquals(DiceColorSettings.DEFAULT_PER_DIE.getValue(target), settings.optionFor(target))
        }
    }

    @Test
    fun `optionFor returns the single color for every target when single color is enabled`() {
        val settings = DiceColorSettings(useSingleColor = true, singleColor = DiceColorOption.GREEN)

        DieColorTarget.entries.forEach { target ->
            assertEquals(DiceColorOption.GREEN, settings.optionFor(target))
        }
    }

    @Test
    fun `optionFor returns the per-die override when present`() {
        val settings = DiceColorSettings(perDie = mapOf(DieColorTarget.D20 to DiceColorOption.TEAL))

        assertEquals(DiceColorOption.TEAL, settings.optionFor(DieColorTarget.D20))
    }

    @Test
    fun `optionFor falls back to the default when a target is missing from perDie`() {
        val settings = DiceColorSettings(perDie = emptyMap())

        assertEquals(
            DiceColorSettings.DEFAULT_PER_DIE.getValue(DieColorTarget.D8),
            settings.optionFor(DieColorTarget.D8)
        )
    }
}
