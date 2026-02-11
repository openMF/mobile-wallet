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

import io.github.vinceglb.filekit.PlatformFile

actual suspend fun decodeQrFromFile(file: PlatformFile): String? {
    // WasmJS QR decoding not yet implemented
    // Would require JavaScript interop with a QR decoding library
    return null
}
