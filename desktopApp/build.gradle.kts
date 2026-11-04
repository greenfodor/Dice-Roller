import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.diceroller.jvm.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.jetbrains.compose.components.resources)
    implementation(libs.kotlinx.coroutines.swing)
}

compose.resources {
    packageOfResClass = "com.greenfodor.diceroller.desktop.generated.resources"
}

compose.desktop {
    application {
        mainClass = "com.greenfodor.diceroller.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DiceRoller"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("src/main/composeResources/drawable/ic_launcher.png"))
            }
        }
    }
}
