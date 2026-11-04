package com.greenfodor.diceroller

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.di.presentationModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTestRule
import java.time.Clock
import java.time.ZoneId

/** The application context of the app under test. */
val targetApplicationContext: Context
    get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext

/**
 * Stands in for the `coreDataModule` with [rollHistoryRepository] and [settingsRepository], the
 * device clock and time zone, and a process-wide write scope.
 */
fun fakeDataModule(
    rollHistoryRepository: RollHistoryRepository,
    settingsRepository: SettingsRepository
): Module = module {
    single { rollHistoryRepository }
    single { settingsRepository }
    single<Clock> { Clock.systemDefaultZone() }
    factory<ZoneId> { ZoneId.systemDefault() }
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
}

/**
 * Starts Koin before each test with every presentation module and [fakeDataModule] in place of
 * the persistence layer, and stops it after the test.
 */
fun fakeDataKoinTestRule(
    rollHistoryRepository: RollHistoryRepository,
    settingsRepository: SettingsRepository
): KoinTestRule = KoinTestRule.create {
    androidContext(targetApplicationContext)
    modules(listOf(fakeDataModule(rollHistoryRepository, settingsRepository)) + presentationModules)
}
