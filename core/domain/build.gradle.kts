import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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

    api(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
