/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.network.di

import kpt.core.base.network.AccessPointRegistry
import kpt.core.base.network.MultiUrlConfigProvider
import kpt.core.base.network.SupabaseClientFactory
import kpt.core.base.network.SupabaseConfigClient
import kpt.core.base.network.SupabaseCredentials
import kpt.core.network.config.AppAccessPoints
import kpt.core.network.config.AppMultiUrlConfigProvider
import kpt.core.network.config.AppSupabaseAnonKeys
import org.koin.dsl.module

// NOTE: Backend base URLs are NOT hardcoded here or in config classes anymore — every server is a
// declared access point in app-profile/app.yaml#network.access_points (→ AccessPointRegistry via
// syncForkConfig). The demo APIs are wired in ProjectNetworkModule via `restApi("<id>") { … }`, which
// auto-builds each transport from its access point. Fork-customisation = edit app.yaml + syncForkConfig.
// FRED's API key remains a per-request @Query param (a vault secret: mifos-x-fred-api-key → BuildKonfig).
// Dynamic server config (consumer-facing, from core-base/network):
//   - SupabaseConfigClient(s) are registered below so forks can fetch runtime server config from a
//     Supabase `app_config`-style table. The AccessPointRegistry is the single URL SoT: SupabaseClientFactory
//     materializes one client per declared SUPABASE access point (URL from AccessPoint.baseUrl), and the
//     default `single<SupabaseCredentials>` binding derives its URL from the primary Supabase access point.
//     Anon keys resolve per-id via AppSupabaseAnonKeys (fork-owned secrets seam); absent a key the creds are
//     empty, so the client stays inert (isConfigured == false). Forks edit app-profile + AppSupabaseAnonKeys.
//   - DynamicBaseUrlPlugin is an opt-in of setupDefaultHttpClient(...): a fork implements
//     DynamicUrlConfigProvider (reads its selected server, keyed by AppUrlTypes) and passes it as
//     `dynamicUrlProvider = get()` on any client that should switch base URL at runtime. The
//     toolkit's own fixed-URL APIs (FRED / World Bank / CoinGecko / Frankfurter) don't use it.
// INFRA-ONLY, owner: template (E1 / C2). The demo API configs + FintechApiClient + demo API bindings
// relocated to the fork-owned [kpt.core.network.demo.di.ProjectNetworkModule]; this aggregator carries
// ZERO `kpt.core.*.demo.*` imports so a template sync can blind-copy it without re-introducing demo
// wiring a fork already stripped.
val NetworkModule = module {

    // The fork's generated access points, wrapped by the framework registry mechanism (core-base/network).
    // The restApi("<id>") DSL and AppMultiUrlConfigProvider both resolve transports/base-URLs from this.
    single { AccessPointRegistry(AppAccessPoints.points) }

    // Unified access-point provider — resolves every named UrlType to its REST base URL from the
    // AccessPointRegistry. Clients thread it via
    // setupDefaultHttpClient(multiUrlProvider = get(), urlType = AppUrlTypes.<NAME>).
    single<MultiUrlConfigProvider> { AppMultiUrlConfigProvider(get()) }

    // Per-point Supabase client factory — URL from AccessPointRegistry, anon key by id.
    single {
        SupabaseClientFactory(
            registry = get(),
            anonKeyFor = AppSupabaseAnonKeys::forId,
        )
    }

    // Map<id, client> — every declared Supabase access point materializes exactly one client.
    single<Map<String, SupabaseConfigClient>> { get<SupabaseClientFactory>().clients() }

    // Reconciled default SupabaseCredentials: URL from primary Supabase access point, anon key
    // from the id-keyed resolver. Replaces the legacy read of the secrets creds file's URL — the
    // registry is now the single URL SoT (RESEARCH.md Area 3 reconciliation).
    single<SupabaseCredentials> {
        val registry: AccessPointRegistry = get()
        val primary = registry.supabasePoints().firstOrNull()
        object : SupabaseCredentials {
            override val url: String = primary?.baseUrl.orEmpty()
            override val anonKey: String = primary?.let { AppSupabaseAnonKeys.forId(it.id) }.orEmpty()
        }
    }
    single { SupabaseConfigClient(credentials = get()) }
}
