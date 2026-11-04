plugins {
    alias(libs.plugins.diceroller.kmp.compose)
}

kotlin {
    android {
        namespace = "com.greenfodor.diceroller.core.designsystem"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.androidx.compose.material.icons.extended)
        }
    }
}
