package com.greenfodor.diceroller.ui.dice.d4

import com.greenfodor.diceroller.geometry.TetrahedronGeometry
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

/**
 * Definition of a D4 die using [DieDefinition].
 *
 * It automatically maps each of the 4 outcomes to a tetrahedron face and
 * pre-calculates the 3-axis rotation required to bring that face to the center
 * in an upright position.
 */
object D4 : DieDefinition {
    override val faces = TetrahedronGeometry.faces.map { face ->
        val (rx, ry, rz) = TetrahedronGeometry.getFaceRotation(face)
        DieFace(value = face.value, rotationX = rx, rotationY = ry, rotationZ = rz)
    }
}
