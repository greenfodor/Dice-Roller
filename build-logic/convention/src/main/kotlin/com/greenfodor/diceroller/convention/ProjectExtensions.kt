package com.greenfodor.diceroller.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/** Plugin id of the `[plugins]` catalog entry [alias]. */
fun VersionCatalog.pluginId(alias: String): String = findPlugin(alias).get().get().pluginId
