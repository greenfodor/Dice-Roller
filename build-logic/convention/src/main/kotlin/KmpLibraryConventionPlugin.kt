import com.greenfodor.diceroller.convention.configureKotlinMultiplatform
import com.greenfodor.diceroller.convention.libs
import com.greenfodor.diceroller.convention.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * Kotlin Multiplatform library module targeting Android, desktop JVM, iOS device and iOS
 * simulator, with Android host tests, `kotlin.test` in `commonTest` and lint setup. Its `test`
 * task runs the desktop and Android host tests.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("kotlin-multiplatform"))
            pluginManager.apply(libs.pluginId("android-kotlin-multiplatform-library"))
            pluginManager.apply(libs.pluginId("diceroller-lint"))

            configureKotlinMultiplatform()

            tasks.register("test") {
                group = LifecycleBasePlugin.VERIFICATION_GROUP
                description = "Runs the desktop and Android host tests."
                dependsOn("desktopTest", "testAndroidHostTest")
            }
        }
    }
}
