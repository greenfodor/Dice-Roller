package com.greenfodor.diceroller.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing

private val FabSize = 96.dp
private val FabIconSize = 48.dp

/**
 * Square floating action button that opens the die type picker and shows the active die as the
 * icon-over-label tile the picker itself uses, one size down.
 *
 * @param selectedDiceType The currently active die type, rendered as icon and label.
 * @param onClick Callback when the button is pressed.
 * @param modifier Modifier for the button.
 */
@Composable
fun DiceTypeFab(
    selectedDiceType: DiceType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = stringResource(R.string.cd_change_die_type)

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(FabSize)
            .semantics { contentDescription = description },
        shape = DiceTileShape
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(selectedDiceType.iconResId),
                contentDescription = null,
                modifier = Modifier.size(FabIconSize)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))

            Text(
                text = stringResource(selectedDiceType.labelResId),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun DiceTypeFabPreview() {
    DiceRollerTheme {
        DiceTypeFab(
            selectedDiceType = DiceType.SINGLE_D20,
            onClick = {}
        )
    }
}
