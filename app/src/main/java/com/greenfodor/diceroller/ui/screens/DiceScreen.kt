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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.dice.DieState
import com.greenfodor.diceroller.ui.theme.spacing
import com.greenfodor.diceroller.ui.utils.rememberShakeDetector
import com.greenfodor.diceroller.ui.utils.rollDice

/**
 * Shared layout for every dice screen: wires the shake detector, lays the dice
 * out (centered for one, evenly spread for several), and renders the roll button.
 *
 * Each die is rendered through the [dieContent] slot, so a screen only has to
 * supply its own renderer. The roll button is disabled while any die is mid-roll.
 *
 * @param dieStates The dice shown on this screen (one or more).
 * @param rollButtonResId Label for the roll button.
 * @param dieContent Renderer for a single die — typically a `RollingDNAnimation`.
 */
@Composable
fun DiceScreen(
    dieStates: List<DieState>,
    @StringRes rollButtonResId: Int,
    modifier: Modifier = Modifier,
    dieContent: @Composable (DieState) -> Unit
) {
    val context = LocalContext.current

    rememberShakeDetector(onShake = { context.rollDice(dieStates) })

    val isRolling = dieStates.any { it.isRolling }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (dieStates.size > 1) Arrangement.SpaceEvenly else Arrangement.Center
        ) {
            dieStates.forEach { dieState ->
                val description = stringResource(R.string.cd_die_value, dieState.currentFace.value)
                Box(
                    modifier = Modifier.semantics {
                        contentDescription = description
                        liveRegion = LiveRegionMode.Polite
                    }
                ) {
                    dieContent(dieState)
                }
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        Button(
            onClick = { context.rollDice(dieStates) },
            enabled = isRolling.not()
        ) {
            Text(text = stringResource(rollButtonResId))
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
    }
}
