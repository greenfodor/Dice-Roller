import com.greenfodor.diceroller.convention.libs
import com.greenfodor.diceroller.convention.pluginId
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * Applies detekt (shared config plus compose-rules) and ktlint (with the disabled standard rules).
 * In Kotlin Multiplatform modules the `detekt` task scans the Kotlin sources of every source set.
 */
class LintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("detekt"))
            pluginManager.apply(libs.pluginId("ktlint"))

            extensions.configure<DetektExtension> {
                toolVersion.set(libs.findVersion("detekt").get().requiredVersion)
                config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
                buildUponDefaultConfig.set(true)
            }

            extensions.configure<KtlintExtension> {
                additionalEditorconfig.set(
                    DISABLED_KTLINT_RULES.associate { "ktlint_standard_$it" to "disabled" }
                )
            }

            dependencies {
                add("detektPlugins", libs.findLibrary("detekt-compose-rules").get())
            }

            pluginManager.withPlugin(libs.pluginId("kotlin-multiplatform")) {
                val kotlinSourceDirs = provider {
                    extensions.getByType(KotlinMultiplatformExtension::class.java)
                        .sourceSets
                        .flatMap { it.kotlin.srcDirs }
                }
                extensions.configure<DetektExtension> {
                    source.setFrom(kotlinSourceDirs)
                }
            }
        }
    }

    private companion object {
        val DISABLED_KTLINT_RULES = listOf(
            "chain-method-continuation",
            "function-signature",
            "function-naming",
            "package-name",
            "filename",
            "multiline-expression-wrapping"
        )
    }
}
