plugins {
    alias(libs.plugins.diceroller.kmp.library)
}

kotlin {
    android {
        namespace = "com.greenfodor.diceroller.core.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
        }
    }
}
