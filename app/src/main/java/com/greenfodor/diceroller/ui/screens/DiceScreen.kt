package com.greenfodor.diceroller.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.dice.DieState
import com.greenfodor.diceroller.ui.theme.diceSpecs
import com.greenfodor.diceroller.ui.theme.spacing
import com.greenfodor.diceroller.ui.utils.LocalHapticsEnabled
import com.greenfodor.diceroller.ui.utils.LocalShakeToRollEnabled
import com.greenfodor.diceroller.ui.utils.rememberShakeDetector
import com.greenfodor.diceroller.ui.utils.rollDice
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Shared layout for every dice screen: shows the rolled value, wires the shake
 * detector, lays the dice out (centered for one, evenly spread for several), and
 * renders the roll button.
 *
 * The result sits above the dice. It scales + fades out the instant a roll starts,
 * stays hidden through the roll, then pops back in (bouncy spring, overshooting past
 * full size) timed to land just before the dice settle — so the outcome is not
 * lingering on screen during the roll.
 *
 * Each die is rendered through the [dieContent] slot, so a screen only has to
 * supply its own renderer. The roll button is disabled while any die is mid-roll.
 *
 * A roll is reported through [onRollSettled] once every die has finished animating, as a
 * single [RollOutcome] carrying [dieLabel], each die's face value, the screen's scored
 * [result] and the time the roll was started — so a screen rolling several dice at once (2d6,
 * d100) reports one outcome, not one per die. Only a roll that settles on screen is reported:
 * one interrupted first — by switching die type, leaving the screen, a configuration change or
 * process death — is dropped.
 *
 * `rollStartedAtMillis` is non-null only while a roll is in flight, holding the time it started.
 *
 * @param dieStates The dice shown on this screen (one or more).
 * @param dieLabel One of the [com.greenfodor.diceroller.data.DieLabels] constants.
 * @param rollButtonResId Label for the roll button.
 * @param onRollSettled Called once per roll, after the dice settle.
 * @param result The rolled value to display, derived from the dice. Defaults to the
 *   sum of every die's current face — correct for a single die (the value itself) and
 *   for multiple dice (e.g. 2d6). Screens with their own scoring (e.g. percentile d100)
 *   override this.
 * @param dieContent Renderer for a single die — typically a `RollingDNAnimation`.
 */
@Composable
fun DiceScreen(
    dieStates: List<DieState>,
    dieLabel: String,
    @StringRes rollButtonResId: Int,
    modifier: Modifier = Modifier,
    onRollSettled: (RollOutcome) -> Unit = {},
    result: (List<DieState>) -> Int = { states -> states.sumOf { it.currentFace.value } },
    dieContent: @Composable (DieState) -> Unit
) {
    val context = LocalContext.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val shakeToRollEnabled = LocalShakeToRollEnabled.current

    var rollStartedAtMillis by remember { mutableStateOf<Long?>(null) }
    val startRoll = {
        if (context.rollDice(dieStates, hapticsEnabled)) rollStartedAtMillis = System.currentTimeMillis()
    }

    rememberShakeDetector(
        enabled = shakeToRollEnabled,
        onShake = startRoll
    )

    val isRolling = dieStates.any { it.isRolling }

    val currentOnRollSettled by rememberUpdatedState(onRollSettled)
    LaunchedEffect(isRolling, rollStartedAtMillis) {
        val startedAt = rollStartedAtMillis?.takeIf { isRolling.not() } ?: return@LaunchedEffect
        rollStartedAtMillis = null
        currentOnRollSettled(
            RollOutcome(
                dieLabel = dieLabel,
                values = dieStates.map { it.currentFace.value },
                total = result(dieStates),
                startedAtMillis = startedAt
            )
        )
    }
    val rollDurationMillis = MaterialTheme.diceSpecs.rollDurationMillis

    // Drives both alpha and scale of the result: 0 = hidden/small, 1 = shown/full.
    val visibility = remember { Animatable(1f) }
    var displayedValue by remember { mutableIntStateOf(result(dieStates)) }

    // The outcome is decided the instant a roll starts, so we can time the reveal to land
    // just before the dice settle: scale + fade the old value out, hold hidden, then pop the
    // new value in with a bouncy spring (overshoots past full size for a snappy "pop").
    LaunchedEffect(isRolling) {
        if (isRolling) {
            visibility.animateTo(0f, tween(DiceConstants.RESULT_EXIT_MILLIS, easing = FastOutSlowInEasing))
            val holdMillis = rollDurationMillis - DiceConstants.RESULT_EXIT_MILLIS -
                DiceConstants.RESULT_ENTER_LEAD_MILLIS
            delay(holdMillis.coerceAtLeast(0).milliseconds)
            displayedValue = result(dieStates)
            visibility.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            // Guarantee a fully-shown final state even if the settle pre-empts the entrance.
            visibility.snapTo(1f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val rolledDescription = stringResource(R.string.cd_rolled_value, displayedValue)
        Text(
            text = displayedValue.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .graphicsLayer {
                    val shown = visibility.value
                    // Alpha stays in [0,1]; scale follows the raw spring so it can overshoot past
                    // full size for the pop.
                    alpha = shown.coerceIn(0f, 1f)
                    val scale = DiceConstants.RESULT_HIDDEN_SCALE +
                        (1f - DiceConstants.RESULT_HIDDEN_SCALE) * shown
                    scaleX = scale
                    scaleY = scale
                }
                .semantics {
                    contentDescription = rolledDescription
                    liveRegion = LiveRegionMode.Polite
                }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (dieStates.size > 1) Arrangement.SpaceEvenly else Arrangement.Center
        ) {
            dieStates.forEach { dieState ->
                val description = stringResource(R.string.cd_die_value, dieState.currentFace.value)
                Box(
                    modifier = Modifier.semantics {
                        contentDescription = description
                    }
                ) {
                    dieContent(dieState)
                }
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        Button(
            onClick = startRoll,
            enabled = isRolling.not()
        ) {
            Text(text = stringResource(rollButtonResId))
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
    }
}
