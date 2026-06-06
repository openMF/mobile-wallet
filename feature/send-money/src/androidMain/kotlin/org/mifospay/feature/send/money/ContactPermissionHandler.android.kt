/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import androidx.compose.runtime.Composable
import org.mifospay.core.designsystem.component.PermissionBox

@Composable
actual fun ContactPermissionHandler() {
    PermissionBox(
        title = "Contact Permission Required",
        confirmButtonText = "Grant Permission",
        dismissButtonText = "Open Settings",
        requiredPermissions = listOf("android.permission.READ_CONTACTS"),
        description = "To help you select contacts for payments, we need access to your contacts. This allows you to quickly find and select contacts when making payments.",
        onGranted = { },
    )
}
