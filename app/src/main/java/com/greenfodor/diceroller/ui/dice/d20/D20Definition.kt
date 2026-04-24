package com.greenfodor.diceroller.ui.dice.d20

import com.greenfodor.diceroller.geometry.IcosahedronGeometry
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

/**
 * Definition of a D20 die using [DieDefinition].
 * 
 * It automatically maps each of the 20 outcomes to an icosahedron face and
 * pre-calculates the 3-axis rotation required to bring that face to the center
 * in an upright position.
 */
object D20 : DieDefinition {
    override val name = "d20"
    
    override val faces = (0 until 20).map { index ->
        val rotation = IcosahedronGeometry.getFaceRotation(index)
        DieFace(
            value = index + 1,
            rotationX = rotation.first,
            rotationY = rotation.second,
            rotationZ = rotation.third
        )
    }
}
