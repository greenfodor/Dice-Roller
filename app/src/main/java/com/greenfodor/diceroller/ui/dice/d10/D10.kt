package com.greenfodor.diceroller.ui.dice.d10

import com.greenfodor.diceroller.geometry.PentagonalTrapezohedronGeometry
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

object D10 : DieDefinition {
    override val faces = PentagonalTrapezohedronGeometry.faces.map { face ->
        val (rx, ry, rz) = PentagonalTrapezohedronGeometry.getFaceRotation(face)
        DieFace(value = face.value, rotationX = rx, rotationY = ry, rotationZ = rz)
    }
}
