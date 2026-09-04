package com.greenfodor.diceroller.ui.screens

import androidx.annotation.StringRes
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.ui.dice.DieState
import com.greenfodor.diceroller.ui.theme.spacing
import com.greenfodor.diceroller.ui.utils.LocalHapticsEnabled
import com.greenfodor.diceroller.ui.utils.LocalShakeToRollEnabled
import com.greenfodor.diceroller.ui.utils.rememberShakeDetector
import com.greenfodor.diceroller.ui.utils.rollDice

/**
 * Shared layout for every dice screen: wires the shake detector, lays the dice out as one
 * centered group with a fixed gap between them, and renders the roll button.
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
 * @param result The scored outcome of the roll, derived from the dice. Defaults to the
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large, Alignment.CenterHorizontally)
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
