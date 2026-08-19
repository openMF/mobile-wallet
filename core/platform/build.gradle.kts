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
    alias(libs.plugins.kmp.library.convention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Encapsulation-compliant re-export boundary (G-CORE-BASE-ENCAP): app-shell modules
            // (cmp-navigation / cmp-shared / cmp-android) reach the core-base/platform surface —
            // platformModule, GarbageCollectionManager, tryCollect — through core/ (never core-base
            // directly). `api` so those symbols are visible transitively.
            //
            // Fork-owned platform-specific code (expect/actual bridges a fork adds) also belongs here.
            // The bill-reminder scheduler that previously lived in this module migrated to feature/bills
            // + the cross-platform sync worker infra (worker-kmp + KMPNotifier).
            api(projects.coreBase.platform)
        }
    }
}
