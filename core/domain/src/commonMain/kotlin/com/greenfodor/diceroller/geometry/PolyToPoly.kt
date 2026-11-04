package com.greenfodor.diceroller.geometry

import kotlin.math.abs

/** Number of coefficients written by [triangleToTriangleTransform]. */
const val AFFINE_COEFFICIENT_COUNT = 6

/** Number of coefficients written by [quadToQuadTransform]. */
const val PROJECTIVE_COEFFICIENT_COUNT = 9

/** Index of the x scale coefficient (`a` in `x' = a*x + b*y + c`). */
const val COEFF_SCALE_X = 0

/** Index of the x skew coefficient (`b` in `x' = a*x + b*y + c`). */
const val COEFF_SKEW_X = 1

/** Index of the x translation coefficient (`c` in `x' = a*x + b*y + c`). */
const val COEFF_TRANSLATE_X = 2

/** Index of the y skew coefficient (`d` in `y' = d*x + e*y + f`). */
const val COEFF_SKEW_Y = 3

/** Index of the y scale coefficient (`e` in `y' = d*x + e*y + f`). */
const val COEFF_SCALE_Y = 4

/** Index of the y translation coefficient (`f` in `y' = d*x + e*y + f`). */
const val COEFF_TRANSLATE_Y = 5

/** Index of the x perspective coefficient (`g` in `w = g*x + h*y + i`). */
const val COEFF_PERSPECTIVE_X = 6

/** Index of the y perspective coefficient (`h` in `w = g*x + h*y + i`). */
const val COEFF_PERSPECTIVE_Y = 7

/** Index of the perspective scale coefficient (`i` in `w = g*x + h*y + i`). */
const val COEFF_PERSPECTIVE_W = 8

private const val COLLINEAR_TOLERANCE = 1e-6

/**
 * Computes the affine transform that maps the triangle [src] onto the triangle [dst] (the
 * three-point case of a polygon-to-polygon mapping). Both arrays hold `x0, y0, x1, y1, x2, y2`;
 * point `i` of [src] maps onto point `i` of [dst].
 *
 * The six coefficients are written to [out] at [COEFF_SCALE_X]..[COEFF_TRANSLATE_Y], describing
 * `x' = a*x + b*y + c` and `y' = d*x + e*y + f` as `[a, b, c, d, e, f]`.
 *
 * Returns `false` and leaves [out] unchanged when [src] is degenerate: its points coincide or are
 * collinear (the doubled triangle area is at most `1e-6` times the summed squared edge lengths
 * from point 0). A degenerate [dst] is valid and yields a singular transform that collapses the
 * plane onto a line or a point.
 */
fun triangleToTriangleTransform(src: FloatArray, dst: FloatArray, out: FloatArray): Boolean {
    val p0x = src[0].toDouble()
    val p0y = src[1].toDouble()
    val e1x = src[2] - p0x
    val e1y = src[3] - p0y
    val e2x = src[4] - p0x
    val e2y = src[5] - p0y
    val det = e1x * e2y - e1y * e2x
    val scale = e1x * e1x + e1y * e1y + e2x * e2x + e2y * e2y
    if (scale == 0.0 || abs(det) <= COLLINEAR_TOLERANCE * scale) return false

    val q0x = dst[0].toDouble()
    val q0y = dst[1].toDouble()
    val f1x = dst[2] - q0x
    val f1y = dst[3] - q0y
    val f2x = dst[4] - q0x
    val f2y = dst[5] - q0y

    val a = (f1x * e2y - f2x * e1y) / det
    val b = (f2x * e1x - f1x * e2x) / det
    val d = (f1y * e2y - f2y * e1y) / det
    val e = (f2y * e1x - f1y * e2x) / det

    out[COEFF_SCALE_X] = a.toFloat()
    out[COEFF_SKEW_X] = b.toFloat()
    out[COEFF_TRANSLATE_X] = (q0x - a * p0x - b * p0y).toFloat()
    out[COEFF_SKEW_Y] = d.toFloat()
    out[COEFF_SCALE_Y] = e.toFloat()
    out[COEFF_TRANSLATE_Y] = (q0y - d * p0x - e * p0y).toFloat()
    return true
}

/**
 * Computes the projective transform (homography) that maps the quadrilateral [src] onto the
 * quadrilateral [dst] (the four-point case of a polygon-to-polygon mapping). Both arrays hold
 * `x0, y0, x1, y1, x2, y2, x3, y3`; point `i` of [src] maps onto point `i` of [dst].
 *
 * The nine coefficients are written to [out] at [COEFF_SCALE_X]..[COEFF_PERSPECTIVE_W],
 * describing `x' = (a*x + b*y + c) / w`, `y' = (d*x + e*y + f) / w` with `w = g*x + h*y + i`, as
 * `[a, b, c, d, e, f, g, h, i]`, scaled so that `i == 1` whenever `i` is non-zero.
 *
 * Returns `false` and leaves [out] unchanged when either quadrilateral is degenerate: any three of
 * its points are collinear or coincide (same relative tolerance as [triangleToTriangleTransform]).
 */
