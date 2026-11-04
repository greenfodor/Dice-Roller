import com.android.build.api.dsl.LibraryExtension
import com.greenfodor.diceroller.convention.configureAndroidCompose
import com.greenfodor.diceroller.convention.libs
import com.greenfodor.diceroller.convention.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** `diceroller.android.library` with the Compose compiler plugin and Compose enabled. */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("diceroller-android-library"))

            extensions.configure<LibraryExtension> {
                configureAndroidCompose(this)
            }
        }
    }
}
