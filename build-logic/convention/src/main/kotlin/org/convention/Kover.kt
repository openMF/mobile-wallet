package org.convention

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Configures the `kover` plugin with the [configure] lambda.
 *
 * Mirrors the [detektGradle] / [spotlessGradle] helpers in ProjectExtensions.kt
 * so KoverConventionPlugin reads the same way as DetektConventionPlugin and
 * SpotlessConventionPlugin.
 */
internal inline fun Project.koverGradle(crossinline configure: KoverProjectExtension.() -> Unit) =
    extensions.configure<KoverProjectExtension> {
        configure()
    }

/**
 * Root-level kover report configuration — single source of truth for the
 * filter / verify rules that gate the aggregated coverage report.
 *
 * Applied by KoverConventionPlugin when it runs on the root project. Lives
 * here (not inline in the plugin class) for parity with the detekt / spotless
 * convention plugins, which delegate their configuration to helpers in this
 * package.
 */
internal fun Project.configureKoverRootReports() = koverGradle {
    reports {
        filters {
            excludes {
                classes(
                    "*.di.*",                       // Koin / kotlin-inject DI modules
                    "*.BuildConfig",
                    "*ComposableSingletons*",       // Compose generated lambda holders
                    "*_*Factory*",                  // Generated factories
                    "*\$ComposableLambda\$*",
                    "*Preview*",                    // @Preview functions
                    "*Test*",                       // test helpers themselves
                )
                packages(
                    "*.generated.*",
                    "*.ksp.*",
                )
                annotatedBy(
                    // @Composable funcs are better tested via screenshot/UI tests,
                    // not Kover line coverage.
                    "androidx.compose.runtime.Composable",
                )
            }
        }
        verify {
            // Phase 1 floor — single global threshold while coverage grows.
            // Per-module thresholds added as test-coverage PRs raise individual modules.
            rule { minBound(40) }
        }
    }
}
