package com.greenfodor.diceroller.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing
import kotlinx.coroutines.launch

private const val DICE_TYPE_GRID_COLUMNS = 3

private val DiceTypeRows = DiceType.entries.chunked(DICE_TYPE_GRID_COLUMNS)

private val TileShape = RoundedCornerShape(16.dp)
private val TileBorderWidth = 2.dp
private val TileIconSize = 60.dp
private val TileMinHeight = 104.dp

/**
 * Modal bottom sheet listing every [DiceType] as a tile in a three-column grid. The sheet opens
 * fully expanded and its grid scrolls vertically, so every tile stays reachable when the rows do
 * not all fit — in landscape, or once the tiles grow at raised font scales. A row that does not
 * fill all three columns is centered.
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = MaterialTheme.spacing.medium,
                    top = MaterialTheme.spacing.small,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            DiceTypeRows.forEach { rowDiceTypes ->
                DiceTypeRow(
                    diceTypes = rowDiceTypes,
                    selectedDiceType = selectedDiceType,
                    onDiceTypeClick = { diceType ->
                        onDiceTypeSelected(diceType)
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
                    }
                )
            }
        }
    }
}

/**
 * One grid row. A row holding fewer than [DICE_TYPE_GRID_COLUMNS] tiles splits the leftover
 * columns evenly between both ends, so its tiles keep a full column's width and sit centered.
 */
@Composable
private fun DiceTypeRow(
    diceTypes: List<DiceType>,
    selectedDiceType: DiceType,
    onDiceTypeClick: (DiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    val edgeColumns = (DICE_TYPE_GRID_COLUMNS - diceTypes.size) / 2f

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        EdgeSpacer(columns = edgeColumns)
        diceTypes.forEach { diceType ->
            DiceTypeTile(
                diceType = diceType,
                selected = diceType == selectedDiceType,
                onClick = { onDiceTypeClick(diceType) },
                modifier = Modifier.weight(1f)
            )
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
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent

    Column(
        modifier = modifier
            .heightIn(min = TileMinHeight)
            .clip(TileShape)
            .border(width = TileBorderWidth, color = borderColor, shape = TileShape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(MaterialTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(diceType.iconResId),
            contentDescription = null,
            modifier = Modifier.size(TileIconSize)
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
