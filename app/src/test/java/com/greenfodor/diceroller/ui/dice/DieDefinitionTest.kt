package com.greenfodor.diceroller.ui.dice

import com.greenfodor.diceroller.ui.dice.d20.D20
import com.greenfodor.diceroller.ui.dice.d6.D6
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DieDefinitionTest {
    @Test
    fun `D6 definition is valid`() {
        validateDie(D6, expectedFaceCount = 6)
    }

    @Test
    fun `D20 definition is valid`() {
        validateDie(D20, expectedFaceCount = 20)
    }

    private fun validateDie(
        die: DieDefinition,
        expectedFaceCount: Int
    ) {
        assertEquals("Die ${die.name} should have $expectedFaceCount faces", expectedFaceCount, die.faces.size)

        val values = die.faces.map { it.value }

        // Check for correct range
        assertTrue(
            "Die ${die.name} values should be in range 1..$expectedFaceCount",
            values.all { it in 1..expectedFaceCount }
        )

        // Check for duplicates
        assertEquals(
            "Die ${die.name} should not have duplicate face values",
            expectedFaceCount,
            values.distinct().size
        )
    }
}
