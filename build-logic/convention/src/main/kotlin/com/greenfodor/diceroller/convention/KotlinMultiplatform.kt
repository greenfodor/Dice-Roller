package com.greenfodor.diceroller.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Declares the Android, `desktop` JVM, `iosArm64` and `iosSimulatorArm64` targets with the
 * shared SDK levels and JVM target, enables Android host tests and adds `kotlin.test` to
 * `commonTest`. The module still sets its own Android `namespace`.
 */
internal fun Project.configureKotlinMultiplatform() {
    extensions.configure<KotlinMultiplatformExtension> {
        extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
            compileSdk = COMPILE_SDK
            minSdk = MIN_SDK
            compilerOptions {
                jvmTarget.set(JVM_TARGET)
            }
            withHostTest {}
        }

        jvm("desktop") {
            compilerOptions {
                jvmTarget.set(JVM_TARGET)
            }
        }
        iosArm64()
        iosSimulatorArm64()

        sourceSets.getByName("commonTest").dependencies {
            implementation(libs.findLibrary("kotlin-test").get())
        }
    }
}
