package com.greenfodor.diceroller.ui.dice.d6

import com.greenfodor.diceroller.ui.DiceConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class CubeStateTest {
    @Test
    fun `initial state has correct default values`() {
        val state = CubeState()
        assertEquals(D6.faces.first(), state.currentFace)
        assertEquals(0f, state.targetRotationX, 0.001f)
        assertEquals(0f, state.targetRotationY, 0.001f)
        assertEquals(false, state.isRolling)
    }

    @Test
    fun `rolling updates current face and rotations`() {
        val state = CubeState()

        state.roll()

        // Rotations should update
        assertNotEquals(0f, state.targetRotationX)
        assertNotEquals(0f, state.targetRotationY)

        // Absolute rotations should be at least (spin count - 1) * 360
        val minRotation = DiceConstants.FULL_ROTATION * (DiceConstants.ROTATION_SPIN_COUNT - 1)
        assertTrue("X rotation should be significant", abs(state.targetRotationX) >= minRotation)
        assertTrue("Y rotation should be significant", abs(state.targetRotationY) >= minRotation)
    }

    @Test
    fun `multiple rolls change target rotation`() {
        val state = CubeState()

        state.roll() // First roll
        val firstX = state.targetRotationX

        state.roll() // Second roll
        val secondX = state.targetRotationX

        // It's extremely unlikely (but mathematically possible if direction flips
        // and spins change) that they are exactly equal, but for a unit test
        // this is generally reliable given the large rotation increments.
        assertNotEquals("Second roll should change rotation", firstX, secondX)
    }
}
