package com.greenfodor.diceroller.ui.dice

import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.dice.d4.D4
import com.greenfodor.diceroller.ui.dice.d6.D6
import kotlin.math.abs
import org.junit.Assert
import org.junit.Test

class DieStateTest {
    @Test
    fun `initial state has correct default values`() {
        val state = DieState(die = D6)
        Assert.assertEquals(D6.faces.first(), state.currentFace)
        Assert.assertEquals(0f, state.targetRotationX, 0.001f)
        Assert.assertEquals(0f, state.targetRotationY, 0.001f)
        Assert.assertEquals(false, state.isRolling)
    }

    @Test
    fun `rolling updates current face and rotations`() {
        val state = DieState(die = D6)

        state.roll()

        // Rotations should update
        Assert.assertNotEquals(0f, state.targetRotationX)
        Assert.assertNotEquals(0f, state.targetRotationY)

        // Absolute rotations should be at least (spin count - 1) * 360
        val minRotation = DiceConstants.FULL_ROTATION * (DiceConstants.ROTATION_SPIN_COUNT - 1)
        Assert.assertTrue("X rotation should be significant", abs(state.targetRotationX) >= minRotation)
        Assert.assertTrue("Y rotation should be significant", abs(state.targetRotationY) >= minRotation)
    }

    @Test
    fun `initial state with D4 reflects first face rotation`() {
        val state = DieState(die = D4)
        val expectedFace = D4.faces.first()

        Assert.assertEquals(expectedFace, state.currentFace)
        Assert.assertEquals(expectedFace.rotationX, state.targetRotationX, 0.001f)
        Assert.assertEquals(expectedFace.rotationY, state.targetRotationY, 0.001f)
        Assert.assertEquals(expectedFace.rotationZ, state.targetRotationZ, 0.001f)

        // Guard: D4's first face must have a non-zero rotation for this test to be meaningful
        val hasNonZeroRotation = expectedFace.rotationX != 0f ||
            expectedFace.rotationY != 0f ||
            expectedFace.rotationZ != 0f
        Assert.assertTrue("D4 first face should have non-zero rotation", hasNonZeroRotation)
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

        Assert.assertEquals(
            "Base X should be a whole multiple of 360",
            0f,
            abs(baseX) % DiceConstants.FULL_ROTATION,
            0.001f
        )
        Assert.assertEquals(
            "Base Y should be a whole multiple of 360",
            0f,
            abs(baseY) % DiceConstants.FULL_ROTATION,
            0.001f
        )
        Assert.assertTrue("Base X magnitude should be in [3,5] * 360", abs(baseX) in minMagnitude..maxMagnitude)
        Assert.assertTrue("Base Y magnitude should be in [3,5] * 360", abs(baseY) in minMagnitude..maxMagnitude)
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
        Assert.assertNotEquals("Second roll should change rotation", firstX, secondX)
    }
}
