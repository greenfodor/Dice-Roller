package com.greenfodor.diceroller.ui.utils

import android.content.Context
import com.greenfodor.diceroller.sensors.performRollHaptics
import com.greenfodor.diceroller.ui.dice.d6.CubeState

fun Context.rollDice(vararg cubeStates: CubeState) {
    if (cubeStates.any { it.isRolling }) return

    cubeStates.forEach { it.roll() }
    performRollHaptics()
}