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

import kpt.core.base.network.SupabaseConfigClient
import kpt.core.base.network.SupabaseCredentials
import org.koin.dsl.module
import kpt.core.network.config.SupabaseCredentials as GeneratedSupabaseCredentials

// NOTE: Backend URLs are sourced from Koin-injected config classes (FredApiConfig,
// FrankfurterApiConfig, WorldBankApiConfig), each carrying a `baseUrl: String` field that
// defaults to the public production endpoint. Forks override these `single { ... }` bindings
// at their app-module level to swap in mocks / mirrors / per-environment endpoints.
//
// TODO: If/when BuildKonfig is added to the project for FRED_API_KEY (the canonical Plan 10
// strategy), thread BuildKonfig.FRED_BASE_URL / FRANKFURTER_BASE_URL / WORLDBANK_BASE_URL
// through here. Today (no BuildKonfig in-tree), the default-param approach on each config class
// provides the same fork-customisation surface without the buildscript complexity.
// Dynamic server config (consumer-facing, from core-base/network):
//   - SupabaseConfigClient is registered below so forks can fetch runtime server config from a
//     Supabase `app_config`-style table. Its credentials are generated from the gitignored
//     `secrets/supabaseCredentialsFile.json` (SupabaseConfigConventionPlugin); absent that file the
//     creds are empty, so the client stays inert (isConfigured == false). Forks drop in the file, or
//     override the `single<SupabaseCredentials>` binding.
//   - DynamicBaseUrlPlugin is an opt-in of setupDefaultHttpClient(...): a fork implements
//     DynamicUrlConfigProvider (reads its selected server, keyed by AppUrlTypes) and passes it as
//     `dynamicUrlProvider = get()` on any client that should switch base URL at runtime. The
//     toolkit's own fixed-URL APIs (FRED / World Bank / CoinGecko / Frankfurter) don't use it.
val NetworkModule = module {

    // Supabase-backed dynamic config — overridable by forks. The credentials object is generated
    // from secrets/supabaseCredentialsFile.json by SupabaseConfigConventionPlugin (empty when the
    // file is absent, so the client stays inert until a fork provides a project).
    single<SupabaseCredentials> { GeneratedSupabaseCredentials }
    single { SupabaseConfigClient(credentials = get()) }
}
