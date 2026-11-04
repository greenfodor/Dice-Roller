package com.greenfodor.diceroller

import android.app.Application
import com.greenfodor.diceroller.di.initKoin
import org.koin.android.ext.koin.androidContext

class DiceRollerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@DiceRollerApplication)
        }
    }
}
