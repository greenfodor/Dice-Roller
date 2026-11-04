import com.android.build.api.dsl.KotlinMultiplatformAndroidHostTestCompilation

plugins {
    alias(libs.plugins.diceroller.kmp.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "com.greenfodor.diceroller.feature.dice.presentation"

        androidResources {
            enable = true
        }

        compilations.withType<KotlinMultiplatformAndroidHostTestCompilation>().configureEach {
            isReturnDefaultValues = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:designsystem"))

            implementation(libs.androidx.navigation3.runtime)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core.viewmodel)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            api(libs.jetbrains.compose.material3.windowsizeclass)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

compose.resources {
    publicResClass = true
}
