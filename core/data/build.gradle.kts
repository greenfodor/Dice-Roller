import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.greenfodor.diceroller.core.data"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

ktlint {
    additionalEditorconfig.set(
        mapOf(
            "ktlint_standard_chain-method-continuation" to "disabled",
            "ktlint_standard_function-signature" to "disabled",
            "ktlint_standard_function-naming" to "disabled",
            "ktlint_standard_package-name" to "disabled",
            "ktlint_standard_filename" to "disabled",
            "ktlint_standard_multiline-expression-wrapping" to "disabled"
        )
    )
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    detektPlugins(libs.detekt.compose.rules)

    implementation(project(":core:domain"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
