package com.greenfodor.diceroller.ui.utils

import android.content.Context
import com.greenfodor.diceroller.sensors.performRollHaptics
import com.greenfodor.diceroller.ui.dice.d6.CubeState

/**
 * Orchestrates a die roll for one or more [CubeState] instances.
 *
 * It validates that no dice are currently rolling before triggering a new roll on each
 * provided state and performing haptic feedback.
 *
 * @param cubeStates The states of the dice to be rolled.
 */
fun Context.rollDice(vararg cubeStates: CubeState) {
    if (cubeStates.any { it.isRolling }) return

    cubeStates.forEach { it.roll() }
    performRollHaptics()
}
