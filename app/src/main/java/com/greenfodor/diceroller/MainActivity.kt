package com.greenfodor.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.greenfodor.diceroller.ui.components.DiceRollerTopBar
import com.greenfodor.diceroller.ui.screens.D20Screen
import com.greenfodor.diceroller.ui.screens.D4Screen
import com.greenfodor.diceroller.ui.screens.D6Screen
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.screens.DoubleD6Screen
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemInDarkTheme) }
            var selectedDiceType by remember { mutableStateOf(DiceType.SINGLE_D6) }

            DiceRollerTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        DiceRollerTopBar(
                            selectedDiceType = selectedDiceType,
                            onDiceTypeSelected = { selectedDiceType = it },
                            isDarkMode = isDarkMode,
                            onToggleTheme = { isDarkMode = isDarkMode.not() }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedDiceType) {
                            DiceType.SINGLE_D4 -> D4Screen()
                            DiceType.SINGLE_D6 -> D6Screen()
                            DiceType.DOUBLE_D6 -> DoubleD6Screen()
                            DiceType.SINGLE_D20 -> D20Screen()
                        }
                    }
                }
            }
        }
    }
}
