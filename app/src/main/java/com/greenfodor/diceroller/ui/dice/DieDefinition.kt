package com.greenfodor.diceroller.ui.dice

interface DieDefinition {
    /** Human-readable name, e.g. "d6", "d4" */
    val name: String
    /** All possible outcomes, in any order */
    val faces: List<DieFace>
    /** Pick a random face to land on */
    fun roll(): DieFace = faces.random()
}

data class DieFace(
    val value: Int,
    val rotationX: Float,
    val rotationY: Float,
    val rotationZ: Float = 0f
)