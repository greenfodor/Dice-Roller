package com.greenfodor.diceroller.ui.dice.d4

import com.greenfodor.diceroller.geometry.TetrahedronGeometry
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

object D4 : DieDefinition {

    override val faces = TetrahedronGeometry.faces.map { face ->
        val (rx, ry, rz) = TetrahedronGeometry.getFaceRotation(face)
        DieFace(value = face.value, rotationX = rx, rotationY = ry, rotationZ = rz)
    }
}
