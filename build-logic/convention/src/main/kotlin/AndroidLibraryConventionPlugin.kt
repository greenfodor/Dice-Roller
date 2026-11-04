import com.android.build.api.dsl.LibraryExtension
import com.greenfodor.diceroller.convention.configureKotlinAndroid
import com.greenfodor.diceroller.convention.libs
import com.greenfodor.diceroller.convention.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Android library module with the shared SDK levels, JVM target, test defaults and lint setup. */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("android-library"))
            pluginManager.apply(libs.pluginId("diceroller-lint"))

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testOptions.unitTests.isReturnDefaultValues = true
            }
        }
    }
}
