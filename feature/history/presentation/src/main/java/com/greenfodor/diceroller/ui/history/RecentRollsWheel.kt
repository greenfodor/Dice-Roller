package com.greenfodor.diceroller.ui.history

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/** Height of one wheel slot; the wheel is [RECENT_ROLLS_COUNT] slots tall. */
private val SlotHeight = 40.dp

/** How long an entry takes to travel one slot. */
private const val SLOT_TRAVEL_MILLIS = 350

/** How long a newly added entry takes to reach full size and opacity. */
private const val ENTER_MILLIS = 350

/** Separates the die notation, the total and the breakdown when a line is read as one string. */
private const val PART_SEPARATOR = "  "

/** The size a newly added entry grows from. */
private const val ENTER_START_SCALE = 0.6f

/** How much smaller each slot above the bottom one renders. */
private const val SLOT_SCALE_STEP = 0.15f

/**
 * The newest rolls stacked as a wheel: the newest entry sits at the bottom, full size and fully
 * opaque, with the entries above it progressively smaller and more transparent. Each line reads
 * as its die notation, then the scored total in bold, then the individual values of a multi-die
 * roll.
 *
 * A new roll scales up and fades in on the bottom slot while the entries already on the wheel
 * travel up one slot; the entry pushed off the top travels into the slot above the wheel, fading
 * to fully transparent as it leaves, and is dropped from composition once it arrives.
 *
 * @param rolls The rolls to show, oldest first and newest last, at most [RECENT_ROLLS_COUNT].
 * @param modifier Modifier for the wheel.
 */
@Composable
fun RecentRollsWheel(
    rolls: List<RecentRollUiModel>,
    modifier: Modifier = Modifier
) {
    var rendered by remember { mutableStateOf(rolls) }

    LaunchedEffect(rolls) {
        rendered = (rendered + rolls).distinctBy { it.id }
        delay(SLOT_TRAVEL_MILLIS.milliseconds)
        rendered = rolls
    }

    Box(
        modifier = modifier
            .height(SlotHeight * RECENT_ROLLS_COUNT)
            .clipToBounds(),
        contentAlignment = Alignment.BottomCenter
    ) {
        rendered.forEach { entry ->
            key(entry.id) {
                val slot = rememberSlotPosition(target = targetSlot(entry = entry, rolls = rolls))
                val enter = rememberEnterProgress()
                RecentRollLine(
                    entry = entry,
                    modifier = Modifier.graphicsLayer {
                        val position = slot.value
                        val entered = enter.value
                        translationY = -position * SlotHeight.toPx()
                        alpha = slotAlpha(position) * entered
                        val scale = slotScale(position) * lerp(ENTER_START_SCALE, 1f, entered)
                        scaleX = scale
                        scaleY = scale
                    }
                )
            }
        }
    }
}

/**
 * One wheel line, styled part by part: the die notation quiet, the total large and bold in the
 * primary color, and the breakdown of a multi-die roll small and quiet again. The parts are laid
 * out as a row and centred on each other, so a part reads level with the total whatever its size.
 * The row carries the whole line as its semantics text.
 */
@Composable
private fun RecentRollLine(
    entry: RecentRollUiModel,
    modifier: Modifier = Modifier
) {
    val line = remember(entry) {
        listOfNotNull(entry.dieLabel, entry.total, entry.breakdown).joinToString(PART_SEPARATOR)
    }
    Row(
        modifier = modifier.clearAndSetSemantics { text = AnnotatedString(line) },
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.dieLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = entry.total,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )
        entry.breakdown?.let { breakdown ->
            Text(
                text = breakdown,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

/**
 * Animates an entry towards [target], counted in slots up from the bottom of the wheel. A newly
 * composed entry starts on the slot it belongs to, so it appears in place rather than travelling
 * in from outside the wheel.
 */
@Composable
private fun rememberSlotPosition(target: Float): State<Float> {
    val position = remember { Animatable(target) }
    LaunchedEffect(target) {
        position.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = SLOT_TRAVEL_MILLIS, easing = FastOutSlowInEasing)
        )
    }
    return position.asState()
}

/** Runs `0`..`1` once, when an entry is first composed, scaling and fading it into the wheel. */
@Composable
private fun rememberEnterProgress(): State<Float> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = ENTER_MILLIS, easing = FastOutSlowInEasing)
        )
    }
    return progress.asState()
}

/**
 * The slot [entry] belongs in, counted up from the bottom of the wheel. An entry no longer in
 * [rolls] targets the slot just above the wheel, where it is invisible.
 */
private fun targetSlot(entry: RecentRollUiModel, rolls: List<RecentRollUiModel>): Float {
    val index = rolls.indexOfFirst { it.id == entry.id }
    return if (index < 0) RECENT_ROLLS_COUNT.toFloat() else (rolls.lastIndex - index).toFloat()
}

/** Fully opaque in the bottom slot, fading to fully transparent in the slot above the wheel. */
private fun slotAlpha(slot: Float): Float = (1f - slot / RECENT_ROLLS_COUNT).coerceIn(0f, 1f)

/** Full size in the bottom slot, shrinking by [SLOT_SCALE_STEP] per slot above it. */
private fun slotScale(slot: Float): Float = (1f - slot * SLOT_SCALE_STEP).coerceIn(0f, 1f)

@LightDarkPreview
@Composable
private fun RecentRollsWheelPreview() {
    DiceRollerTheme {
        RecentRollsWheel(
            rolls = listOf(
                RecentRollUiModel(id = 1, dieLabel = "d100", total = "100", breakdown = "(00 + 0)"),
                RecentRollUiModel(id = 2, dieLabel = "2d6", total = "7", breakdown = "(3 + 4)"),
                RecentRollUiModel(id = 3, dieLabel = "d20", total = "20")
            )
        )
    }
}
