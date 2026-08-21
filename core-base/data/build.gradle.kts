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
    alias(libs.plugins.kmp.core.base.library.convention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // ChangeListVersions (delta-sync bookkeeping) consumed by Synchronizer.
            implementation(projects.coreBase.datastore)
            // DispatcherManager (used by the Android TimeZoneMonitor impl).
            implementation(projects.coreBase.common)

            // NetworkMonitor is backed by the KmpToolkit multiplatform connectivity monitor;
            // api so consumers that read NetworkMonitor state see its types.
            api(libs.cmp.network.monitor)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            // androidx.tracing.trace around the Android TimeZone broadcast-receiver flow.
            implementation(libs.androidx.tracing.ktx)
        }
    }
}
