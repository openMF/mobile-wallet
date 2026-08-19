/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.cmp.feature.convention)
    id("org.convention.worker.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.datastore)
            implementation(projects.core.model)

            // worker-kmp core — CoroutineWorker / WorkerContext / WorkResult / WorkData /
            // WorkManager / WorkInfo public API used by the workers + DefaultWorkScheduler.
            // MUST be `api`: :sync's public worker classes (DataSyncWorker/NotificationWorker)
            // extend the public `CoroutineWorker`, so worker-kmp core is part of :sync's API
            // surface. It also lets the `@WorkerKmpWorkers` KSP codegen in the consuming
            // module (cmp-shared) resolve the workers' `CoroutineWorker` supertype — an
            // `implementation` dep would not leak transitively and KSP would fail supertype
            // resolution ("must extend CoroutineWorker; found supertypes: ...").
            api(libs.worker.kmp)

            // worker-kmp v4.0.0 — UniqueWorkObserver replaces the hand-rolled
            // per-platform `expect/actual provideSyncManager(...)` files.
            implementation(libs.worker.sync)

            // KMPNotifier 2.0.0 — kmpnotifier-local is the Firebase-free local-notification module
            // (Android/iOS/Desktop/Web). Requires Kotlin 2.4.0. NotificationWorker renders via
            // KMPNotifier.localNotifier (see infra/SyncNotifier.kt).
            implementation(libs.kmpnotifier.local)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            // worker-app convention plugin provides the worker-kmp Android backing
            // (cmp-worker-android factory). No extra deps required here.
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

// namespace ("kpt.sync") + compileSdk/minSdk are set by the cmp.feature.convention
// plugin (KMPLibraryConventionPlugin) via KotlinMultiplatformAndroidComponentsExtension
// .finalizeDsl(). A top-level `android { }` / `kotlin { androidLibrary { } }` block does
// NOT resolve under the AGP9 KMP library plugin (the rebase onto openMF/dev migrated to
// it) — sibling feature modules (feature/loans etc.) declare no android block for the
// same reason.
