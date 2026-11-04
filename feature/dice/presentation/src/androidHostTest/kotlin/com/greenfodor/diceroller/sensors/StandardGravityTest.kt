package com.greenfodor.diceroller.sensors

import android.hardware.SensorManager
import kotlin.test.Test
import kotlin.test.assertEquals

class StandardGravityTest {
    @Test
    fun standardGravityMatchesTheAndroidSensorManagerConstant() {
        assertEquals(SensorManager.GRAVITY_EARTH, ShakeDetector.STANDARD_GRAVITY)
    }
}
