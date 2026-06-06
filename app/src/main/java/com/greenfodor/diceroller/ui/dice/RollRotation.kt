package com.greenfodor.diceroller.ui.dice

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import com.greenfodor.diceroller.ui.theme.diceSpecs

/**
 * The three animated rotation axes for a rolling die.
 *
 * Each value is exposed as a [State] so callers read it **inside the draw
 * lambda** (a deferred read). That keeps the per-frame animation off the
 * composition path — the composable does not recompose on every frame.
 */
@Stable
class RollRotation(
    val x: State<Float>,
    val y: State<Float>,
    val z: State<Float>
)

/**
 * Drives the rolling animation for [dieState] and returns the animated rotations.
 *
 * The X axis carries a `finishedListener` that flips [DieState.isRolling] back
 * off once the roll settles; X always changes on a roll (full spins are added),
 * so it is a reliable signal that the whole animation has finished. No animated
 * value is read in composition scope here, so neither the rolling flag nor the
 * frame updates trigger recomposition.
 */
@Composable
fun rememberRollRotation(dieState: DieState): RollRotation {
    val durationMillis = MaterialTheme.diceSpecs.rollDurationMillis
    val spec = remember(durationMillis) {
        tween<Float>(durationMillis = durationMillis, easing = FastOutSlowInEasing)
    }

    val x = animateFloatAsState(
        targetValue = dieState.targetRotationX,
        animationSpec = spec,
        finishedListener = { dieState.onSettled() },
        label = "rotationX"
    )
    val y = animateFloatAsState(
        targetValue = dieState.targetRotationY,
        animationSpec = spec,
        label = "rotationY"
    )
    val z = animateFloatAsState(
        targetValue = dieState.targetRotationZ,
        animationSpec = spec,
        label = "rotationZ"
    )

    return remember { RollRotation(x, y, z) }
}
