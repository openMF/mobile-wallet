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
import kotlinx.coroutines.CoroutineScope

/**
 * Builds the platform-appropriate jordond [Connectivity] engine for
 * [JordondNetworkMonitor]:
 *  - **Android / iOS** → `connectivity-device` (native `ConnectivityManager` / `NWPathMonitor`
 *    callbacks; instant, event-driven, no battery cost).
 *  - **Desktop / JS / WasmJs** → `connectivity-http` (URL reachability polling; jordond bundles
 *    the platform ktor engine).
 *
 * All actuals request `autoStart = true` so monitoring begins on construction and
 * [Connectivity.status] returns a live value for the seed.
 */
internal expect fun platformConnectivity(scope: CoroutineScope): Connectivity
