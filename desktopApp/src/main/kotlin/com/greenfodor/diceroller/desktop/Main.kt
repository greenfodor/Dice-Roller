package com.greenfodor.diceroller.desktop

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.greenfodor.diceroller.desktop.generated.resources.Res
import com.greenfodor.diceroller.desktop.generated.resources.ic_launcher
import com.greenfodor.diceroller.di.initKoin
import com.greenfodor.diceroller.ui.DiceRollerApp
import org.jetbrains.compose.resources.painterResource

/** Starts Koin and opens the Dice Roller window. */
fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = WindowPosition(Alignment.Center),
                width = 1100.dp,
                height = 720.dp
            ),
            title = "Dice Roller",
            icon = painterResource(Res.drawable.ic_launcher)
        ) {
            DiceRollerApp()
        }
    }
}
