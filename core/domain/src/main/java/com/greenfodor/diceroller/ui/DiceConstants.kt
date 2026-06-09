package com.greenfodor.diceroller.ui

import com.greenfodor.diceroller.geometry.Point3D

object DiceConstants {
    // Camera and Projection
    const val CAMERA_DISTANCE = 800f
    const val FIELD_OF_VIEW = 500f

    // Animation
    const val ROLL_DURATION_MILLIS = 2_000
    const val RESULT_ENTER_LEAD_MILLIS = 375 // how long before the dice settle the pop-in begins
    const val RESULT_EXIT_MILLIS = 100
    const val RESULT_HIDDEN_SCALE = 0.3f
    const val THEME_TRANSITION_DURATION_MILLIS = 500
    const val ROTATION_SPIN_COUNT = 3
    const val FULL_ROTATION = 360f

    // Cube Appearance
    const val DEFAULT_CUBE_SIZE = 320f
    const val STROKE_WIDTH = 2f

    // D6 specific
    const val D6_STROKE_ALPHA = 0.5f
    const val D6_DOT_ALPHA = 0.9f

    // D6 number faces. The label is mapped onto the face quad via setPolyToPoly; the source
    // square spans [-D6_NUMBER_UV, D6_NUMBER_UV] on both axes, matching the face's UV corners.
    const val D6_NUMBER_UV = 100f
    const val D6_NUMBER_TEXT_SIZE_UV = 85f

    // Face Rendering
    const val DOT_OFFSET_FACTOR = 0.1f
    const val MIN_SHADING_INTENSITY = 0.4f
    const val MAX_SHADING_INTENSITY = 1.0f

    // Dots
    const val DOT_RADIUS_FACTOR = 0.15f
    const val DOT_SPACING_FACTOR = 0.55f
    const val DOT_SEGMENTS = 64

    // D20 specific
    val D20_AMBIGUOUS_LABELS = setOf("6", "9")
    const val D20_TEXT_SIZE_UV = 55f
    const val D20_TEXT_BASELINE_ADJUSTMENT = -10f
    const val D20_FACE_ALPHA = 0.9f
    const val D20_STROKE_ALPHA = 0.3f
    const val D20_UNDERLINE_WIDTH_UV = 28f
    const val D20_UNDERLINE_HEIGHT_UV = 4f
    const val D20_UNDERLINE_TOP_OFFSET_UV = 2f

    const val D10_UNDERLINE_WIDTH_UV = 28f
    const val D10_UNDERLINE_HEIGHT_UV = 4f
    const val D10_UNDERLINE_TOP_OFFSET_UV = 10f

    // The label is mapped onto the kite's top triangle (apex + the two side vertices), whose
    // centroid sits above the kite's true centroid. This shifts the baseline down (+y) to the
    // kite's area centroid so the number is centered on the face instead of riding up toward the
    // pointy tip. The kite is elongated toward the apex, so its area centroid (~+12) sits above
    // its vertex centroid (~+21).
    const val D10_TEXT_BASELINE_ADJUSTMENT = 12f

    // UV space triangle for D20 (radius 100)
    const val D20_UV_X = 86.6f
    const val D20_UV_Y_TOP = -100f
    const val D20_UV_Y_BOTTOM = 50f
    const val D20_UV_X_TOP = 0f

    val D20_SRC_TRIANGLE = floatArrayOf(
        D20_UV_X_TOP,
        D20_UV_Y_TOP,
        D20_UV_X,
        D20_UV_Y_BOTTOM,
        -D20_UV_X,
        D20_UV_Y_BOTTOM
    )

    // Source quad mapped onto a face via setPolyToPoly, in the same order as the face's vertex
    // list. The projected face winding places vertex 0 at the bottom-right, so the square is
    // rotated 180° (every coordinate negated) to keep the upright digit reading upright.
    val D6_SRC_QUAD = floatArrayOf(
        D6_NUMBER_UV,
        D6_NUMBER_UV,
        -D6_NUMBER_UV,
        D6_NUMBER_UV,
        -D6_NUMBER_UV,
        -D6_NUMBER_UV,
        D6_NUMBER_UV,
        -D6_NUMBER_UV
    )

    val LIGHT_SOURCE by lazy { Point3D(0.5f, -1f, 1.5f).normalize() }
}
