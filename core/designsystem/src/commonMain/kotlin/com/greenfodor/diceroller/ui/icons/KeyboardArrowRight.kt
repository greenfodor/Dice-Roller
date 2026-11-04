/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("MagicNumber")

package com.greenfodor.diceroller.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector

/** The auto-mirrored filled Material "KeyboardArrowRight" icon. */
val DiceIcons.KeyboardArrowRight: ImageVector
    get() = keyboardArrowRight

private val keyboardArrowRight: ImageVector by lazy {
    materialIcon(name = "AutoMirrored.Filled.KeyboardArrowRight", autoMirror = true) {
        moveTo(8.59f, 16.59f)
        lineTo(13.17f, 12.0f)
        lineTo(8.59f, 7.41f)
        lineTo(10.0f, 6.0f)
        lineToRelative(6.0f, 6.0f)
        lineToRelative(-6.0f, 6.0f)
        lineToRelative(-1.41f, -1.41f)
        close()
    }
}
