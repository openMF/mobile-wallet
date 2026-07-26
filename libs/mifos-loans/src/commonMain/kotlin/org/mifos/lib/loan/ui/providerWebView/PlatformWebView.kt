/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.lib.loan.ui.providerWebView

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Renders [url] in an embedded web view.
 *
 * Backed by `compose-webview-multiplatform` on Android and iOS. That library doesn't publish
 * js/wasmJs artifacts and its desktop support needs a separate KCEF runtime download, so those
 * platforms fall back to a plain placeholder instead of an embedded browser (see the respective
 * `actual` implementations).
 */
@Composable
expect fun PlatformWebView(url: String, modifier: Modifier = Modifier)