fun quadToQuadTransform(src: FloatArray, dst: FloatArray, out: FloatArray): Boolean {
    if (hasCollinearTriple(src) || hasCollinearTriple(dst)) return false
    return squareToQuad(src) { sa, sb, sc, sd, se, sf, sg, sh ->
        // Adjugate of the square-to-src matrix, i.e. src-to-square up to scale.
        val ia = se - sf * sh
        val ib = sc * sh - sb
        val ic = sb * sf - sc * se
        val id = sf * sg - sd
        val ie = sa - sc * sg
        val iF = sc * sd - sa * sf
        val ig = sd * sh - se * sg
        val ih = sb * sg - sa * sh
        val ii = sa * se - sb * sd
        squareToQuad(dst) { da, db, dc, dd, de, df, dg, dh ->
            val ha = da * ia + db * id + dc * ig
            val hb = da * ib + db * ie + dc * ih
            val hc = da * ic + db * iF + dc * ii
            val hd = dd * ia + de * id + df * ig
            val he = dd * ib + de * ie + df * ih
            val hf = dd * ic + de * iF + df * ii
            val hg = dg * ia + dh * id + ig
            val hh = dg * ib + dh * ie + ih
            val hi = dg * ic + dh * iF + ii
            val norm = if (hi == 0.0) 1.0 else hi
            out[COEFF_SCALE_X] = (ha / norm).toFloat()
            out[COEFF_SKEW_X] = (hb / norm).toFloat()
            out[COEFF_TRANSLATE_X] = (hc / norm).toFloat()
            out[COEFF_SKEW_Y] = (hd / norm).toFloat()
            out[COEFF_SCALE_Y] = (he / norm).toFloat()
            out[COEFF_TRANSLATE_Y] = (hf / norm).toFloat()
            out[COEFF_PERSPECTIVE_X] = (hg / norm).toFloat()
            out[COEFF_PERSPECTIVE_Y] = (hh / norm).toFloat()
            out[COEFF_PERSPECTIVE_W] = (hi / norm).toFloat()
            true
        }
    }
}

/**
 * Passes the homography `[a b c; d e f; g h 1]` that maps the unit square corners
 * (0,0), (1,0), (1,1), (0,1) onto the four points of [quad] to [block].
 */
private inline fun <R> squareToQuad(
    quad: FloatArray,
    block: (Double, Double, Double, Double, Double, Double, Double, Double) -> R
): R {
    val x0 = quad[0].toDouble()
    val y0 = quad[1].toDouble()
    val x1 = quad[2].toDouble()
    val y1 = quad[3].toDouble()
    val x2 = quad[4].toDouble()
    val y2 = quad[5].toDouble()
    val x3 = quad[6].toDouble()
    val y3 = quad[7].toDouble()
    val dx1 = x1 - x2
    val dy1 = y1 - y2
    val dx2 = x3 - x2
    val dy2 = y3 - y2
    val dx3 = x0 - x1 + x2 - x3
    val dy3 = y0 - y1 + y2 - y3
    val den = dx1 * dy2 - dx2 * dy1
    val g = (dx3 * dy2 - dx2 * dy3) / den
    val h = (dx1 * dy3 - dx3 * dy1) / den
    return block(x1 - x0 + g * x1, x3 - x0 + h * x3, x0, y1 - y0 + g * y1, y3 - y0 + h * y3, y0, g, h)
}

private fun hasCollinearTriple(quad: FloatArray): Boolean =
    isCollinear(quad, 0, 1, 2) ||
        isCollinear(quad, 0, 1, 3) ||
        isCollinear(quad, 0, 2, 3) ||
        isCollinear(quad, 1, 2, 3)

private fun isCollinear(points: FloatArray, i: Int, j: Int, k: Int): Boolean {
    val px = points[i * 2].toDouble()
    val py = points[i * 2 + 1].toDouble()
    val e1x = points[j * 2] - px
    val e1y = points[j * 2 + 1] - py
    val e2x = points[k * 2] - px
    val e2y = points[k * 2 + 1] - py
    val det = e1x * e2y - e1y * e2x
    val scale = e1x * e1x + e1y * e1y + e2x * e2x + e2y * e2y
    return scale == 0.0 || abs(det) <= COLLINEAR_TOLERANCE * scale
}
