import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.compose.multiplatform.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.diceroller.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.diceroller.android.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = libs.plugins.diceroller.android.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidTest") {
            id = libs.plugins.diceroller.android.test.get().pluginId
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.diceroller.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("kmpLibrary") {
            id = libs.plugins.diceroller.kmp.library.get().pluginId
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = libs.plugins.diceroller.kmp.compose.get().pluginId
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("lint") {
            id = libs.plugins.diceroller.lint.get().pluginId
            implementationClass = "LintConventionPlugin"
        }
    }
}
