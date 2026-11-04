package com.greenfodor.diceroller

import android.app.Application
import com.greenfodor.diceroller.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DiceRollerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DiceRollerApplication)
            modules(appModules)
        }
    }
}
