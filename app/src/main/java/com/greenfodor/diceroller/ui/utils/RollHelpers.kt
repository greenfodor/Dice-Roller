package com.greenfodor.diceroller.ui.utils

import android.content.Context
import com.greenfodor.diceroller.sensors.performRollHaptics
import com.greenfodor.diceroller.ui.dice.DieState

/**
 * Orchestrates a die roll for one or more [DieState] instances.
 *
 * It validates that no dice are currently rolling before triggering a new roll on each
 * provided state and performing haptic feedback.
 *
 * @param dieStates The states of the dice to be rolled.
 */
fun Context.rollDice(vararg dieStates: DieState) {
    if (dieStates.any { it.isRolling }) return

    dieStates.forEach { it.roll() }
    performRollHaptics()
}
