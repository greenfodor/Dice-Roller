package com.greenfodor.diceroller.ui.history

import com.greenfodor.diceroller.data.RollHistorySection

/** Everything [RollHistoryDialog] renders, in the three states the history can be in. */
sealed interface RollHistoryUiState {
    /** The persisted history has not been read yet. */
    data object Loading : RollHistoryUiState

    /** The history was read and holds no rolls. */
    data object Empty : RollHistoryUiState

    /** The history was read and holds [sections], newest day first. */
    data class Content(
        val sections: List<RollHistorySection>
    ) : RollHistoryUiState
}
