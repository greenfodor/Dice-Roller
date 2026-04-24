package com.greenfodor.diceroller.geometry

import com.greenfodor.diceroller.ui.DiceConstants
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Point3D(val x: Float, val y: Float, val z: Float) {

    operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Point3D(x * scalar, y * scalar, z * scalar)

    fun dot(other: Point3D): Float = x * other.x + y * other.y + z * other.z

    fun cross(other: Point3D) = Point3D(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    fun normalize(): Point3D {
        val length = sqrt(x * x + y * y + z * z)
        return if (length == 0f) this else Point3D(x / length, y / length, z / length)
    }
}

data class Point2D(val x: Float, val y: Float)

/**
 * Rotates a 3D point around the X and Y axes.
 */
fun Point3D.rotatePoint(rotationX: Float, rotationY: Float): Point3D {
    val radX = Math.toRadians(rotationX.toDouble()).toFloat()
    val radY = Math.toRadians(rotationY.toDouble()).toFloat()

    // Rotate around X axis
    val cosX = cos(radX)
    val sinX = sin(radX)
    val y1 = y * cosX - z * sinX
    val z1 = y * sinX + z * cosX

    // Rotate around Y axis
    val cosY = cos(radY)
    val sinY = sin(radY)
    val x2 = x * cosY - z1 * sinY
    val z2 = x * sinY + z1 * cosY

    return Point3D(x2, y1, z2)
}

/**
 * Projects a 3D point onto 2D screen space using perspective projection.
 */
fun Point3D.projectPoint(centerX: Float, centerY: Float): Point2D {
    val scale = DiceConstants.FIELD_OF_VIEW / (DiceConstants.CAMERA_DISTANCE - z)
    return Point2D(
        x = centerX + x * scale,
        y = centerY + y * scale
    )
}
