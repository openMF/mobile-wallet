/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.qr

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun QrCodeScanner(
    types: List<CodeType>,
    modifier: Modifier,
    onScanned: (String) -> Boolean,
) {
    UiScannerView(
        modifier = modifier,
        onScanned = onScanned,
        allowedMetadataTypes = types.toFormat(),
    )
}
