package com.greenfodor.diceroller.sensors

import android.hardware.SensorManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShakeDetectorTest {
    @Test
    fun `static gravity results in zero acceleration`() {
        // Device sitting flat on table
        val acc = ShakeDetector.calculateAcceleration(0f, 0f, SensorManager.GRAVITY_EARTH)
        assertEquals(0f, acc, 0.01f)
    }

    @Test
    fun `shake on X axis results in positive acceleration`() {
        // Device moved quickly sideways
        val acc = ShakeDetector.calculateAcceleration(15f, 0f, SensorManager.GRAVITY_EARTH)

        // sqrt(15^2 + 9.8^2) - 9.8 = sqrt(225 + 96.04) - 9.8 = 17.91 - 9.8 = 8.11
        assertEquals(8.11f, acc, 0.01f)
    }

    @Test
    fun `large shake exceeds threshold`() {
        val acc = ShakeDetector.calculateAcceleration(25f, 25f, 25f)
        // threshold is 12f
        assertTrue("Large shake should be significant", acc > 12f)
    }
}
