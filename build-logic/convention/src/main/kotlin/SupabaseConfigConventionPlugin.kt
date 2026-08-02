/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.convention.libs

/**
 * Convention plugin that generates Supabase credentials from a JSON secrets file.
 *
 * This plugin:
 * 1. Reads credentials from `secrets/supabaseCredentialsFile.json`
 * 2. Generates a `SupabaseCredentials` object implementing `kpt.core.base.network.SupabaseCredentials`
 * 3. Adds the generated source to the commonMain source set
 * 4. Adds the supabase-postgrest dependency
 *
 * ## Usage
 *
 * Apply the plugin to your module's build.gradle.kts:
 * ```kotlin
 * plugins {
 *     alias(libs.plugins.kmp.supabase.config)
 * }
 *
 * // Optional: Configure the package name (defaults to module namespace + ".config")
 * supabaseConfig {
 *     packageName = "com.example.myapp.network.config"
 * }
 * ```
 *
 * ## Secrets File Format
 *
 * Create `secrets/supabaseCredentialsFile.json` in your project root:
 * ```json
 * {
 *   "url": "https://your-project.supabase.co",
 *   "anonKey": "your-anon-key"
 * }
 * ```
 *
 * ## Generated Code
 *
 * The plugin generates:
 * ```kotlin
 * object SupabaseCredentials : kpt.core.base.network.SupabaseCredentials {
 *     override val url = "https://your-project.supabase.co"
 *     override val anonKey = "your-anon-key"
 * }
 * ```
 */
class SupabaseConfigConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Create extension for configuration
            val extension = extensions.create("supabaseConfig", SupabaseConfigExtension::class.java)

            val generatedDir = layout.buildDirectory.dir("generated/supabase")
            val secretsFile = rootProject.file("secrets/supabaseCredentialsFile.json")

            // Register the code generation task
            val generateTask = tasks.register("generateSupabaseConfig") {
                // Determine package name from extension or derive from namespace
                val packageName = extension.packageName
                    ?: project.findProperty("android.namespace")?.toString()?.let { "$it.config" }
                    ?: "${project.group}.config"

                val packagePath = packageName.replace(".", "/")
                val outputFile = generatedDir.get().asFile
                    .resolve("$packagePath/SupabaseCredentials.kt")

                // Track file content as input property for proper up-to-date checking
                val fileContent = if (secretsFile.exists() && secretsFile.length() > 0) {
                    secretsFile.readText()
                } else {
                    ""
                }
                inputs.property("credentialsContent", fileContent)
                inputs.property("packageName", packageName)
                outputs.file(outputFile)

                doLast {
                    val currentContent = inputs.properties["credentialsContent"] as String
                    val currentPackage = inputs.properties["packageName"] as String
                    val (url, anonKey) = if (currentContent.isNotEmpty()) {
                        try {
                            parseCredentials(currentContent)
                        } catch (e: Exception) {
                            logger.warn("Failed to parse Supabase credentials file: ${e.message}")
                            Pair("", "")
                        }
                    } else {
                        logger.warn("Supabase credentials file not found at: ${secretsFile.absolutePath}")
                        logger.warn("Create secrets/supabaseCredentialsFile.json with 'url' and 'anonKey' fields")
                        Pair("", "")
                    }

                    outputFile.parentFile.mkdirs()

                    outputFile.writeText(
                        """
                        |/*
                        | * Copyright 2025 Mifos Initiative
                        | *
                        | * This Source Code Form is subject to the terms of the Mozilla Public
                        | * License, v. 2.0. If a copy of the MPL was not distributed with this
                        | * file, You can obtain one at https://mozilla.org/MPL/2.0/.
                        | *
                        | * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
                        | */
                        |package $currentPackage
                        |
                        |import kpt.core.base.network.SupabaseCredentials as BaseSupabaseCredentials
                        |
                        |/**
                        | * Generated Supabase credentials from secrets/supabaseCredentialsFile.json
                        | * DO NOT EDIT - This file is generated by SupabaseConfigConventionPlugin
                        | */
                        |object SupabaseCredentials : BaseSupabaseCredentials {
                        |    override val url: String = "$url"
                        |    override val anonKey: String = "$anonKey"
                        |}
                        """.trimMargin()
                    )
                }
            }

            // Configure the Kotlin Multiplatform extension
            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.named("commonMain") {
                    kotlin.srcDir(generatedDir)
                    dependencies {
                        implementation(libs.findLibrary("supabase-postgrest").get())
                    }
                }
            }

            // Make compile and KSP tasks depend on the generate task
            tasks.matching {
                it.name.startsWith("compileKotlin") || it.name.startsWith("ksp")
            }.configureEach {
                dependsOn(generateTask)
            }
        }
    }

    /**
     * Simple JSON parser for extracting url and anonKey fields.
     * Avoids dependency on kotlinx-serialization in build-logic.
     */
    private fun parseCredentials(jsonContent: String): Pair<String, String> {
        val urlPattern = """"url"\s*:\s*"([^"]*)"""".toRegex()
        val anonKeyPattern = """"anonKey"\s*:\s*"([^"]*)"""".toRegex()

        val url = urlPattern.find(jsonContent)?.groupValues?.getOrNull(1) ?: ""
        val anonKey = anonKeyPattern.find(jsonContent)?.groupValues?.getOrNull(1) ?: ""

        return Pair(url, anonKey)
    }
}

/**
 * Extension for configuring SupabaseConfigConventionPlugin.
 */
open class SupabaseConfigExtension {
    /**
     * The package name for the generated SupabaseCredentials object.
     * If not set, defaults to the module's android namespace + ".config"
     */
    var packageName: String? = null
}
