import com.android.build.api.dsl.ApplicationExtension
import com.greenfodor.diceroller.convention.TARGET_SDK
import com.greenfodor.diceroller.convention.configureAndroidCompose
import com.greenfodor.diceroller.convention.configureKotlinAndroid
import com.greenfodor.diceroller.convention.libs
import com.greenfodor.diceroller.convention.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Android application module with Compose, the shared SDK levels, JVM target and lint setup. */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("android-application"))
            pluginManager.apply(libs.pluginId("diceroller-lint"))

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                defaultConfig.targetSdk = TARGET_SDK
                testOptions.unitTests.isReturnDefaultValues = true
            }
        }
    }
}
