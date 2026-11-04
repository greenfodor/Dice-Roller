package com.greenfodor.diceroller.ui

import androidx.navigation3.runtime.NavKey
import com.greenfodor.diceroller.ui.history.RollHistoryRoute
import com.greenfodor.diceroller.ui.screens.DiceRoute
import com.greenfodor.diceroller.ui.settings.DiceColorsRoute
import com.greenfodor.diceroller.ui.settings.SettingsRoute
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** Every [NavKey] the app puts on its back stack. */
internal val allNavKeys: List<NavKey> = listOf(DiceRoute, SettingsRoute, DiceColorsRoute, RollHistoryRoute)

class NavSavedStateTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun everyNavKeyHasAPolymorphicSerializer() {
        val module = navSavedStateConfiguration.serializersModule

        allNavKeys.forEach { key ->
            val serializer = assertNotNull(module.getPolymorphic(NavKey::class, key), "$key")
            val bySerialName = module.getPolymorphic(NavKey::class, serializer.descriptor.serialName)
            assertEquals(serializer.descriptor, bySerialName?.descriptor, "$key")
        }
    }
}
