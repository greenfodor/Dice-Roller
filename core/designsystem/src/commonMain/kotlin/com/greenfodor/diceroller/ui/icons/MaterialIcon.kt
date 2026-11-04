package com.greenfodor.diceroller.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private const val MATERIAL_ICON_SIZE = 24f

/**
 * Builds a 24dp Material icon from a single black-filled path with the Material icon defaults.
 *
 * @param name the full name of the icon
 * @param autoMirror whether the icon is mirrored in right-to-left layouts
 * @param pathBuilder the path commands of the icon
 */
internal inline fun materialIcon(
    name: String,
    autoMirror: Boolean = false,
    pathBuilder: PathBuilder.() -> Unit
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = MATERIAL_ICON_SIZE.dp,
    defaultHeight = MATERIAL_ICON_SIZE.dp,
    viewportWidth = MATERIAL_ICON_SIZE,
    viewportHeight = MATERIAL_ICON_SIZE,
    autoMirror = autoMirror
).path(
    fill = SolidColor(Color.Black),
    fillAlpha = 1f,
    stroke = null,
    strokeAlpha = 1f,
    strokeLineWidth = 1f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Bevel,
    strokeLineMiter = 1f,
    pathFillType = PathFillType.NonZero,
    pathBuilder = pathBuilder
).build()
