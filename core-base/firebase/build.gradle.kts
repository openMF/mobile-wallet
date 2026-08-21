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
    // No Compose compiler here: this module only re-exports cmp-firebase(-compose) + wires DI; it has
    // no @Composable of its own. Consumers that call the Compose helpers (cmp-navigation, feature/*)
    // apply the Compose plugin themselves and get cmp-firebase-compose transitively.
    alias(libs.plugins.kmp.core.base.library.convention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Base Firebase host (template-owned): the published cmp-firebase engine (Analytics +
            // Crashlytics behind FirebaseKit) + its Compose companion. `api` so the project layer
            // (core/firebase) and app-shell reach the library types through core/ per G-CORE-BASE-ENCAP.
            api(libs.cmp.firebase)
            api(libs.cmp.firebase.compose)
            api(libs.koin.core)
        }
    }
}
