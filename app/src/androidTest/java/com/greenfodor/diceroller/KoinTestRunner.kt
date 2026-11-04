package com.greenfodor.diceroller

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Swaps in a plain [Application] that does not start Koin, so each instrumentation test starts
 * its own Koin container through a `KoinTestRule`.
 */
class KoinTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application = super.newApplication(classLoader, Application::class.java.name, context)
}
