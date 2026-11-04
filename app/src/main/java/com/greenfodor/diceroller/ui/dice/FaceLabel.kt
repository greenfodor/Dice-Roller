package com.greenfodor.diceroller.ui.dice

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.greenfodor.diceroller.geometry.COEFF_PERSPECTIVE_W
import com.greenfodor.diceroller.geometry.COEFF_PERSPECTIVE_X
import com.greenfodor.diceroller.geometry.COEFF_PERSPECTIVE_Y
import com.greenfodor.diceroller.geometry.COEFF_SCALE_X
import com.greenfodor.diceroller.geometry.COEFF_SCALE_Y
import com.greenfodor.diceroller.geometry.COEFF_SKEW_X
import com.greenfodor.diceroller.geometry.COEFF_SKEW_Y
import com.greenfodor.diceroller.geometry.COEFF_TRANSLATE_X
import com.greenfodor.diceroller.geometry.COEFF_TRANSLATE_Y

private val UnitDensity = Density(density = 1f, fontScale = 1f)

/**
 * Caches the [TextLayoutResult] of each die face label, keyed by label, font size and colour.
 *
 * Labels are measured as bold text in the default font family at a density of 1, so the font size
 * is in the units of the coordinate space the label is drawn in. The cache holds layouts for one
 * [TextMeasurer], font size and colour at a time; a request with a different one of these drops the
 * cached layouts and re-reads the font metrics.
 */
class FaceLabelLayouts {
    private val layouts = HashMap<String, TextLayoutResult>()
    private var measurer: TextMeasurer? = null
    private var fontSize = Float.NaN
    private var color = Color.Unspecified

    /** Font ascent at the font size of the latest [layout] call (negative, above the baseline). */
    var ascent = 0f
        private set

    /** Font descent at the font size of the latest [layout] call (positive, below the baseline). */
    var descent = 0f
        private set

    /** Returns the cached layout of [label], measuring it with [textMeasurer] on a cache miss. */
    fun layout(textMeasurer: TextMeasurer, label: String, fontSize: Float, color: Color): TextLayoutResult {
        if (textMeasurer !== measurer || fontSize != this.fontSize || color != this.color) {
            layouts.clear()
            measurer = textMeasurer
            this.fontSize = fontSize
            this.color = color
            val reference = textMeasurer.measureLabel(METRICS_REFERENCE_TEXT, METRICS_REFERENCE_SIZE, color)
            ascent = (reference.getLineTop(0) - reference.firstBaseline) / METRICS_REFERENCE_SIZE * fontSize
            descent = (reference.getLineBottom(0) - reference.firstBaseline) / METRICS_REFERENCE_SIZE * fontSize
        }
        return layouts.getOrPut(label) { textMeasurer.measureLabel(label, fontSize, color) }
    }

    private fun TextMeasurer.measureLabel(text: String, fontSize: Float, color: Color): TextLayoutResult =
        measure(
            text = text,
            style = TextStyle(color = color, fontSize = fontSize.sp, fontWeight = FontWeight.Bold),
            softWrap = false,
            maxLines = 1,
            density = UnitDensity
        )

    private companion object {
        /** Font size of the reference layout the font's ascent and descent per unit of size are read from. */
        const val METRICS_REFERENCE_SIZE = 16_384f
        const val METRICS_REFERENCE_TEXT = "0"
    }
}

/** Draws [layout] horizontally centred on x = 0 with its first baseline at y = [baseline]. */
internal fun DrawScope.drawCenteredLabel(layout: TextLayoutResult, baseline: Float) {
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(-layout.multiParagraph.getLineWidth(0) / 2f, baseline - layout.firstBaseline)
    )
}

/**
 * Sets this matrix to the 2D affine transform described by [coefficients], laid out as written by
 * [com.greenfodor.diceroller.geometry.triangleToTriangleTransform].
 */
internal fun Matrix.setAffine(coefficients: FloatArray) {
    reset()
    values[Matrix.ScaleX] = coefficients[COEFF_SCALE_X]
    values[Matrix.SkewX] = coefficients[COEFF_SKEW_X]
    values[Matrix.TranslateX] = coefficients[COEFF_TRANSLATE_X]
    values[Matrix.SkewY] = coefficients[COEFF_SKEW_Y]
    values[Matrix.ScaleY] = coefficients[COEFF_SCALE_Y]
    values[Matrix.TranslateY] = coefficients[COEFF_TRANSLATE_Y]
}

/**
 * Sets this matrix to the 2D projective transform described by [coefficients], laid out as written
 * by [com.greenfodor.diceroller.geometry.quadToQuadTransform].
 */
internal fun Matrix.setProjective(coefficients: FloatArray) {
    setAffine(coefficients)
    values[Matrix.Perspective0] = coefficients[COEFF_PERSPECTIVE_X]
    values[Matrix.Perspective1] = coefficients[COEFF_PERSPECTIVE_Y]
    values[Matrix.Perspective2] = coefficients[COEFF_PERSPECTIVE_W]
}
