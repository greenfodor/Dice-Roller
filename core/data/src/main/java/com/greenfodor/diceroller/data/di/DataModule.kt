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
import java.time.Clock
import javax.inject.Singleton

/**
 * Binds the persistence layer. Everything here is a [Singleton]: the DataStore and the Room
 * database each hold a single open handle to their file for the life of the process.
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
}
