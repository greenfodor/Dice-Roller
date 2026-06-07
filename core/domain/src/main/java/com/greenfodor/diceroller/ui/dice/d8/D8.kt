package com.greenfodor.diceroller.ui.dice.d8

import com.greenfodor.diceroller.geometry.OctahedronGeometry
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

/**
 * Definition of a D8 die using [DieDefinition].
 *
 * It automatically maps each of the 8 outcomes to an octahedron face and
 * pre-calculates the 3-axis rotation required to bring that face to the center
 * in an upright position.
 */
object D8 : DieDefinition {
    override val faces = OctahedronGeometry.faces.map { face ->
        val (rx, ry, rz) = OctahedronGeometry.getFaceRotation(face)
        DieFace(value = face.value, rotationX = rx, rotationY = ry, rotationZ = rz)
    }
}
