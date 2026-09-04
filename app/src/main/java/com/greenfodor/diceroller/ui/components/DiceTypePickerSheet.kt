package com.greenfodor.diceroller.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing
import kotlinx.coroutines.launch
import kotlin.math.max

private const val SHEET_GRID_COLUMNS = 3
private const val SQUARE_TILE_ASPECT_RATIO = 1f
private const val HANDLE_ALPHA = 0.4f

/** Corner shape shared by the picker tiles. */
internal val DiceTileShape = RoundedCornerShape(16.dp)

/** Size a tile stops growing at, so a wide grid spaces its tiles out instead of inflating them. */
private val TileMaxSize = 120.dp

private val TileBorderWidth = 2.dp
private val SheetTileIconSize = 60.dp

private val HandleAreaHeight = 44.dp
private val HandleAreaWidth = 96.dp
private val HandleThickness = 4.dp
private val ChevronHalfWidth = 30.dp
private val ChevronRise = 14.dp
private val BarHalfWidth = 24.dp

/**
 * Standard bottom sheet listing every [DiceType] as a tile in a three-column grid, peeking above
 * [content] at all times so only its drag handle shows while collapsed. The handle drags and taps
 * between the collapsed and the fully expanded state, and morphs from a large chevron into the
 * Material 3 handle bar as it expands.
 *
 * Selecting a tile reports it through [onDiceTypeSelected] and animates the sheet back to its
 * collapsed peek height.
 *
 * @param selectedDiceType The currently active die type, marked as selected in the grid.
 * @param onDiceTypeSelected Callback when the user picks a die type.
 * @param modifier Modifier for the scaffold hosting the sheet.
 * @param content The screen the sheet peeks over.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceTypePickerSheet(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    val bottomInset = WindowInsets.safeDrawing
        .only(WindowInsetsSides.Bottom)
        .asPaddingValues()
        .calculateBottomPadding()
    val peekHeight = HandleAreaHeight + bottomInset
    val peekHeightPx = with(LocalDensity.current) { peekHeight.toPx() }

    var layoutHeightPx by remember { mutableFloatStateOf(0f) }
    var contentHeightPx by remember { mutableFloatStateOf(0f) }

    BottomSheetScaffold(
        sheetContent = {
            Column(modifier = Modifier.onSizeChanged { contentHeightPx = it.height.toFloat() }) {
                DiceTypePickerHandle(
                    bottomInset = bottomInset,
                    expansionProgress = {
                        sheetState.expansionProgress(
                            layoutHeightPx = layoutHeightPx,
                            sheetHeightPx = contentHeightPx,
                            peekHeightPx = peekHeightPx
                        )
                    },
                    onClick = {
                        scope.launch {
                            if (sheetState.currentValue == SheetValue.Expanded) {
                                sheetState.partialExpand()
                            } else {
                                sheetState.expand()
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

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
                        scope.launch { sheetState.partialExpand() }
                    },
                    contentPadding = PaddingValues(
                        start = MaterialTheme.spacing.medium,
                        top = MaterialTheme.spacing.small,
                        end = MaterialTheme.spacing.medium,
                        bottom = MaterialTheme.spacing.medium + bottomInset
                    )
                )
            }
        },
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { layoutHeightPx = it.height.toFloat() },
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetDragHandle = null,
        containerColor = MaterialTheme.colorScheme.background,
        content = { content() }
    )
}

/**
 * Fraction of the way the sheet has travelled from its collapsed anchor to its expanded one, in
 * `0f..1f`. Returns `0f` until the sheet has been laid out and its offset is readable.
 */
@OptIn(ExperimentalMaterial3Api::class)
private fun SheetState.expansionProgress(
    layoutHeightPx: Float,
    sheetHeightPx: Float,
    peekHeightPx: Float
): Float {
    val collapsedOffset = layoutHeightPx - peekHeightPx
    val expandedOffset = max(layoutHeightPx - sheetHeightPx, 0f)
    val travel = collapsedOffset - expandedOffset
    val offset = runCatching { requireOffset() }.getOrNull()

    return if (travel <= 0f || offset == null) {
        0f
    } else {
        ((collapsedOffset - offset) / travel).coerceIn(0f, 1f)
    }
}

/**
 * Drag handle drawn as a single stroked path whose two arms flatten from a large upward chevron
 * into the Material 3 handle bar as [expansionProgress] rises from `0f` to `1f`. It sits above the
 * bottom safe-drawing inset, takes taps without an indication, and is dragged by the sheet it
 * heads.
 *
 * @param bottomInset Bottom safe-drawing inset the handle is lifted clear of.
 * @param expansionProgress How far the sheet has expanded, read while drawing.
 * @param onClick Callback when the handle is tapped.
 * @param modifier Modifier for the handle.
 */
@Composable
private fun DiceTypePickerHandle(
    bottomInset: Dp,
    expansionProgress: () -> Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = stringResource(R.string.cd_change_die_type)
    val handleColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = HANDLE_ALPHA)
    val path = remember { Path() }

    Canvas(
        modifier = modifier
            .width(HandleAreaWidth)
            .height(HandleAreaHeight + bottomInset)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .semantics { contentDescription = description }
            .padding(bottom = bottomInset)
    ) {
        val progress = expansionProgress()
        val halfWidth = lerp(ChevronHalfWidth, BarHalfWidth, progress).toPx()
        val rise = lerp(ChevronRise, 0.dp, progress).toPx()
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        path.reset()
        path.moveTo(centerX - halfWidth, centerY + rise / 2f)
        path.lineTo(centerX, centerY - rise / 2f)
        path.lineTo(centerX + halfWidth, centerY + rise / 2f)

        drawPath(
            path = path,
            color = handleColor,
            style = Stroke(
                width = HandleThickness.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
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
            content = { Box(modifier = Modifier.fillMaxSize()) }
        )
    }
}
