package com.greenfodor.diceroller.data.di

import com.greenfodor.diceroller.data.DataStoreSettingsRepository
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.history.RollHistoryDatabase
import com.greenfodor.diceroller.data.history.RoomRollHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.datetime.TimeZone
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Clock

/**
 * Binds the persistence layer. The DataStore and the Room database are singletons, each holding
 * a single open handle to their file for the life of the process.
 *
 * - [Clock]: the clock every timestamp and day grouping is measured against; swapped in tests.
 * - [TimeZone]: the device's current time zone, re-read on every injection so a zone change is
 *   picked up without restarting the process.
 * - [CoroutineScope]: scope for persistence writes that must run to completion. It lives for the
 *   whole process and a failed write does not cancel the ones that follow.
 */
val coreDataModule = module {
    single<SettingsRepository> { DataStoreSettingsRepository.create(androidContext()) }
    single { RollHistoryDatabase.create(androidContext()) }
    single { get<RollHistoryDatabase>().rollHistoryDao() }
    singleOf(::RoomRollHistoryRepository) { bind<RollHistoryRepository>() }
    single<Clock> { Clock.System }
    factory<TimeZone> { TimeZone.currentSystemDefault() }
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
}
