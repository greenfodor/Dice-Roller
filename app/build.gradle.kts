plugins {
    alias(libs.plugins.diceroller.android.application)
}

android {
    namespace = "com.greenfodor.diceroller"

    defaultConfig {
        applicationId = "com.greenfodor.diceroller"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "com.greenfodor.diceroller.KoinTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            isProfileable = true
        }
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(project(":core:domain"))
    androidTestImplementation(project(":core:data"))
    androidTestImplementation(project(":core:designsystem"))
    androidTestImplementation(project(":feature:history:presentation"))
    androidTestImplementation(project(":feature:dice:presentation"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.foundation)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(platform(libs.koin.bom))
    androidTestImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.jetbrains.compose.components.resources)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
