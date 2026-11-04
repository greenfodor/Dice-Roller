package com.greenfodor.diceroller

import androidx.compose.ui.window.ComposeUIViewController
import com.greenfodor.diceroller.ui.DiceRollerApp
import platform.UIKit.UIViewController

/** UIKit view controller showing [DiceRollerApp]. Koin has to be started with `initKoin` first. */
fun MainViewController(): UIViewController = ComposeUIViewController { DiceRollerApp() }
