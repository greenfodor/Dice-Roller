package com.greenfodor.diceroller.ui.die.d6

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.die.DieDefinition
import com.greenfodor.diceroller.utils.logD

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
class CubeState(private val die: DieDefinition = D6) {
    var currentFace by mutableStateOf(die.faces.first())
        private set

    var targetRotationX by mutableFloatStateOf(0f)
        private set

    var targetRotationY by mutableFloatStateOf(0f)
        private set

    // Written by RollingCubeAnimation, read by the screen
    var isRolling by mutableStateOf(false)
        internal set

    fun roll() {
        currentFace = die.roll()
        targetRotationX += currentFace.rotationX + DiceConstants.ROTATION_X_OFFSET
        targetRotationY += currentFace.rotationY + DiceConstants.ROTATION_Y_OFFSET

        logD { "rolled ${currentFace.value}" }
    }
}

@Composable
fun rememberCubeState(): CubeState = remember { CubeState() }
