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
import dev.jordond.connectivity.HttpConnectivityOptions
import kotlinx.coroutines.CoroutineScope

/**
 * Desktop (JVM): no OS link-state API, so `connectivity-http` polls reachability of the
 * default hosts (bundles ktor-client-okhttp). 60s cadence balances responsiveness against
 * request noise; the one-shot seed check still fires immediately on construction.
 */
internal actual fun platformConnectivity(scope: CoroutineScope): Connectivity =
    Connectivity(
        HttpConnectivityOptions.build {
            autoStart = true
            pollingIntervalMs = 60_000
        },
        scope,
    )
