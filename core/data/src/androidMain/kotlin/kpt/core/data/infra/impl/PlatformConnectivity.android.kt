/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.infra.impl

import dev.jordond.connectivity.Connectivity
import dev.jordond.connectivity.ConnectivityOptions
import kotlinx.coroutines.CoroutineScope

/**
 * Android: native `ConnectivityManager` callbacks via `connectivity-device`. The app
 * `Context` is supplied automatically by jordond's `connectivity-tools-android`
 * androidx.startup initializer — no manual Context wiring.
 */
internal actual fun platformConnectivity(scope: CoroutineScope): Connectivity =
    Connectivity(ConnectivityOptions(autoStart = true), scope)
