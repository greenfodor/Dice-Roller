package com.greenfodor.diceroller.ui.dice.d6

import com.greenfodor.diceroller.geometry.CubeFace
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

object D6 : DieDefinition {
    override val faces = listOf(
        DieFace(value = 1, rotationX = CubeFace.FRONT.rotationX, rotationY = CubeFace.FRONT.rotationY),
        DieFace(value = 2, rotationX = CubeFace.TOP.rotationX, rotationY = CubeFace.TOP.rotationY),
        DieFace(value = 3, rotationX = CubeFace.RIGHT.rotationX, rotationY = CubeFace.RIGHT.rotationY),
        DieFace(value = 4, rotationX = CubeFace.LEFT.rotationX, rotationY = CubeFace.LEFT.rotationY),
        DieFace(value = 5, rotationX = CubeFace.BOTTOM.rotationX, rotationY = CubeFace.BOTTOM.rotationY),
        DieFace(value = 6, rotationX = CubeFace.BACK.rotationX, rotationY = CubeFace.BACK.rotationY)
    )
}
