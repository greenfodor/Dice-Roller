package com.greenfodor.diceroller.ui.dice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.utils.logD
import kotlin.random.Random

/**
 * Holds and mutates the rotation state for the rolling die animation.
 *
 * Extracting this logic out of the composable means the roll math is
 * independently readable and testable.
 *
 * [isRolling] is owned here: it flips to `true` the moment a roll starts and
 * back to `false` when the animation settles (see [onSettled]). Nothing writes
 * it from inside composition, which keeps the rolling status a single source of
 * truth and avoids backwards writes during the composition phase.
 *
 * Usage:
 * ```kotlin
 * val dieState = rememberDieState(die = D6)
 * // ...
 * Button(onClick = { dieState.roll() })
 * ```
 */
class DieState(
    private val die: DieDefinition
) {
    private val initialFace = die.faces.first()

    var currentFace by mutableStateOf(initialFace)
        private set

    var targetRotationX by mutableFloatStateOf(initialFace.rotationX)
        private set

    var targetRotationY by mutableFloatStateOf(initialFace.rotationY)
        private set

    var targetRotationZ by mutableFloatStateOf(initialFace.rotationZ)
        private set

    var isRolling by mutableStateOf(false)
        private set

    private var baseRotationX = 0f
    private var baseRotationY = 0f
    private var baseRotationZ = 0f

    /**
     * Triggers a new roll.
     *
     * It selects a new random face from the die and calculates the new target
     * rotations. The rotations include multiple full spins in random directions
     * to create a dynamic rolling effect.
     */
    fun roll() {
        currentFace = die.roll()
        isRolling = true

        // Randomize number of full spins and direction (+ or -) for each axis
        val spinsX = (DiceConstants.ROTATION_SPIN_COUNT..DiceConstants.ROTATION_SPIN_COUNT + 2).random()
        val spinsY = (DiceConstants.ROTATION_SPIN_COUNT..DiceConstants.ROTATION_SPIN_COUNT + 2).random()
        val spinsZ = (DiceConstants.ROTATION_SPIN_COUNT..DiceConstants.ROTATION_SPIN_COUNT + 2).random()

        val directionX = if (Random.nextBoolean()) 1 else -1
        val directionY = if (Random.nextBoolean()) 1 else -1
        val directionZ = if (Random.nextBoolean()) 1 else -1

        baseRotationX += directionX * DiceConstants.FULL_ROTATION * spinsX
        baseRotationY += directionY * DiceConstants.FULL_ROTATION * spinsY
        baseRotationZ += directionZ * DiceConstants.FULL_ROTATION * spinsZ

        targetRotationX = baseRotationX + currentFace.rotationX
        targetRotationY = baseRotationY + currentFace.rotationY
        targetRotationZ = baseRotationZ + currentFace.rotationZ

        logD { "rolled ${currentFace.value}" }
    }

    /** Called by the animation once the roll has finished animating to its target. */
    fun onSettled() {
        isRolling = false
    }

    /**
     * Restores the die to a resting position on [face] with no accumulated spin.
     * Used when rebuilding state after a configuration change or process death.
     */
    private fun restoreToFace(face: DieFace) {
        currentFace = face
        targetRotationX = face.rotationX
        targetRotationY = face.rotationY
        targetRotationZ = face.rotationZ
    }

    companion object {
        /**
         * Persists only the resting face (by index) across configuration changes
         * and process death. The in-flight animation is intentionally not saved —
         * the die is restored at rest on its last result.
         */
        fun saver(die: DieDefinition): Saver<DieState, Int> = Saver(
            save = { die.faces.indexOf(it.currentFace) },
            restore = { index ->
                DieState(die).apply {
                    die.faces.getOrNull(index)?.let(::restoreToFace)
                }
            }
        )
    }
}

@Composable
fun rememberDieState(die: DieDefinition): DieState =
    rememberSaveable(die, saver = DieState.saver(die)) { DieState(die) }
