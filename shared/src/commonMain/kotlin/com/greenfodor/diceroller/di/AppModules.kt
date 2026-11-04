package com.greenfodor.diceroller.di

import com.greenfodor.diceroller.data.di.coreDataModule
import com.greenfodor.diceroller.ui.history.di.historyPresentationModule
import com.greenfodor.diceroller.ui.settings.di.settingsPresentationModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/** Every module that provides a ViewModel. */
val presentationModules: List<Module> = listOf(
    settingsPresentationModule,
    historyPresentationModule,
    diceModule
)

/** Every module the app starts Koin with. */
val appModules: List<Module> = listOf(coreDataModule) + presentationModules

/**
 * Starts Koin with [appModules]. [config] is applied first, e.g. to hand Koin the Android
 * context.
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules)
    }
}
