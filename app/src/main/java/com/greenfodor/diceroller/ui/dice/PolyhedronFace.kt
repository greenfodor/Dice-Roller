package com.greenfodor.diceroller.ui.dice

import androidx.compose.ui.graphics.Color

/**
 * Describes a single face of a polyhedron for rendering.
 */
data class PolyhedronFace(
    val vertexIndices: List<Int>,
    val baseColor: Color,
    val label: String
)
