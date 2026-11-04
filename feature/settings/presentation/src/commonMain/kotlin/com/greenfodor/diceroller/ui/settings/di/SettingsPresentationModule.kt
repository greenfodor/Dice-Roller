package com.greenfodor.diceroller.ui.settings.di

import com.greenfodor.diceroller.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Provides the settings ViewModels. */
val settingsPresentationModule = module {
    viewModelOf(::SettingsViewModel)
}
