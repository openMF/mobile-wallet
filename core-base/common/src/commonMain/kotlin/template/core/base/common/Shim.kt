/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

/**
 * Foundation-migration shim (2026-08-01):
 *
 * Upstream KMP template renamed `template.core.base.common.*` → `kpt.core.base.common.*`
 * on `dev`. The only external fork consumer surface remaining on the old namespace is
 * `libs/mifos-loans` (2 files) using `toBase64DataUri`. This shim forwards that call
 * to the kpt.* source so the loans lib compiles without a same-turn import rewrite.
 * Deletion tracker: safe to remove once mifos-loans imports `kpt.core.base.common.toBase64DataUri`.
 */
@file:Suppress("PackageDirectoryMismatch", "ktlint:standard:filename")

package template.core.base.common

import kpt.core.base.common.toBase64DataUri as kptToBase64DataUri

@Deprecated(
    message = "Use kpt.core.base.common.toBase64DataUri",
    replaceWith = ReplaceWith("kpt.core.base.common.toBase64DataUri(mimeType)"),
)
fun ByteArray.toBase64DataUri(mimeType: String = "application/octet-stream"): String =
    this.kptToBase64DataUri(mimeType)
