package com.greenfodor.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.greenfodor.diceroller.ui.DiceRollerApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var uiReady = false
        splashScreen.setKeepOnScreenCondition { uiReady.not() }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceRollerApp(
                windowSizeClass = calculateWindowSizeClass(this),
                onReady = { uiReady = true }
            )
        }
    }
}
