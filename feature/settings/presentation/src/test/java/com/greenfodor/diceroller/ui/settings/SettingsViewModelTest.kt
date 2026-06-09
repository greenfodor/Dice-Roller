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
    initial: ThemeMode,
    initialHaptics: Boolean = true,
    initialShake: Boolean = true
) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    override val themeMode = state

    private val haptics = MutableStateFlow(initialHaptics)
    override val hapticFeedbackEnabled = haptics

    private val shake = MutableStateFlow(initialShake)
    override val shakeToRollEnabled = shake

    override suspend fun setThemeMode(mode: ThemeMode) {
        state.update { mode }
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        haptics.update { enabled }
    }

    override suspend fun setShakeToRollEnabled(enabled: Boolean) {
        shake.update { enabled }
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

    @Test
    fun `hapticFeedbackEnabled reflects the repository`() = runTest {
        val viewModel = SettingsViewModel(FakeSettingsRepository(ThemeMode.DARK, initialHaptics = false))

        assertEquals(false, viewModel.hapticFeedbackEnabled.first())
    }

    @Test
    fun `setHapticFeedbackEnabled forwards the selection to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = SettingsViewModel(repository)

        viewModel.setHapticFeedbackEnabled(false)

        assertEquals(false, repository.hapticFeedbackEnabled.first())
    }

    @Test
    fun `shakeToRollEnabled reflects the repository`() = runTest {
        val viewModel = SettingsViewModel(FakeSettingsRepository(ThemeMode.DARK, initialShake = false))

        assertEquals(false, viewModel.shakeToRollEnabled.first())
    }

    @Test
    fun `setShakeToRollEnabled forwards the selection to the repository`() = runTest {
        val repository = FakeSettingsRepository(ThemeMode.FOLLOW_SYSTEM)
        val viewModel = SettingsViewModel(repository)

        viewModel.setShakeToRollEnabled(false)

        assertEquals(false, repository.shakeToRollEnabled.first())
    }
}
