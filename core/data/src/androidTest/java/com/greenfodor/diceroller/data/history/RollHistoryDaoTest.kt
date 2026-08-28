package com.greenfodor.diceroller.data.history

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RollHistoryDaoTest {
    private lateinit var database: RollHistoryDatabase
    private lateinit var dao: RollHistoryDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, RollHistoryDatabase::class.java).build()
        dao = database.rollHistoryDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertedRollIsReadBackWithEveryField() = runTest {
        dao.insert(entity(dieLabel = "2d6", values = listOf(4, 6), total = 10, timestampMillis = 1_000L))

        val stored = dao.observeAll().first().single()

        assertEquals("2d6", stored.dieLabel)
        assertEquals(listOf(4, 6), stored.values)
        assertEquals(10, stored.total)
        assertEquals(1_000L, stored.timestampMillis)
    }

    @Test
    fun insertAssignsAnIncreasingGeneratedId() = runTest {
        dao.insert(entity(timestampMillis = 1_000L))
        dao.insert(entity(timestampMillis = 2_000L))

        val ids = dao.observeAll().first().map { it.id }

        assertEquals(ids.distinct(), ids)
        assertTrue(ids.all { it > 0L })
    }

    @Test
    fun observeAllReturnsNewestRollFirst() = runTest {
        dao.insert(entity(dieLabel = "d4", timestampMillis = 1_000L))
        dao.insert(entity(dieLabel = "d20", timestampMillis = 3_000L))
        dao.insert(entity(dieLabel = "d8", timestampMillis = 2_000L))

        val labels = dao.observeAll().first().map { it.dieLabel }

        assertEquals(listOf("d20", "d8", "d4"), labels)
    }

    @Test
    fun rollsSharingATimestampAreOrderedByNewestId() = runTest {
        dao.insert(entity(dieLabel = "first", timestampMillis = 5_000L))
        dao.insert(entity(dieLabel = "second", timestampMillis = 5_000L))

        val labels = dao.observeAll().first().map { it.dieLabel }

        assertEquals(listOf("second", "first"), labels)
    }

    @Test
    fun clearRemovesEveryRoll() = runTest {
        dao.insert(entity(timestampMillis = 1_000L))
        dao.insert(entity(timestampMillis = 2_000L))

        dao.clear()

        assertEquals(emptyList<RollRecordEntity>(), dao.observeAll().first())
    }

    @Test
    fun clearOnAnEmptyHistoryLeavesItEmpty() = runTest {
        dao.clear()

        assertEquals(emptyList<RollRecordEntity>(), dao.observeAll().first())
    }

    @Test
    fun aSingleDieRollRoundTripsItsOneValue() = runTest {
        dao.insert(entity(dieLabel = "d20", values = listOf(17), total = 17, timestampMillis = 1_000L))

        assertEquals(listOf(17), dao.observeAll().first().single().values)
    }

    private fun entity(
        dieLabel: String = "d6",
        values: List<Int> = listOf(3),
        total: Int = 3,
        timestampMillis: Long
    ): RollRecordEntity = RollRecordEntity(
        dieLabel = dieLabel,
        values = values,
        total = total,
        timestampMillis = timestampMillis
    )
}
