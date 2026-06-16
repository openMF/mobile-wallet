package org.mifospay.shared.navigation

import android.content.Intent
import androidx.navigation.NavHostController
import androidx.core.net.toUri

/**
 * Android actual — reconstructs an Intent from the stored URI string
 * and passes it to NavHostController.handleDeepLink().
 *
 * Android imports are legal here because this file is in androidMain.
 */
actual fun consumePendingDeepLink(navController: NavHostController) {
    val uriString = PendingDeepLinkStore.consume() ?: return
    val intent    = Intent(Intent.ACTION_VIEW, uriString.toUri())
    navController.handleDeepLink(intent)
}
