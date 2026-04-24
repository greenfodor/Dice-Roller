package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.utils.logD
import kotlin.random.Random

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

    var isRolling by mutableStateOf(false)
        internal set

    private var baseRotationX = 0f
    private var baseRotationY = 0f

    /**
     * Triggers a new roll.
     *
     * It selects a new random face from the die and calculates the new target
     * rotations. The rotations include multiple full spins in random directions
     * to create a dynamic rolling effect.
     */
    fun roll() {
        currentFace = die.roll()

        // Randomize number of full spins and direction (+ or -) for each axis
        val spinsX = (DiceConstants.ROTATION_SPIN_COUNT..DiceConstants.ROTATION_SPIN_COUNT + 2).random()
        val spinsY = (DiceConstants.ROTATION_SPIN_COUNT..DiceConstants.ROTATION_SPIN_COUNT + 2).random()

        val directionX = if (Random.nextBoolean()) 1 else -1
        val directionY = if (Random.nextBoolean()) 1 else -1

        baseRotationX += directionX * DiceConstants.FULL_ROTATION * spinsX
        baseRotationY += directionY * DiceConstants.FULL_ROTATION * spinsY

        targetRotationX = baseRotationX + currentFace.rotationX
        targetRotationY = baseRotationY + currentFace.rotationY

        logD { "rolled ${currentFace.value}" }
    }
}

@Composable
fun rememberCubeState(): CubeState = remember { CubeState() }
