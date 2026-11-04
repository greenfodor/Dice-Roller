package com.greenfodor.diceroller.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DieColorTargetTest {
    @Test
    fun `fromName maps each enum name back to its value`() {
        DieColorTarget.entries.forEach { target ->
            assertEquals(target, DieColorTarget.fromName(target.name))
        }
    }

    @Test
    fun `fromName returns null for null blank unknown or wrong-case input`() {
        assertNull(DieColorTarget.fromName(null))
        assertNull(DieColorTarget.fromName(""))
        assertNull(DieColorTarget.fromName("nonsense"))
        assertNull(DieColorTarget.fromName("d6"))
    }
}
