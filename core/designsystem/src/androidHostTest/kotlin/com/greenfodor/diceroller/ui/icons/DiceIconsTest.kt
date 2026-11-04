package com.greenfodor.diceroller.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.test.Test
import kotlin.test.assertEquals

class DiceIconsTest {
    private val icons: List<Pair<ImageVector, ImageVector>> = listOf(
        Icons.AutoMirrored.Filled.ArrowBack to DiceIcons.ArrowBack,
        Icons.AutoMirrored.Filled.KeyboardArrowRight to DiceIcons.KeyboardArrowRight,
        Icons.Filled.BrightnessAuto to DiceIcons.BrightnessAuto,
        Icons.Filled.Casino to DiceIcons.Casino,
        Icons.Filled.Check to DiceIcons.Check,
        Icons.Filled.DarkMode to DiceIcons.DarkMode,
        Icons.Filled.LightMode to DiceIcons.LightMode,
        Icons.Filled.Numbers to DiceIcons.Numbers,
        Icons.Filled.Refresh to DiceIcons.Refresh,
        Icons.Filled.Settings to DiceIcons.Settings,
        Icons.Outlined.History to DiceIcons.History
    )

    @Test
    fun vendoredIconsMatchMaterialIcons() {
        icons.forEach { (material, vendored) ->
            assertEquals(material.name, vendored.name)
            assertEquals(material.defaultWidth, vendored.defaultWidth)
            assertEquals(material.defaultHeight, vendored.defaultHeight)
            assertEquals(material.viewportWidth, vendored.viewportWidth)
            assertEquals(material.viewportHeight, vendored.viewportHeight)
            assertEquals(material.autoMirror, vendored.autoMirror)
            assertEquals(material.root, vendored.root, material.name)
        }
    }
}
