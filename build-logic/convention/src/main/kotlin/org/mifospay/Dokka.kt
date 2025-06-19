package org.mifospay

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

/**
 * Configures Dokka documentation settings for a feature module.
 * This extension function replaces the FeatureLibraryPlugin class by directly
 * applying the same configuration to the target Project.
 */
// Add this in a shared convention plugin or utilities script
fun Project.configureFeatureDocumentation() {
    plugins.withId("org.jetbrains.dokka") {
        tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
            outputDirectory.set(layout.buildDirectory.dir("dokka"))
            moduleName.set(project.name)

            // Add KMP-specific configuration only for multiplatform modules
            if (pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                dokkaSourceSets.configureEach {
                   // includes.from("README.md")  // Include KMP-specific docs
                }
            }
        }
    }
}