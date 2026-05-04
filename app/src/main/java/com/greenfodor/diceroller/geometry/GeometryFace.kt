package com.greenfodor.diceroller.geometry

/**
 * Describes a single face of a polyhedron in pure geometry terms.
 *
 * @param value The die value shown on this face (e.g. 1–6 for a D6, 1–20 for a D20).
 * @param vertexIndices Indices into the geometry object's vertex list that form this face.
 */
data class GeometryFace(
    val value: Int,
    val vertexIndices: List<Int>
)
