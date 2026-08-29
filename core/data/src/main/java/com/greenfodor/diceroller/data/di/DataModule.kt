package com.greenfodor.diceroller.data.di

import android.content.Context
import com.greenfodor.diceroller.data.DataStoreSettingsRepository
import com.greenfodor.diceroller.data.RollHistoryRepository
import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.history.RollHistoryDao
import com.greenfodor.diceroller.data.history.RollHistoryDatabase
import com.greenfodor.diceroller.data.history.RoomRollHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.time.Clock
import java.time.ZoneId
import javax.inject.Singleton

/**
 * Binds the persistence layer. The DataStore and the Room database are [Singleton]s, each
 * holding a single open handle to their file for the life of the process.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = DataStoreSettingsRepository.create(context)

    @Provides
    @Singleton
    fun provideRollHistoryDatabase(
        @ApplicationContext context: Context
    ): RollHistoryDatabase = RollHistoryDatabase.create(context)

    @Provides
    fun provideRollHistoryDao(database: RollHistoryDatabase): RollHistoryDao = database.rollHistoryDao()

    @Provides
    @Singleton
    fun provideRollHistoryRepository(dao: RollHistoryDao): RollHistoryRepository =
        RoomRollHistoryRepository(dao)

    /** The clock every timestamp and day grouping is measured against; swapped in tests. */
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

    /**
     * The device's current time zone, re-read on every injection so a zone change is picked up
     * without restarting the process.
     */
    @Provides
    fun provideZoneId(): ZoneId = ZoneId.systemDefault()

    /**
     * Scope for persistence writes that must run to completion. It lives for the whole process
     * and a failed write does not cancel the ones that follow.
     */
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
