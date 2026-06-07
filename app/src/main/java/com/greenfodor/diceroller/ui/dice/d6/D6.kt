package com.greenfodor.diceroller.ui.dice.d6

import com.greenfodor.diceroller.geometry.HexahedronGeometry
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

/**
 * Definition of a D6 die using [DieDefinition].
 *
 * It automatically maps each of the 6 outcomes to a hexahedron face and
 * pre-calculates the 3-axis rotation required to bring that face front-facing
 * and upright. Deriving the rotation from the same geometry the renderer uses
 * for pips keeps the rolled value and the displayed face in lockstep.
 */
object D6 : DieDefinition {
    override val faces = HexahedronGeometry.faces.map { face ->
        val (rx, ry, rz) = HexahedronGeometry.getFaceRotation(face)
        DieFace(value = face.value, rotationX = rx, rotationY = ry, rotationZ = rz)
    }
}
