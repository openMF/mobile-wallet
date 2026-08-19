/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.network

import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.module.Module

/**
 * core-base/network transport DSL — the streamlined, template-owned way to wire a REST API interface.
 *
 * A consumer's `ProjectNetworkModule` declares an API in ONE line by referencing the id of an access
 * point in its [AccessPointRegistry] (SoT: `app-profile/app.yaml#network.access_points`). `core-base`
 * auto-builds the Ktor client + Ktorfit from the access point's base URL + loggable host, so the consumer
 * writes ONLY the API interface + its generated `createXApi()` factory — no per-API Ktorfit boilerplate.
 */

/**
 * Build a Ktorfit for the REST access point declared under [accessPointId]. Base URL + loggable host +
 * proxied host come from this [AccessPointRegistry]; throws if the id isn't declared.
 */
fun AccessPointRegistry.ktorfitFor(accessPointId: String): Ktorfit {
    val ap = byId(accessPointId)
        ?: error(
            "No network access point '$accessPointId' declared in app-profile network.access_points " +
                "(AccessPointRegistry). Declare it there, or fix the id.",
        )
    return Ktorfit.Builder()
        .httpClient(
            client = httpClient(
                setupDefaultHttpClient(
                    baseUrl = ap.baseUrl,
                    loggableHosts = listOf(ap.loggableHost),
                    proxiedHosts = ap.proxiedHost?.let { listOf(it) } ?: emptyList(),
                ),
            ),
        )
        .build()
}

/**
 * Register a Ktorfit REST API [T] wired to the access point [accessPointId]. The [AccessPointRegistry]
 * is resolved from Koin (the fork registers `single { AccessPointRegistry(AppAccessPoints.points) }`).
 * Usage:
 * ```
 * val ProjectNetworkModule = module {
 *     restApi("jsonplaceholder") { it.createJsonPlaceholderApi() }
 * }
 * ```
 */
inline fun <reified T : Any> Module.restApi(
    accessPointId: String,
    crossinline create: (Ktorfit) -> T,
) {
    single<T> { create(get<AccessPointRegistry>().ktorfitFor(accessPointId)) }
}
