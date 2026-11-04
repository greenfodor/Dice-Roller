package com.greenfodor.diceroller.di

import com.greenfodor.diceroller.ui.screens.DiceViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Provides the dice screen ViewModels. */
val diceModule = module {
    viewModelOf(::DiceViewModel)
}
