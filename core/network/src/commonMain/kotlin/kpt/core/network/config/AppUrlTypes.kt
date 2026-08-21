/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.network.config

import kpt.core.base.network.UrlType

/**
 * Project-level catalogue of named API endpoints for runtime base-URL switching
 * (see [kpt.core.base.network.MultiUrlConfigProvider] / [kpt.core.base.network.DynamicBaseUrlPlugin]).
 *
 * [UrlType] is an **open** value class — `core-base` ships only the generic [UrlType.MAIN]; this is
 * where the project defines its own vocabulary. A fork edits this object freely (rename, add, remove)
 * without touching `core-base`. The toolkit itself has a single endpoint, so only [MAIN] is defined;
 * the extras below are illustrative of how a multi-server fork would extend it.
 */
object AppUrlTypes {
    /** Primary REST endpoint (generic default from core-base). */
    val MAIN: UrlType = UrlType.MAIN

    /** Secondary REST endpoint — e.g. a staging / mirror server. */
    val STAGING: UrlType = UrlType("STAGING")

    /** Supabase data-plane access point (Postgrest by default). */
    val SUPABASE_DATA: UrlType = UrlType("SUPABASE_DATA")

    /**
     * All endpoint types this project exposes. Base URLs for each are declared once in
     * [AppAccessPoints]; a fork renames / adds / removes entries here and there in lockstep.
     */
    val all: List<UrlType> = listOf(MAIN, STAGING, SUPABASE_DATA)
}
