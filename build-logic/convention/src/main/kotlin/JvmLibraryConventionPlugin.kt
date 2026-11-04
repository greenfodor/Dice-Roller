import com.greenfodor.diceroller.convention.configureKotlinJvm
import com.greenfodor.diceroller.convention.libs
import com.greenfodor.diceroller.convention.pluginId
import org.gradle.api.Plugin
import org.gradle.api.Project

/** Kotlin/JVM library module with the shared JVM target and lint setup. */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("kotlin-jvm"))
            pluginManager.apply(libs.pluginId("diceroller-lint"))

            configureKotlinJvm()
        }
    }
}
