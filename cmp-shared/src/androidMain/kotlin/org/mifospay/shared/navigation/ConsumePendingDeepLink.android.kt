/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.navigation

import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavHostController

/**
 * Android actual — reconstructs an Intent from the stored URI string
 * and passes it to NavHostController.handleDeepLink().
 *
 * Android imports are legal here because this file is in androidMain.
 */
actual fun consumePendingDeepLink(navController: NavHostController) {
    val uriString = PendingDeepLinkStore.consume() ?: return
    val intent = Intent(Intent.ACTION_VIEW, uriString.toUri())
    navController.handleDeepLink(intent)
}
