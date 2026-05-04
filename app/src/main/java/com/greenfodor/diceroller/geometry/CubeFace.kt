package com.greenfodor.diceroller.geometry

enum class CubeFace(
    val rotationX: Float,
    val rotationY: Float
) {
    FRONT(0f, 0f),
    BACK(0f, 180f),
    TOP(270f, 0f),
    BOTTOM(90f, 0f),
    LEFT(0f, 270f),
    RIGHT(0f, 90f)
}
