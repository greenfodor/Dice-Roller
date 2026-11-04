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

/** The filled Material "Check" icon. */
val DiceIcons.Check: ImageVector
    get() = check

private val check: ImageVector by lazy {
    materialIcon(name = "Filled.Check") {
        moveTo(9.0f, 16.17f)
        lineTo(4.83f, 12.0f)
        lineToRelative(-1.42f, 1.41f)
        lineTo(9.0f, 19.0f)
        lineTo(21.0f, 7.0f)
        lineToRelative(-1.41f, -1.41f)
        close()
    }
}
