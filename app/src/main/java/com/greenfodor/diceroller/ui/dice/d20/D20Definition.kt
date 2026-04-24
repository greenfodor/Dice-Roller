package com.greenfodor.diceroller.ui.dice.d20

import com.greenfodor.diceroller.geometry.IcosahedronGeometry
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace

/**
 * Definition of a D20 die.
 * 
 * Maps each value (1-20) to a face of the icosahedron and its required rotation.
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
