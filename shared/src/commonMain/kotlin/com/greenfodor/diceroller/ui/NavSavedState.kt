package com.greenfodor.diceroller.ui

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.greenfodor.diceroller.ui.history.RollHistoryRoute
import com.greenfodor.diceroller.ui.screens.DiceRoute
import com.greenfodor.diceroller.ui.settings.DiceColorsRoute
import com.greenfodor.diceroller.ui.settings.SettingsRoute
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/** Saved-state configuration of the back stack, registering every [NavKey] the app navigates to. */
val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(DiceRoute.serializer())
            subclass(SettingsRoute.serializer())
            subclass(DiceColorsRoute.serializer())
            subclass(RollHistoryRoute.serializer())
        }
    }
}
