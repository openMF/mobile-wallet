/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

/* =================================================================================
 *  DO NOT EDIT to add consumer-specific flavors. This file is SYNCED from
 *  kmp-project-template into every downstream consumer app via sync-dirs.sh.
 *  Edits here will be overwritten on the next sync.
 *
 *  To add consumer-specific flavors / dimensions / overrides, create:
 *      build-logic/convention/src/main/kotlin/local/LocalFlavors.kt
 *
 *  That local/ directory is excluded from sync-dirs.sh and survives every sync.
 *  See docs/FLAVORS_EXTENSION.md.
 * ================================================================================= */

import com.mobilebytelabs.kmpflavors.KmpFlavorExtension
import com.mobilebytelabs.kmpflavors.KmpFlavorPlugin
import org.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

/**
 * Convention plugin that wires `kmp-product-flavors` with the BASE flavor contract
 * every downstream consumer app inherits:
 *
 * - `tier`: `demo` (default — demo credentials for testers / public demo APK) and
 *   `prod` (real customers).
 * - `buildType`: `debug` (default — debuggable), `staging`, `release`.
 *
 * ## AGP bridge
 *
 * `bridgeAgpProductFlavors` and `bridgeAgpBuildTypes` default to `true` in the plugin
 * and use `AgpProductFlavorRegistrar` (hooked via `pluginManager.withPlugin`) — the
 * correct AGP lifecycle hook (fires synchronously before AGP's afterEvaluate). No manual
 * `android { productFlavors {} }` block is needed in build-logic, including for pure
 * `com.android.application` modules that do not apply `kotlin("multiplatform")`.
 *
 * Consumer apps extend this contract by creating
 * `build-logic/convention/src/main/kotlin/local/LocalFlavors.kt` — see
 * [LocalFlavorsLoader] for the hook and `docs/FLAVORS_EXTENSION.md` for examples.
 */
class KMPFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 1. Apply the upstream plugin (provides KmpFlavorExtension + codegen + source-set wiring
            //    + AGP bridge via AgpProductFlavorRegistrar.whenObjectAdded).
            pluginManager.apply(KmpFlavorPlugin::class.java)

            // 2. Configure the KMP-side flavor contract.
            //    buildConfigPackage comes from gradle/libs.versions.toml ([versions].appId)
            //    so forks change the brand by editing ONE line.
            extensions.configure<KmpFlavorExtension> {
                buildConfigPackage.set(libs.findVersion("appId").get().requiredVersion)
                // App identity for KmpFlavorsRuntime — single-sourced from libs.versions.toml
                // so forks rebrand by editing one line. appId gets the active flavor's id
                // suffix appended; appDisplayName is the human-facing name.
                appId.set(libs.findVersion("appId").get().requiredVersion)
                appDisplayName.set(libs.findVersion("appDisplayName").get().requiredVersion)
                enableBuildTypes.set(true)

                // iOS xcconfig generation + variants.json export are provided by the plugin
                // (kmp-product-flavors 2.8.3+). Identity stays in Config.xcconfig
                // ($(APP_BUNDLE_ID) / $(TEAM_ID), synced from libs.versions.toml via
                // syncForkConfig), so the per-variant xcconfigs reference those vars and Pods
                // settings flow in per-configuration. Replaces the former hand-maintained
                // GenerateIosFlavorXcconfigsTask + ExportKmpFlavorsManifestTask in build-logic.
                iosXcconfigGeneration.set(true)
                iosManifestExport.set(true)
                iosBundleIdBaseExpr.set("\$(APP_BUNDLE_ID)")
                iosDevelopmentTeamExpr.set("\$(TEAM_ID)")
                iosCocoapodsIntegration.set(true)

                flavorDimensions {
                    register("contentType") { priority.set(0) }
                }

                flavors {
                    register("demo") {
                        dimension.set("contentType")
                        isDefault.set(true)
                        applicationIdSuffix.set(".demo")
                        bundleIdSuffix.set(".demo")
                        desktopWindowTitleSuffix.set(" (Demo)")
                        webTitleSuffix.set(" (Demo)")
                        buildConfigField("Boolean", "IS_DEMO_BUILD", "true")
                        buildConfigField("String", "BASE_URL", "\"https://demo.openmf.org\"")
                        buildConfigField("String", "DEMO_USERNAME", "\"demo\"")
                        buildConfigField("String", "DEMO_PASSWORD", "\"demo\"")
                    }
                    register("prod") {
                        dimension.set("contentType")
                        buildConfigField("Boolean", "IS_DEMO_BUILD", "false")
                        buildConfigField("String", "BASE_URL", "\"https://api.openmf.org\"")
                        buildConfigField("String", "DEMO_USERNAME", "\"\"")
                        buildConfigField("String", "DEMO_PASSWORD", "\"\"")
                    }
                }

                buildTypes {
                    register("debug") {
                        isDefault.set(true)
                        isDebuggable.set(true)
                        applicationIdSuffix.set(".debug")
                        buildConfigField("Boolean", "ENABLE_LOGGING", "true")
                        buildConfigField("Boolean", "SHOW_DEBUG_OVERLAY", "true")
                        buildConfigField("String", "LOG_TAG", "\"KMPTemplate-DEBUG\"")
                    }
                    register("staging") {
                        isDebuggable.set(false)
                        applicationIdSuffix.set(".staging")
                        buildConfigField("Boolean", "ENABLE_LOGGING", "true")
                        buildConfigField("Boolean", "SHOW_DEBUG_OVERLAY", "false")
                        buildConfigField("String", "LOG_TAG", "\"KMPTemplate-STAGING\"")
                    }
                    register("release") {
                        isDebuggable.set(false)
                        isMinifyEnabled.set(true)
                        buildConfigField("Boolean", "ENABLE_LOGGING", "false")
                        buildConfigField("Boolean", "SHOW_DEBUG_OVERLAY", "false")
                        buildConfigField("String", "LOG_TAG", "\"KMPTemplate\"")
                    }
                }

                // Consumer extension hook — must be the LAST statement so the
                // local file sees the fully-populated extension.
                LocalFlavorsLoader.applyIfPresent(this, target)
            }

            // iOS xcconfig generation + variants.json export (generateIosFlavorXcconfigs /
            // kmpFlavorsBootstrapXcode / exportKmpFlavorsManifest) are registered by the
            // kmp-product-flavors plugin itself, driven by the ios* DSL flags set above —
            // no hand-maintained build-logic tasks. (Was: registerIosFlavorXcconfigsTask() +
            // registerExportKmpFlavorsManifestTask(), removed in the 2.8.3 adoption.)
        }
    }

}
