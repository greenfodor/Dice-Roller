package com.greenfodor.diceroller.ui.dice.d6

import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

/**
 * Definition of a D6 die using [DieDefinition].
 *
 * Uses explicit axis-aligned rotations to ensure the cube stays "flat"
 * (faces parallel to screen axes) when it lands. Each face is defined
 * by exactly 4 values: value, rotationX, rotationY, and rotationZ.
 */
object D6 : DieDefinition {
    override val faces = listOf(
        DieFace(value = 1, rotationX = 0f, rotationY = 0f, rotationZ = 0f), // Front
        DieFace(value = 2, rotationX = 270f, rotationY = 0f, rotationZ = 0f), // Top
        DieFace(value = 3, rotationX = 0f, rotationY = 90f, rotationZ = 0f), // Right
        DieFace(value = 4, rotationX = 0f, rotationY = 270f, rotationZ = 0f), // Left
        DieFace(value = 5, rotationX = 90f, rotationY = 0f, rotationZ = 0f), // Bottom
        DieFace(value = 6, rotationX = 0f, rotationY = 180f, rotationZ = 0f) // Back
    )
}
