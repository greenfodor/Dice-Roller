package com.greenfodor.diceroller.di

import com.greenfodor.diceroller.data.di.coreDataModule
import com.greenfodor.diceroller.ui.history.di.historyPresentationModule
import com.greenfodor.diceroller.ui.settings.di.settingsPresentationModule
import org.koin.core.module.Module

/** Every module that provides a ViewModel. */
val presentationModules: List<Module> = listOf(
    settingsPresentationModule,
    historyPresentationModule,
    diceModule
)

/** Every module the app starts Koin with. */
val appModules: List<Module> = listOf(coreDataModule) + presentationModules
