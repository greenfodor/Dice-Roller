package com.greenfodor.diceroller.ui.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.data.RollHistoryDay
import com.greenfodor.diceroller.data.RollHistorySection
import com.greenfodor.diceroller.data.RollRecord
import com.greenfodor.diceroller.feature.history.presentation.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing

private val DialogShape = RoundedCornerShape(28.dp)
private val LoadingBoxHeight = 96.dp

/** Share of the window height the roll list may occupy before it starts scrolling. */
private const val LIST_HEIGHT_FRACTION = 0.6f

/** Share of the window width the pop-up occupies, within [DialogMinWidth]..[DialogMaxWidth]. */
private const val DIALOG_WIDTH_FRACTION = 0.72f
private val DialogMinWidth = 280.dp
private val DialogMaxWidth = 360.dp

/** Used when the window size is not reported, as in a wrap-content preview. */
private val FallbackListMaxHeight = 360.dp

/**
 * Body of the roll history pop-up: every recorded roll, newest first, under a sticky header per
 * calendar day. Shows a spinner while the history is still loading and a short message when it
 * holds no rolls.
 *
 * Every state is laid out at the same width, so the pop-up does not resize as the history fills
 * up or is cleared. Height is content-sized: a short history wraps, while a long one scrolls
 * inside a list capped at [LIST_HEIGHT_FRACTION] of the window height, so the title and the close
 * button stay on screen whatever the display size.
 *
 * Rendered inside the dialog window supplied by the `rollHistoryGraph` destination, which is why
 * this composable holds no `Dialog` of its own and previews normally.
 */
@Composable
fun RollHistoryContent(
    state: RollHistoryUiState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(rollHistoryDialogWidth()),
        shape = DialogShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
            Text(
                text = stringResource(R.string.roll_history_title),
                style = MaterialTheme.typography.headlineSmall
            )

            when (state) {
                RollHistoryUiState.Loading -> RollHistoryLoading()
                RollHistoryUiState.Empty -> RollHistoryEmpty()
                is RollHistoryUiState.Content -> RollHistoryList(sections = state.sections)
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(R.string.roll_history_close))
            }
        }
    }
}

@Composable
private fun RollHistoryLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = LoadingBoxHeight),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RollHistoryEmpty(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.roll_history_empty),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = MaterialTheme.spacing.large)
    )
}

/**
 * [DIALOG_WIDTH_FRACTION] of the window width, clamped to [DialogMinWidth]..[DialogMaxWidth] and
 * never wider than the window itself. Applied in every state so the pop-up keeps one width.
 */
@Composable
private fun rollHistoryDialogWidth(): Dp {
    val windowWidthPx = LocalWindowInfo.current.containerSize.width
    if (windowWidthPx <= 0) return DialogMaxWidth
    val available = with(LocalDensity.current) { windowWidthPx.toDp() }
    return (available * DIALOG_WIDTH_FRACTION)
        .coerceIn(DialogMinWidth, DialogMaxWidth)
        .coerceAtMost(available)
}

/** [LIST_HEIGHT_FRACTION] of the window height, or [FallbackListMaxHeight] if it is unreported. */
@Composable
private fun rollHistoryListMaxHeight(): Dp {
    val windowHeightPx = LocalWindowInfo.current.containerSize.height
    if (windowHeightPx <= 0) return FallbackListMaxHeight
    return with(LocalDensity.current) { (windowHeightPx * LIST_HEIGHT_FRACTION).toDp() }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RollHistoryList(
    sections: List<RollHistorySection>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.heightIn(max = rollHistoryListMaxHeight()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
    ) {
        sections.forEach { section ->
            stickyHeader(key = section.day.toString()) {
                RollHistoryDayHeader(day = section.day)
            }
            items(items = section.rolls, key = { it.id }) { record ->
                RollHistoryRow(record = record)
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun RollHistoryContentPreview() {
    DiceRollerTheme {
        RollHistoryContent(
            state = RollHistoryUiState.Content(
                sections = listOf(
                    RollHistorySection(
                        day = RollHistoryDay.Today,
                        rolls = listOf(
                            RollRecord(
                                id = 3,
                                dieLabel = "2d6",
                                values = listOf(4, 6),
                                total = 10,
                                timestampMillis = PREVIEW_TIMESTAMP
                            ),
                            RollRecord(
                                id = 2,
                                dieLabel = "d20",
                                values = listOf(20),
                                total = 20,
                                timestampMillis = PREVIEW_TIMESTAMP
                            )
                        )
                    ),
                    RollHistorySection(
                        day = RollHistoryDay.Yesterday,
                        rolls = listOf(
                            RollRecord(
                                id = 1,
                                dieLabel = "d100",
                                values = listOf(10, 2),
                                total = 12,
                                timestampMillis = PREVIEW_TIMESTAMP
                            )
                        )
                    )
                )
            ),
            onDismiss = {}
        )
    }
}

@LightDarkPreview
@Composable
private fun RollHistoryContentEmptyPreview() {
    DiceRollerTheme {
        RollHistoryContent(state = RollHistoryUiState.Empty, onDismiss = {})
    }
}

@LightDarkPreview
@Composable
private fun RollHistoryContentLoadingPreview() {
    DiceRollerTheme {
        RollHistoryContent(state = RollHistoryUiState.Loading, onDismiss = {})
    }
}

private const val PREVIEW_TIMESTAMP = 1_787_000_000_000L
