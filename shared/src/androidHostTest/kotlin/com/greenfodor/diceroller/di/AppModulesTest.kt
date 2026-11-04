package com.greenfodor.diceroller.di

import org.koin.android.test.verify.androidVerify
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import kotlin.test.Test

class AppModulesTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun everyDefinitionInTheAppModulesCanBeResolved() {
        module { includes(appModules) }.androidVerify()
    }
}
