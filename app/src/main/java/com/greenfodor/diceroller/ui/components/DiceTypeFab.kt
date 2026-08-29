package com.greenfodor.diceroller.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private val FabIconSize = 38.dp

/**
 * Extended floating action button that opens the die type picker and shows the active die.
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

    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier.semantics { contentDescription = description },
        icon = {
            Image(
                painter = painterResource(selectedDiceType.iconResId),
                contentDescription = null,
                modifier = Modifier.size(FabIconSize)
            )
        },
        text = { Text(text = stringResource(selectedDiceType.labelResId)) }
    )
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
