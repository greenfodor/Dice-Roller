package com.greenfodor.diceroller.ui.dice.d100

import com.greenfodor.diceroller.geometry.PentagonalTrapezohedronGeometry
import com.greenfodor.diceroller.ui.dice.DieDefinition
import com.greenfodor.diceroller.ui.dice.DieFace
import com.greenfodor.diceroller.ui.dice.DieState

/**
 * Standard D&D percentile dice: a d100 is rolled with two physical d10s — a "tens"
 * die marked 00–90 and a "units" die marked 0–9 — whose results are summed, with
 * the double-zero (00 + 0) reading as 100.
 *
 * Both dice share [PentagonalTrapezohedronGeometry] (the d10 shape) and only differ
 * in how each geometry face value (1–10) is labeled and scored:
 *   - units die: `v % 10`            → 0–9
 *   - tens die:  `(v % 10) * 10`     → 00–90
 *
 * The renderer derives a face's label from its geometry value via [tensLabel] /
 * [unitsLabel], and each [DieFace.value] below carries the matching contribution,
 * so the up-facing label and the scored value always agree.
 */
private const val TENS_PER_FACE = 10
private const val PERCENTILE_MAX = 100

private fun unitsContribution(geometryValue: Int): Int = geometryValue % TENS_PER_FACE

private fun tensContribution(geometryValue: Int): Int = unitsContribution(geometryValue) * TENS_PER_FACE

fun unitsLabel(geometryValue: Int): String = unitsContribution(geometryValue).toString()

fun tensLabel(geometryValue: Int): String = "%02d".format(tensContribution(geometryValue))

/**
 * Maps a raw tens+units sum to a percentile result: 0 (00 + 0) reads as 100,
 * everything else is the sum itself. Pure and independent of [DieState] for testability.
 */
internal fun percentileValue(rawSum: Int): Int = if (rawSum == 0) PERCENTILE_MAX else rawSum

/**
 * Combined percentile result for a [tens, units] pair of [DieState]s.
 * A summed value of 0 (00 + 0) reads as 100, per the standard convention.
 */
fun percentileTotal(dieStates: List<DieState>): Int =
    percentileValue(dieStates.sumOf { it.currentFace.value })

object PercentileTensDie : DieDefinition {
    override val faces = PentagonalTrapezohedronGeometry.faces.map { face ->
        val (rx, ry, rz) = PentagonalTrapezohedronGeometry.getFaceRotation(face)
        DieFace(value = tensContribution(face.value), rotationX = rx, rotationY = ry, rotationZ = rz)
    }
}

object PercentileUnitsDie : DieDefinition {
    override val faces = PentagonalTrapezohedronGeometry.faces.map { face ->
        val (rx, ry, rz) = PentagonalTrapezohedronGeometry.getFaceRotation(face)
        DieFace(value = unitsContribution(face.value), rotationX = rx, rotationY = ry, rotationZ = rz)
    }
}
