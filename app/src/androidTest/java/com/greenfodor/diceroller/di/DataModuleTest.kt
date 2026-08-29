package com.greenfodor.diceroller.di

import com.greenfodor.diceroller.data.DataStoreSettingsRepository
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.history.RoomRollHistoryRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import javax.inject.Inject

@HiltAndroidTest
class DataModuleTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var rollHistoryRepository: RollHistoryRepository

    @Inject
    lateinit var clock: Clock

    @Inject
    lateinit var secondSettingsRepository: SettingsRepository

    @Inject
    lateinit var secondRollHistoryRepository: RollHistoryRepository

    @Before
    fun inject() {
        hiltRule.inject()
    }

    @Test
    fun settingsRepositoryResolvesToTheDataStoreImplementation() {
        assertTrue(settingsRepository is DataStoreSettingsRepository)
    }

    @Test
    fun rollHistoryRepositoryResolvesToTheRoomImplementation() {
        assertTrue(rollHistoryRepository is RoomRollHistoryRepository)
    }

    @Test
    fun aClockIsAvailableForInjection() {
        assertTrue(clock.millis() > 0L)
    }

    @Test
    fun bothRepositoriesAreSingletons() {
        assertSame(settingsRepository, secondSettingsRepository)
        assertSame(rollHistoryRepository, secondRollHistoryRepository)
    }
}
