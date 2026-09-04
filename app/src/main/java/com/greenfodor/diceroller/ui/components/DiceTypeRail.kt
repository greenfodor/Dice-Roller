package com.greenfodor.diceroller.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing

private const val DICE_RAIL_GRID_COLUMNS = 2

private val RailTileIconSize = 72.dp

/**
 * Width the rail's tiles occupy; the rail itself also covers the start system-bar inset, and the
 * dice content takes the remaining window width. It holds [DICE_RAIL_GRID_COLUMNS] square tiles
 * of [RailTileIconSize] plus their labels.
 */
val DiceRailWidth = 280.dp

/**
 * Returns whether the permanent dice-selection rail replaces the floating action button and its
 * modal picker, which it does only in a landscape window of
 * [WindowWidthSizeClass.Expanded] width.
 *
 * @param widthSizeClass Width size class of the current window.
 * @param orientation Orientation of the current configuration, one of the
 * `Configuration.ORIENTATION_*` values.
 */
fun shouldShowDiceRail(widthSizeClass: WindowWidthSizeClass, orientation: Int): Boolean =
    widthSizeClass == WindowWidthSizeClass.Expanded &&
        orientation == Configuration.ORIENTATION_LANDSCAPE

/**
 * Permanently visible dice-selection rail, [DiceRailWidth] of tiles wide — plus the start
 * system-bar inset it pads them clear of — and as tall as the window it is pinned to. It lists
 * every [DiceType] as a square tile in a two-column grid that scrolls vertically and keeps its
 * first and last row clear of the system bars; a trailing row that fills only one column is
 * centered across both. Tiles carry the same selected border and
 * `Role.RadioButton` semantics as the ones in [DiceTypePickerSheet], and tapping one reports it
 * through [onDiceTypeSelected] without dismissing the rail.
 *
 * @param selectedDiceType The currently active die type, marked as selected in the rail.
 * @param onDiceTypeSelected Callback when the user picks a die type.
 * @param modifier Modifier for the rail.
 */
@Composable
fun DiceTypeRail(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    val verticalInsets = WindowInsets.safeDrawing
        .only(WindowInsetsSides.Vertical)
        .asPaddingValues()
    val startInset = WindowInsets.safeDrawing
        .only(WindowInsetsSides.Start)
        .asPaddingValues()
        .calculateStartPadding(LocalLayoutDirection.current)

    Surface(
        modifier = modifier
            .width(DiceRailWidth + startInset)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        DiceTypeGrid(
            columns = DICE_RAIL_GRID_COLUMNS,
            tileIconSize = RailTileIconSize,
            selectedDiceType = selectedDiceType,
            onDiceTypeClick = onDiceTypeSelected,
            contentPadding = PaddingValues(
                start = MaterialTheme.spacing.medium,
                top = MaterialTheme.spacing.medium + verticalInsets.calculateTopPadding(),
                end = MaterialTheme.spacing.medium,
                bottom = MaterialTheme.spacing.medium + verticalInsets.calculateBottomPadding()
            ),
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Start)
            )
        )
    }
}

@LightDarkPreview
@Composable
private fun DiceTypeRailPreview() {
    DiceRollerTheme {
        DiceTypeRail(
            selectedDiceType = DiceType.SINGLE_D20,
            onDiceTypeSelected = {}
        )
    }
}
