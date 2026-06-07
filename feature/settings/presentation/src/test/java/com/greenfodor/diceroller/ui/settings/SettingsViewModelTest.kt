package com.greenfodor.diceroller.ui.settings

import com.greenfodor.diceroller.data.SettingsRepository
import com.greenfodor.diceroller.data.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

private class FakeSettingsRepository(
    initial: ThemeMode
) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    override val themeMode = state

    override suspend fun setThemeMode(mode: ThemeMode) {
        state.update { mode }
    }
}

class SettingsViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `themeMode starts null then reflects the repository`() = runTest {
        val viewModel = SettingsViewModel(FakeSettingsRepository(ThemeMode.DARK))

        assertNull(viewModel.themeMode.value)
        assertEquals(ThemeMode.DARK, viewModel.themeMode.first { it != null })
    }

    @Test
    fun `setThemeMode forwards the selection to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = SettingsViewModel(repository)

        viewModel.setThemeMode(ThemeMode.LIGHT)

        assertEquals(ThemeMode.LIGHT, repository.themeMode.first())
    }
}
