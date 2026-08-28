package com.greenfodor.diceroller.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DieColorTargetTest {
    @Test
    fun `fromName maps each enum name back to its value`() {
        DieColorTarget.entries.forEach { target ->
            assertEquals(target, DieColorTarget.fromName(target.name))
        }
    }

    @Test
    fun `fromName returns null for null, blank, unknown, or wrong-case input`() {
        assertNull(DieColorTarget.fromName(null))
        assertNull(DieColorTarget.fromName(""))
        assertNull(DieColorTarget.fromName("nonsense"))
        assertNull(DieColorTarget.fromName("d6"))
    }
}
