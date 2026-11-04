package com.greenfodor.diceroller.di

import org.junit.Test
import org.koin.android.test.verify.androidVerify
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module

class AppModulesTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun everyDefinitionInTheAppModulesCanBeResolved() {
        module { includes(appModules) }.androidVerify()
    }
}
