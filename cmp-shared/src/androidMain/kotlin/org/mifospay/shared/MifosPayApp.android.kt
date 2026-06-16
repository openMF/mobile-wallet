/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import org.mifospay.shared.navigation.PendingDeepLinkStore

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
actual fun HandleDeepLinks(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(activity?.intent) {
        val intent = activity?.intent ?: return@LaunchedEffect
        val uri = intent.data ?: return@LaunchedEffect

        // Store the URI string — no Android type leaks into shared code.
        // Consumed by RootNavGraph once MAIN_GRAPH composes (post-passcode).
        PendingDeepLinkStore.store(uri.toString())

        // Clear intent data so rotation doesn't re-trigger this effect.
        activity.intent = activity.intent.apply { data = null }
    }
}
