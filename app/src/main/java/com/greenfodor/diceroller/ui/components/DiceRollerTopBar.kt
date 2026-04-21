package com.greenfodor.diceroller.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.greenfodor.diceroller.DiceType
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.DiceConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerTopBar(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(value = false) }

    val rotation by animateFloatAsState(
        targetValue = if (isDarkMode) 180f else 0f,
        animationSpec = tween(durationMillis = DiceConstants.ICON_ROTATION_DURATION_MILLIS),
        label = "iconRotation"
    )

    TopAppBar(
        modifier = modifier,
        title = { },
        navigationIcon = {
            Box {
                TextButton(onClick = { isMenuExpanded = true }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (selectedDiceType) {
                                DiceType.SINGLE_D6 -> stringResource(R.string.d6_label)
                                DiceType.DOUBLE_D6 -> stringResource(R.string.double_d6_label)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.d6_label)) },
                        onClick = {
                            onDiceTypeSelected(DiceType.SINGLE_D6)
                            isMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.double_d6_label)) },
                        onClick = {
                            onDiceTypeSelected(DiceType.DOUBLE_D6)
                            isMenuExpanded = false
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        actions = {
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    )
}
