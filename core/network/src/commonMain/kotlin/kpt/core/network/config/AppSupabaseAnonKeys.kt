/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.network.config

/**
 * The per-fork map of Supabase access-point id → anon key.
 *
 * **SoT: fork-local secrets** — a fork wires this from its per-point secrets file (one anon key
 * per id declared under `AppAccessPoints#points` with `AccessPointKind.SUPABASE`). The
 * sentinel-bounded block below is the seam the fork edits (or `syncForkConfig` regenerates,
 * once wired). Default entries mirror the template's declared Supabase point ids so the gate
 * NAP-7 passes on the neutral template; a fork replaces the placeholder values with real keys.
 */
object AppSupabaseAnonKeys {
    // syncForkConfig:supabase-anon-keys:begin — fork-owned per-point anon-key map.
    // Every id declared under AppAccessPoints#points with AccessPointKind.SUPABASE MUST have a row
    // here or NAP-7 will FAIL (the point would be decorative).
    private val byId: Map<String, String> = mapOf(
        "supabase_data" to "YOUR_SUPABASE_ANON_KEY_supabase_data",
    )
    // syncForkConfig:supabase-anon-keys:end

    /** Anon key for [id], or empty string if [id] is not registered. */
    fun forId(id: String): String = byId[id].orEmpty()
}
