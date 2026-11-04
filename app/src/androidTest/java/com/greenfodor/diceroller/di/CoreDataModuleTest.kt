package com.greenfodor.diceroller.di

import com.greenfodor.diceroller.data.DataStoreSettingsRepository
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.di.coreDataModule
import com.greenfodor.diceroller.data.history.RoomRollHistoryRepository
import com.greenfodor.diceroller.targetApplicationContext
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import kotlin.time.Clock

class CoreDataModuleTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(targetApplicationContext)
        modules(coreDataModule)
    }

    @Test
    fun settingsRepositoryResolvesToTheDataStoreImplementation() {
        assertTrue(get<SettingsRepository>() is DataStoreSettingsRepository)
    }

    @Test
    fun rollHistoryRepositoryResolvesToTheRoomImplementation() {
        assertTrue(get<RollHistoryRepository>() is RoomRollHistoryRepository)
    }

    @Test
    fun aClockIsAvailableForInjection() {
        assertTrue(get<Clock>().now().toEpochMilliseconds() > 0L)
    }

    @Test
    fun bothRepositoriesAreSingletons() {
        assertSame(get<SettingsRepository>(), get<SettingsRepository>())
        assertSame(get<RollHistoryRepository>(), get<RollHistoryRepository>())
    }
}
