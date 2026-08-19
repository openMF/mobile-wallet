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

import io.github.jan.supabase.logging.LogLevel

/**
 * Per-point Supabase client factory.
 *
 * Builds one [SupabaseConfigClient] per declared Supabase [AccessPoint]. URL always comes from
 * [AccessPoint.baseUrl] (the registry is the single URL SoT — reconciles the legacy path where
 * the URL was read from the secrets creds file). Anon key is resolved via [anonKeyFor], keyed by
 * access-point id, so a fork threads its per-project secrets through one narrow seam.
 *
 * Cached per id so consumers can inject the factory + resolve on demand without re-building.
 */
class SupabaseClientFactory(
    private val registry: AccessPointRegistry,
    private val anonKeyFor: (id: String) -> String,
    private val logLevel: LogLevel = LogLevel.INFO,
) {
    private val cache: MutableMap<String, SupabaseConfigClient> = mutableMapOf()

    /** Client for [id], or `null` if [id] is not a declared Supabase access point. */
    fun clientFor(id: String): SupabaseConfigClient? {
        val point = registry.byId(id)?.takeIf { it.kind == AccessPointKind.SUPABASE } ?: return null
        return cache.getOrPut(id) {
            SupabaseConfigClient(
                credentials = object : SupabaseCredentials {
                    override val url: String = point.baseUrl
                    override val anonKey: String = anonKeyFor(id)
                },
                logLevel = logLevel,
            )
        }
    }

    /** Map of every declared Supabase access-point id → its client. */
    fun clients(): Map<String, SupabaseConfigClient> =
        registry.supabasePoints().mapNotNull { p -> clientFor(p.id)?.let { p.id to it } }.toMap()
}
