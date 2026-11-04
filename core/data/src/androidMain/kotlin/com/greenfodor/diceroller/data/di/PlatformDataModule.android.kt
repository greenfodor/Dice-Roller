package com.greenfodor.diceroller.data.di

import com.greenfodor.diceroller.data.history.buildRollHistoryDatabase
import com.greenfodor.diceroller.data.history.rollHistoryDatabaseBuilder
import com.greenfodor.diceroller.data.settingsDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single { settingsDataStore(androidContext()) }
    single { buildRollHistoryDatabase(rollHistoryDatabaseBuilder(androidContext())) }
}
