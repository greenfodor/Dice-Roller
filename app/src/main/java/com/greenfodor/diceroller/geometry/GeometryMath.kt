package com.greenfodor.diceroller.geometry

import com.greenfodor.diceroller.ui.DiceConstants
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a point in 3D space.
 */
data class Point3D(
    val x: Float,
    val y: Float,
    val z: Float
) {
    operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)

    operator fun times(scalar: Float) = Point3D(x * scalar, y * scalar, z * scalar)

    /**
     * Calculates the dot product between this vector and another.
     */
    fun dot(other: Point3D): Float = (x * other.x) + (y * other.y) + (z * other.z)

    /**
     * Calculates the cross product between this vector and another.
     */
    fun cross(other: Point3D) =
        Point3D(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )

    /**
     * Returns a unit vector pointing in the same direction as this one.
     */
    fun normalize(): Point3D {
        val length = sqrt(x * x + y * y + z * z)
        return if (length == 0f) this else Point3D(x / length, y / length, z / length)
    }
}

/**
 * Represents a point in 2D screen space.
 */
data class Point2D(
    val x: Float,
    val y: Float
)

/**
 * Rotates a 3D point around the X, Y and Z axes in that order.
 */
fun Point3D.rotatePoint(
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float = 0f
): Point3D {
    val radX = Math.toRadians(rotationX.toDouble()).toFloat()
    val radY = Math.toRadians(rotationY.toDouble()).toFloat()
    val radZ = Math.toRadians(rotationZ.toDouble()).toFloat()

    // Rotate around X axis
    val cosX = cos(radX)
    val sinX = sin(radX)
    val y1 = y * cosX - z * sinX
    val z1 = y * sinX + z * cosX

    // Rotate around Y axis
    val cosY = cos(radY)
    val sinY = sin(radY)
    val x2 = x * cosY + z1 * sinY
    val z2 = -x * sinY + z1 * cosY

    // Rotate around Z axis
    val cosZ = cos(radZ)
    val sinZ = sin(radZ)
    val x3 = x2 * cosZ - y1 * sinZ
    val y3 = x2 * sinZ + y1 * cosZ

    return Point3D(x3, y3, z2)
}

/**
 * Projects a 3D point onto 2D screen space using perspective projection.
 */
fun Point3D.projectPoint(
    centerX: Float,
    centerY: Float
): Point2D {
    val scale = DiceConstants.FIELD_OF_VIEW / (DiceConstants.CAMERA_DISTANCE - z)
    return Point2D(
        x = centerX + x * scale,
        y = centerY + y * scale
    )
}

/**
 * Calculates the Euler rotation (X, Y, Z degrees) required to bring a triangular face
 * front-facing and upright. The first vertex in [face] is oriented to point straight up.
 *
 * Shared by all polyhedra that use triangular faces (icosahedron, tetrahedron, etc.).
 */
fun calculateFaceRotation(vertices: List<Point3D>, face: GeometryFace): Triple<Float, Float, Float> {
    val v0 = vertices[face.vertexIndices[0]]
    val v1 = vertices[face.vertexIndices[1]]
    val v2 = vertices[face.vertexIndices[2]]

    val normal = (v1 - v0).cross(v2 - v0).normalize()

    val rx = atan2(normal.y, normal.z)
    val rxDeg = rx * 180f / PI.toFloat()

    val ry = atan2(-normal.x, sqrt(normal.y * normal.y + normal.z * normal.z))
    val ryDeg = ry * 180f / PI.toFloat()

    val center = (v0 + v1 + v2) * (1f / 3f)
    val v0Rotated = v0.rotatePoint(rxDeg, ryDeg, 0f)
    val centerRotated = center.rotatePoint(rxDeg, ryDeg, 0f)

    val dir = v0Rotated - centerRotated
    val currentAngle = atan2(dir.y, dir.x)

    val rz = -(PI.toFloat() / 2f + currentAngle)
    val rzDeg = rz * 180f / PI.toFloat()

    return Triple(rxDeg, ryDeg, rzDeg)
}

/**
 * Calculates the Z-component of the surface normal for a face defined by three vertices.
 * Used for back-face culling. If the result is positive, the face is pointing towards the camera.
 */
fun calculateNormalZ(
    v0: Point3D,
    v1: Point3D,
    v3: Point3D
): Float = (v1.x - v0.x) * (v3.y - v0.y) - (v1.y - v0.y) * (v3.x - v0.x)

/**
 * Maps a 2D face coordinate (u, v in [-1, 1]) to 3D world space
 * using bilinear interpolation across four face vertices.
 *
 * @param normalOffset Small vector that lifts the result slightly above the face surface.
 */
fun interpolatePoint3DOnFace(
    u: Float,
    v: Float,
    vVertices: List<Point3D>,
    normalOffset: Point3D = Point3D(0f, 0f, 0f)
): Point3D {
    val s = (u + 1f) / 2f
    val t = (v + 1f) / 2f
    val (v0, v1, v2, v3) = vVertices
    return Point3D(
        x = (1 - s) * (1 - t) * v0.x + s * (1 - t) * v1.x + s * t * v2.x + (1 - s) * t * v3.x,
        y = (1 - s) * (1 - t) * v0.y + s * (1 - t) * v1.y + s * t * v2.y + (1 - s) * t * v3.y,
        z = (1 - s) * (1 - t) * v0.z + s * (1 - t) * v1.z + s * t * v2.z + (1 - s) * t * v3.z
    ) + normalOffset
}
