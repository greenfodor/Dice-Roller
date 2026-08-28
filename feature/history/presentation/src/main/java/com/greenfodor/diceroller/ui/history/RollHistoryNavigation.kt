package com.greenfodor.diceroller.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import kotlinx.serialization.Serializable

/** The roll history pop-up, shown over whichever screen is underneath it. */
@Serializable
data object RollHistoryRoute : NavKey

/**
 * Adds the roll history entry.
 *
 * It is declared as a dialog scene, so it renders over the entry beneath it and its
 * [RollHistoryViewModel] is scoped to this entry: created when the history opens and cleared
 * when it closes. `usePlatformDefaultWidth = false` hands the width to [RollHistoryContent],
 * which sizes every state alike instead of taking the full platform dialog width.
 */
fun EntryProviderScope<NavKey>.rollHistoryEntry(onDismiss: () -> Unit) {
    entry<RollHistoryRoute>(
        metadata = DialogSceneStrategy.dialog(
            DialogProperties(usePlatformDefaultWidth = false)
        )
    ) {
        val viewModel: RollHistoryViewModel = hiltViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        RollHistoryContent(state = state, onDismiss = onDismiss)
    }
}
