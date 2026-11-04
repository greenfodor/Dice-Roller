package com.greenfodor.diceroller.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

const val COMPILE_SDK = 37
const val TARGET_SDK = 37
const val MIN_SDK = 29

val JAVA_VERSION = JavaVersion.VERSION_21
val JVM_TARGET = JvmTarget.JVM_21

/** Sets compileSdk, minSdk and the Java/Kotlin JVM target of an Android module. */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk {
            version = release(COMPILE_SDK)
        }
        defaultConfig.minSdk = MIN_SDK
        compileOptions.sourceCompatibility = JAVA_VERSION
        compileOptions.targetCompatibility = JAVA_VERSION
    }
    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JVM_TARGET)
        }
    }
}

/** Sets the Java/Kotlin JVM target of a Kotlin/JVM module. */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JAVA_VERSION
        targetCompatibility = JAVA_VERSION
    }
    extensions.configure<KotlinJvmProjectExtension> {
        compilerOptions {
            jvmTarget.set(JVM_TARGET)
        }
    }
}

/** Applies the Compose compiler plugin and enables Compose for an Android module. */
internal fun Project.configureAndroidCompose(commonExtension: CommonExtension) {
    pluginManager.apply(libs.pluginId("kotlin-compose"))
    commonExtension.buildFeatures.compose = true
}
