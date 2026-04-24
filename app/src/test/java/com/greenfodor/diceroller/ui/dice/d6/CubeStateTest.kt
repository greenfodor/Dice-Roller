package com.greenfodor.diceroller.ui.dice.d6

import com.greenfodor.diceroller.ui.DiceConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

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
        
        // Rotations should be at least the spin count * 360
        val minRotation = DiceConstants.FULL_ROTATION * DiceConstants.ROTATION_SPIN_COUNT
        assertEquals(true, state.targetRotationX >= minRotation)
        assertEquals(true, state.targetRotationY >= minRotation)
    }

    @Test
    fun `multiple rolls increment base rotation correctly`() {
        val state = CubeState()
        
        state.roll() // First roll
        val firstX = state.targetRotationX
        
        state.roll() // Second roll
        val secondX = state.targetRotationX
        
        val rotationIncrement = DiceConstants.FULL_ROTATION * DiceConstants.ROTATION_SPIN_COUNT
        
        // The second target should be roughly first + increment
        // (allowing for difference in face rotation)
        assertTrue("Second roll should have higher rotation", secondX > firstX)
        assertTrue("Increment should be at least spin count", secondX - firstX >= rotationIncrement - 360f)
    }
    
    private fun assertTrue(message: String, condition: Boolean) {
        org.junit.Assert.assertTrue(message, condition)
    }
}
