package com.greenfodor.diceroller.ui

import com.greenfodor.diceroller.geometry.Point3D

object DiceConstants {
    // Camera and Projection
    const val CAMERA_DISTANCE = 800f
    const val FIELD_OF_VIEW = 500f

    // Animation
    const val ROLL_DURATION_MILLIS = 2_000
    const val THEME_TRANSITION_DURATION_MILLIS = 500
    const val ICON_ROTATION_DURATION_MILLIS = 500
    const val ROTATION_SPIN_COUNT = 3
    const val FULL_ROTATION = 360f

    // Cube Appearance
    const val DEFAULT_CUBE_SIZE = 320f
    const val STROKE_WIDTH = 2f

    // D6 specific
    const val D6_STROKE_ALPHA = 0.5f
    const val D6_DOT_ALPHA = 0.9f

    // Face Rendering
    const val DOT_OFFSET_FACTOR = 0.1f
    const val MIN_SHADING_INTENSITY = 0.4f
    const val MAX_SHADING_INTENSITY = 1.0f

    // Dots
    const val DOT_RADIUS_FACTOR = 0.15f
    const val DOT_SPACING_FACTOR = 0.55f
    const val DOT_SEGMENTS = 64

    // D20 specific
    const val D20_TEXT_SIZE_UV = 65f
    const val D20_TEXT_BASELINE_ADJUSTMENT = 10f
    const val D20_FACE_ALPHA = 0.9f
    const val D20_STROKE_ALPHA = 0.3f

    // UV space triangle for D20 (radius 100)
    const val D20_UV_X = 86.6f
    const val D20_UV_Y_TOP = -100f
    const val D20_UV_Y_BOTTOM = 50f
    const val D20_UV_X_TOP = 0f

    val D20_SRC_TRIANGLE =
        floatArrayOf(
            D20_UV_X_TOP,
            D20_UV_Y_TOP,
            D20_UV_X,
            D20_UV_Y_BOTTOM,
            -D20_UV_X,
            D20_UV_Y_BOTTOM,
        )

    val LIGHT_SOURCE by lazy { Point3D(0.5f, -1f, 1.5f).normalize() }
}
