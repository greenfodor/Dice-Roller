package com.greenfodor.diceroller.ui

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class NavSavedStateRoundTripTest {
    private val backStackSerializer = ListSerializer(PolymorphicSerializer(NavKey::class))

    @Test
    fun aBackStackHoldingEveryNavKeySurvivesSavedState() {
        val savedState = encodeToSavedState(backStackSerializer, allNavKeys, navSavedStateConfiguration)

        val restored = decodeFromSavedState(backStackSerializer, savedState, navSavedStateConfiguration)

        assertEquals(allNavKeys, restored)
    }
}
