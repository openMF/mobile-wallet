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
    /** The primary endpoint (generic default from core-base). */
    val MAIN: UrlType = UrlType.MAIN

    // Example additions a multi-server fork would declare — name them for your domain:
    //   val SERVER1: UrlType = UrlType("SERVER1")
    //   val STAGING: UrlType = UrlType("STAGING")
    //   val PAYMENT_GATEWAY: UrlType = UrlType("PAYMENT_GATEWAY")

    /** All endpoint types this project exposes (extend as you add more above). */
    val all: List<UrlType> = listOf(MAIN)
}
