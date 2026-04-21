package com.greenfodor.diceroller.geometry

import androidx.compose.ui.geometry.Offset
import com.greenfodor.diceroller.ui.DiceConstants
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A representation of a point in 3D space.
 *
 * @property x The coordinate along the horizontal axis.
 * @property y The coordinate along the vertical axis.
 * @property z The coordinate along the depth axis (positive is closer to the camera).
 */
data class Point3D(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Point3D(x * scalar, y * scalar, z * scalar)

    /**
     * Calculates the dot product between this point (vector) and another.
     */
    fun dot(other: Point3D) = x * other.x + y * other.y + z * other.z

    /**
     * Normalizes the vector to have a length of 1.
     */
    fun normalize(): Point3D {
        val len = sqrt(x * x + y * y + z * z)
        return if (len > 0) Point3D(x / len, y / len, z / len) else this
    }

    /**
     * Calculates the cross product between this point (vector) and another.
     * Used for calculating face normals in 3D rendering.
     */
    fun cross(other: Point3D) = Point3D(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
}

/**
 * Rotates a 3D point around the X and Y axes.
 *
 * @param point The original point to rotate.
 * @param angleX Rotation angle around the X axis in degrees.
 * @param angleY Rotation angle around the Y axis in degrees.
 * @return The new rotated [Point3D].
 */
fun rotatePoint(point: Point3D, angleX: Float, angleY: Float): Point3D {
    val radX = Math.toRadians(angleX.toDouble())
    val radY = Math.toRadians(angleY.toDouble())

    val x = point.x
    val y = point.y
    val z = point.z

    val cosY = cos(radY).toFloat()
    val sinY = sin(radY).toFloat()
    val x1 = x * cosY - z * sinY
    val z1 = x * sinY + z * cosY

    val cosX = cos(radX).toFloat()
    val sinX = sin(radX).toFloat()
    val y2 = y * cosX - z1 * sinX
    val z2 = y * sinX + z1 * cosX

    return Point3D(x1, y2, z2)
}

/**
 * Projects a 3D point onto a 2D screen coordinate system.
 * Uses a simple perspective projection based on [DiceConstants.CAMERA_DISTANCE]
 * and [DiceConstants.FIELD_OF_VIEW].
 *
 * @param point The 3D point to project.
 * @param centerX The X center of the screen/canvas.
 * @param centerY The Y center of the screen/canvas.
 * @return An [Offset] representing the 2D screen coordinates.
 */
fun projectPoint(point: Point3D, centerX: Float, centerY: Float): Offset {
    val factor = DiceConstants.FIELD_OF_VIEW / (DiceConstants.CAMERA_DISTANCE - point.z)

    return Offset(
        centerX + point.x * factor,
        centerY + point.y * factor
    )
}
