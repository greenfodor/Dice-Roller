package com.greenfodor.diceroller.ui.dice

import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.dice.d4.D4
import com.greenfodor.diceroller.ui.dice.d6.D6
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DieStateTest {
    @Test
    fun `initial state has correct default values`() {
        val state = DieState(die = D6)
        assertEquals(D6.faces.first(), state.currentFace)
        assertEquals(0f, state.targetRotationX, 0.001f)
        assertEquals(0f, state.targetRotationY, 0.001f)
        assertEquals(false, state.isRolling)
    }

    @Test
    fun `rolling updates current face and rotations`() {
        val state = DieState(die = D6)

        state.roll()

        // Rotations should update
        assertNotEquals(0f, state.targetRotationX)
        assertNotEquals(0f, state.targetRotationY)

        // Absolute rotations should be at least (spin count - 1) * 360
        val minRotation = DiceConstants.FULL_ROTATION * (DiceConstants.ROTATION_SPIN_COUNT - 1)
        assertTrue(abs(state.targetRotationX) >= minRotation, "X rotation should be significant")
        assertTrue(abs(state.targetRotationY) >= minRotation, "Y rotation should be significant")
    }

    @Test
    fun `initial state with D4 reflects first face rotation`() {
        val state = DieState(die = D4)
        val expectedFace = D4.faces.first()

        assertEquals(expectedFace, state.currentFace)
        assertEquals(expectedFace.rotationX, state.targetRotationX, 0.001f)
        assertEquals(expectedFace.rotationY, state.targetRotationY, 0.001f)
        assertEquals(expectedFace.rotationZ, state.targetRotationZ, 0.001f)

        // Guard: D4's first face must have a non-zero rotation for this test to be meaningful
        val hasNonZeroRotation = expectedFace.rotationX != 0f ||
            expectedFace.rotationY != 0f ||
            expectedFace.rotationZ != 0f
        assertTrue(hasNonZeroRotation, "D4 first face should have non-zero rotation")
    }

    @Test
    fun `rolling D4 accumulates from zero base not initial face rotation`() {
        val state = DieState(die = D4)

        state.roll()

        // targetRotation = base + currentFace.rotation, where base = ±{3,4,5} * 360
        // If the initial face rotation leaked into the base, the residual would not be a
        // whole multiple of 360 (D4 face rotations have fractional degree values).
        val baseX = state.targetRotationX - state.currentFace.rotationX
        val baseY = state.targetRotationY - state.currentFace.rotationY

        val minMagnitude = DiceConstants.FULL_ROTATION * DiceConstants.ROTATION_SPIN_COUNT
        val maxMagnitude = DiceConstants.FULL_ROTATION * (DiceConstants.ROTATION_SPIN_COUNT + 2)

        assertEquals(
            0f,
            abs(baseX) % DiceConstants.FULL_ROTATION,
            0.001f,
            "Base X should be a whole multiple of 360"
        )
        assertEquals(
            0f,
            abs(baseY) % DiceConstants.FULL_ROTATION,
            0.001f,
            "Base Y should be a whole multiple of 360"
        )
        assertTrue(abs(baseX) in minMagnitude..maxMagnitude, "Base X magnitude should be in [3,5] * 360")
        assertTrue(abs(baseY) in minMagnitude..maxMagnitude, "Base Y magnitude should be in [3,5] * 360")
    }

    @Test
    fun `rolling marks the die as rolling`() {
        val state = DieState(die = D6)

        state.roll()

        assertTrue(state.isRolling, "Die should be rolling immediately after roll()")
    }

    @Test
    fun `onSettled clears the rolling flag`() {
        val state = DieState(die = D6)
        state.roll()

        state.onSettled()

        assertFalse(state.isRolling, "Die should not be rolling once settled")
    }

    @Test
    fun `multiple rolls change target rotation`() {
        val state = DieState(die = D6)

        state.roll() // First roll
        val firstX = state.targetRotationX

        state.roll() // Second roll
        val secondX = state.targetRotationX

        // It's extremely unlikely (but mathematically possible if direction flips
        // and spins change) that they are exactly equal, but for a unit test
        // this is generally reliable given the large rotation increments.
        assertNotEquals(firstX, secondX, "Second roll should change rotation")
    }
}
