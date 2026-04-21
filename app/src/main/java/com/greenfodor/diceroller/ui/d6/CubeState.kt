package com.greenfodor.diceroller.ui.d6

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.greenfodor.diceroller.geometry.CubeFace
import com.greenfodor.diceroller.ui.DiceConstants

/**
 * Holds and mutates the rotation state for the rolling cube animation.
 *
 * Extracting this logic out of the composable means the roll math is
 * independently readable and testable.
 *
 * Usage:
 * ```kotlin
 * val cubeState = rememberCubeState()
 * // ...
 * Button(onClick = { cubeState.roll() })
 * ```
 */
class CubeState {
    var targetFace by mutableStateOf(CubeFace.FRONT)
        private set

    var targetRotationX by mutableFloatStateOf(0f)
        private set

    var targetRotationY by mutableFloatStateOf(0f)
        private set

    /**
     * Picks a random target face and accumulates the rotation needed to
     * show it, plus a full spin offset so the cube visibly "rolls".
     */
    fun roll() {
        targetFace = CubeFace.entries.random()
        targetRotationX += targetFace.rotationX + DiceConstants.ROTATION_X_OFFSET
        targetRotationY += targetFace.rotationY + DiceConstants.ROTATION_Y_OFFSET
    }
}

@Composable
fun rememberCubeState(): CubeState = remember { CubeState() }
