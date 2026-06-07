package com.greenfodor.diceroller.ui.preview

import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotation that renders a composable on a background in both light and dark themes.
 *
 * Replaces the light/dark `@Preview` pair that was repeated on every previewable composable. Each
 * preview is still distinguished by its function name, so the per-use names previously passed to
 * `@Preview` are no longer needed.
 */
@Preview(name = "Light", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
annotation class LightDarkPreview
