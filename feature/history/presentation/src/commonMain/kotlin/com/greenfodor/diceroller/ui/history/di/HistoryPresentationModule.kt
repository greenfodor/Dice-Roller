package com.greenfodor.diceroller.ui.history.di

import com.greenfodor.diceroller.ui.history.RecentRollsViewModel
import com.greenfodor.diceroller.ui.history.RollHistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Provides the roll history ViewModels. */
val historyPresentationModule = module {
    viewModelOf(::RollHistoryViewModel)
    viewModelOf(::RecentRollsViewModel)
}
