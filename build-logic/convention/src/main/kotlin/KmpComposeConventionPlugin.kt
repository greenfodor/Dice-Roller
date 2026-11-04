import com.greenfodor.diceroller.convention.libs
import com.greenfodor.diceroller.convention.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * `diceroller.kmp.library` with the Compose compiler plugin and Compose Multiplatform. Adds
 * runtime, foundation, material3, ui, compose resources and preview annotations to `commonMain`,
 * and the preview tooling to the Android runtime classpath. The generated `Res` class lives in
 * `com.greenfodor.diceroller.<project path>.generated.resources`.
 */
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("diceroller-kmp-library"))
            pluginManager.apply(libs.pluginId("kotlin-compose"))
            pluginManager.apply(libs.pluginId("compose-multiplatform"))

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    COMMON_LIBRARIES.forEach { implementation(libs.findLibrary(it).get()) }
                }
            }

            extensions.getByType<ComposeExtension>().extensions.configure<ResourcesExtension> {
                val modulePackage = path.removePrefix(":").replace(':', '.')
                packageOfResClass = "com.greenfodor.diceroller.$modulePackage.generated.resources"
            }

            dependencies {
                add("androidRuntimeClasspath", libs.findLibrary("jetbrains-compose-ui-tooling").get())
            }
        }
    }

    private companion object {
        val COMMON_LIBRARIES = listOf(
            "jetbrains-compose-runtime",
            "jetbrains-compose-foundation",
            "jetbrains-compose-material3",
            "jetbrains-compose-ui",
            "jetbrains-compose-components-resources",
            "jetbrains-compose-ui-tooling-preview"
        )
    }
}
