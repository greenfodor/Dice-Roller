package com.greenfodor.diceroller.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing
import kotlinx.coroutines.launch

private const val SHEET_GRID_COLUMNS = 3
private const val SQUARE_TILE_ASPECT_RATIO = 1f

/** Corner shape shared by the picker tiles and the [DiceTypeFab]. */
internal val DiceTileShape = RoundedCornerShape(16.dp)

/** Size a tile stops growing at, so a wide grid spaces its tiles out instead of inflating them. */
private val TileMaxSize = 120.dp

private val TileBorderWidth = 2.dp
private val SheetTileIconSize = 60.dp

/**
 * Modal bottom sheet listing every [DiceType] as a tile in a three-column grid. The sheet opens
 * fully expanded and its grid scrolls vertically, so every tile stays reachable when the rows do
 * not all fit — in landscape, or once the square tiles grow with the sheet's width. A row that
 * does not fill all three columns is centered.
 *
 * Selecting a tile reports it through [onDiceTypeSelected] and then animates the sheet away,
 * calling [onDismissRequest] once it is hidden.
 *
 * @param selectedDiceType The currently active die type, marked as selected in the grid.
 * @param onDiceTypeSelected Callback when the user picks a die type.
 * @param onDismissRequest Callback when the sheet has been dismissed.
 * @param modifier Modifier for the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceTypePickerSheet(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState
    ) {
        Text(
            text = stringResource(R.string.dice_type_picker_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
        )

        DiceTypeGrid(
            columns = SHEET_GRID_COLUMNS,
            tileIconSize = SheetTileIconSize,
            selectedDiceType = selectedDiceType,
            onDiceTypeClick = { diceType ->
                onDiceTypeSelected(diceType)
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
            },
            contentPadding = PaddingValues(
                start = MaterialTheme.spacing.medium,
                top = MaterialTheme.spacing.small,
                end = MaterialTheme.spacing.medium,
                bottom = MaterialTheme.spacing.medium
            )
        )
    }
}

/**
 * Vertically scrolling grid holding one tile per [DiceType], laid out [columns] tiles to a row.
 * A row that does not fill every column splits the leftover columns evenly between both ends, so
 * its tiles keep a full column's width and sit centered.
 *
 * @param columns Number of tiles per row.
 * @param tileIconSize Size of the die icon each tile draws.
 * @param selectedDiceType The currently active die type, marked as selected in the grid.
 * @param onDiceTypeClick Callback when the user taps a tile.
 * @param contentPadding Padding applied around the rows, inside the scrolling area.
 * @param modifier Modifier for the grid.
 */
@Composable
internal fun DiceTypeGrid(
    columns: Int,
    tileIconSize: Dp,
    selectedDiceType: DiceType,
    onDiceTypeClick: (DiceType) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val rows = remember(columns) { DiceType.entries.chunked(columns) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        rows.forEach { rowDiceTypes ->
            DiceTypeRow(
                columns = columns,
                tileIconSize = tileIconSize,
                diceTypes = rowDiceTypes,
                selectedDiceType = selectedDiceType,
                onDiceTypeClick = onDiceTypeClick
            )
        }
    }
}

/**
 * One grid row. A row holding fewer than [columns] tiles splits the leftover columns evenly
 * between both ends, so its tiles keep a full column's width and sit centered. A tile is square
 * and fills its column up to [TileMaxSize], staying centered in a column wider than that.
 */
@Composable
private fun DiceTypeRow(
    columns: Int,
    tileIconSize: Dp,
    diceTypes: List<DiceType>,
    selectedDiceType: DiceType,
    onDiceTypeClick: (DiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    val edgeColumns = (columns - diceTypes.size) / 2f

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        EdgeSpacer(columns = edgeColumns)
        diceTypes.forEach { diceType ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                DiceTypeTile(
                    diceType = diceType,
                    iconSize = tileIconSize,
                    selected = diceType == selectedDiceType,
                    onClick = { onDiceTypeClick(diceType) },
                    modifier = Modifier
                        .widthIn(max = TileMaxSize)
                        .fillMaxWidth()
                )
            }
        }
        EdgeSpacer(columns = edgeColumns)
    }
}

@Composable
private fun RowScope.EdgeSpacer(columns: Float) {
    if (columns > 0f) {
        Spacer(modifier = Modifier.weight(columns))
    }
}

@Composable
private fun DiceTypeTile(
    diceType: DiceType,
    iconSize: Dp,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent

    Column(
        modifier = modifier
            .aspectRatio(SQUARE_TILE_ASPECT_RATIO)
            .clip(DiceTileShape)
            .border(width = TileBorderWidth, color = borderColor, shape = DiceTileShape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(MaterialTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(diceType.iconResId),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))

        Text(
            text = stringResource(diceType.labelResId),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@LightDarkPreview
@Composable
private fun DiceTypePickerSheetPreview() {
    DiceRollerTheme {
        DiceTypePickerSheet(
            selectedDiceType = DiceType.SINGLE_D6,
            onDiceTypeSelected = {},
            onDismissRequest = {}
        )
    }
}
