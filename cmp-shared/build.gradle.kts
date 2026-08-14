/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

@file:OptIn(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCacheApi::class)

import com.mobilebytelabs.kmpflavors.KmpFlavorExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.DisableCacheInKotlinVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kotlinCocoapods)
    // worker-kmp v4.0.0 — applies @WorkerKmpApp/@WorkerKmpWorkers codegen pipeline.
    // KSP processor scans this module's commonMain for @WorkerKmpWorkers (see
    // cmp/shared/WorkerDeclarations.kt) + emits per-platform installWorkerKmp{Platform}
    // files + the commonMain WorkerKmpAuto.kt shim into build/generated/worker-kmp-app/.
    // No `version` since the plugin is already on the buildscript classpath via the
    // composite-build substitution from build-logic/convention — adding `version` would
    // cause Gradle's "plugin is already on the classpath with an unknown version" error.
    id("io.github.mobilebytelabs.worker-app")
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // KGP rejects debuggable=true + optimized=true on the same binary
            // (kotlin:kgp:misconfiguration:incompatible-binary-configuration).
            optimized = buildType == NativeBuildType.RELEASE
        }
        // compose-signature:1.0.1 ships an iosArm64 klib that fails Kotlin/Native
        // static-cache generation on Kotlin 2.4.0 — the CI iOS build aborts with
        // "error: Failed to build cache for .../compose-signature-iosArm64Main-1.0.1.klib".
        // Disable the native cache for every binary of this target (framework + the
        // CocoaPods-plugin framework the CI xcodebuild links) so the klib is linked
        // directly. The `kotlin.native.cacheKind.<target>` gradle property that used to
        // do this was removed in 2.3.20; this per-binary DSL is its replacement.
        iosTarget.binaries.configureEach {
            disableNativeCache(
                DisableCacheInKotlinVersion.`2_4_0`,
                reason = "compose-signature:1.0.1 iosArm64 klib fails static-cache build on Kotlin 2.4.0",
            )
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Navigation Modules
            implementation(projects.cmpNavigation)
            implementation(compose.components.resources)
            implementation(projects.coreBase.platform)
            implementation(projects.coreBase.ui)

            implementation(libs.coil.kt.compose)

            // worker-kmp v4.0.0 — @WorkerKmpWorkers annotation site lives in this module
            // (cmp.shared.WorkerDeclarations.kt). Codegen runs in this module's build/
            // directory (consumer-side, not in published worker-kmp jars).
            implementation(libs.worker.app.annotations)
            // worker-kmp core — required DIRECTLY here (not just transitively via :sync) so the
            // @WorkerKmpWorkers KSP codegen can resolve the referenced workers' `CoroutineWorker`
            // supertype during commonMain metadata processing. KSP metadata resolution does not
            // traverse transitive klibs, so a transitive-only dep fails supertype resolution
            // ("must extend CoroutineWorker; found supertypes: ...").
            implementation(libs.worker.kmp)
            // The @WorkerKmpWorkers codegen emits `Generated_WorkerKmpInit.kt` per platform,
            // which references worker-koin (WorkerKmpHost / loadKoinModules / getKoin) + each
            // platform's WorkManager factory (desktopWorkManagerFactory, androidWorkManagerFactory,
            // …). The `worker-compose-all` umbrella supplies the koin integration + all platform
            // factories the generated code compiles against (same dep the worker-compose
            // convention adds to :sync).
            implementation(libs.worker.compose.all)
            // Worker classes referenced from the @WorkerKmpWorkers annotation live here.
            implementation(projects.sync)
            // DataSyncWorker's constructor params come from core:data (CurrencyRepository,
            // MacroIndicatorsRepository) + core:datastore (SyncStatePersister). The
            // @WorkerKmpWorkers KSP codegen reads every worker ctor param type for Koin
            // autowiring, and KSP metadata resolution needs them on the DIRECT classpath
            // (transitive-via-:sync klibs are not resolved) — else "could not resolve type
            // of constructor parameter ...".
            implementation(projects.core.data)
            implementation(projects.core.datastore)

            // Fork's KoinModules aggregator + MifosPayApp compose every feature/lib/domain/
            // network module into a single graph. Declared here so `import
            // org.mifospay.feature.*.di.*Module` + `import org.mifos.lib.loan.di.*`
            // + `import org.mifospay.core.{common,domain,network}.di.*` resolve.
            implementation(projects.core.common)
            implementation(projects.core.domain)
            implementation(projects.core.network)
            implementation(projects.feature.auth)
            implementation(projects.feature.home)
            implementation(projects.feature.settings)
            implementation(projects.feature.faq)
            implementation(projects.feature.editpassword)
            implementation(projects.feature.profile)
            implementation(projects.feature.history)
            implementation(projects.feature.payments)
            implementation(projects.feature.accounts)
            implementation(projects.feature.beneficiary)
            implementation(projects.feature.invoices)
            implementation(projects.feature.kyc)
            implementation(projects.feature.notification)
            implementation(projects.feature.savedcards)
            implementation(projects.feature.receipt)
            implementation(projects.feature.standingInstruction)
            implementation(projects.feature.transferIntrabank)
            implementation(projects.feature.transferInterbank)
            implementation(projects.feature.mpayQr)
            implementation(projects.feature.mpayQrScan)
            implementation(projects.feature.fastMpay)
            implementation(projects.feature.merchants)
            implementation(projects.feature.upiSetup)
            implementation(projects.feature.passcode)
            implementation(projects.feature.autopay)
            implementation(projects.feature.sendMoney)
            implementation(projects.feature.pocket)
            implementation(projects.libs.mifosLoans)
            implementation(projects.feature.finance)

            // MifosAppState + HomeScreen tie to material3 window-size-class multiplatform;
            // Preview annotations use org.jetbrains.compose.ui.tooling.preview.
            implementation(libs.window.size)
            implementation(compose.components.uiToolingPreview)
        }

        desktopMain.dependencies {
            // Desktop specific dependencies
            implementation(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }

    cocoapods {
        summary = "KMP Shared Module"
        homepage = "https://github.com/openMF/kmp-project-template"
        version =
            project.version
                .toString()
                .substringBefore("-")
                .substringBefore("+")
        ios.deploymentTarget = "16.0"
        podfile = project.file("../cmp-ios/Podfile")

        // Map every {flavor}{BuildType} Xcode build configuration to a Kotlin/Native build
        // type, derived DYNAMICALLY from the kmpFlavors DSL — no hardcoded config list, so
        // adding / renaming / removing a flavor or build type auto-propagates here on the
        // next build. Without this the Kotlin CocoaPods plugin cannot identify
        // debug-vs-release for a non-standard CONFIGURATION name and fails the ComposeApp
        // framework build ("Could not identify build type for Kotlin framework"). The
        // config name mirrors GenerateIosFlavorXcconfigsTask ({flavor}{BuildType}); a
        // debuggable build type → DEBUG, otherwise → RELEASE.
        project.extensions.getByType<KmpFlavorExtension>().let { flavorsExt ->
            for (flavor in flavorsExt.flavors) {
                for (buildType in flavorsExt.buildTypes) {
                    val configName = flavor.name + buildType.name.replaceFirstChar { it.uppercase() }
                    xcodeConfigurationToNativeBuildType[configName] =
                        if (buildType.isDebuggable.getOrElse(false)) {
                            NativeBuildType.DEBUG
                        } else {
                            NativeBuildType.RELEASE
                        }
                }
            }
        }

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "cmp.shared.generated.resources"
}
