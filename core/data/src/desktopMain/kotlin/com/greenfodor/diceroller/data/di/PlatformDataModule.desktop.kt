package com.greenfodor.diceroller.data.di

import com.greenfodor.diceroller.data.SETTINGS_DATASTORE_FILE_NAME
import com.greenfodor.diceroller.data.appDataDirectory
import com.greenfodor.diceroller.data.createSettingsDataStore
import com.greenfodor.diceroller.data.history.buildRollHistoryDatabase
import com.greenfodor.diceroller.data.history.rollHistoryDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single {
        createSettingsDataStore { appDataDirectory().resolve(SETTINGS_DATASTORE_FILE_NAME).absolutePath }
    }
    single { buildRollHistoryDatabase(rollHistoryDatabaseBuilder()) }
}
