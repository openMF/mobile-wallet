/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.security

import androidx.compose.runtime.Composable
import org.mifospay.core.designsystem.component.MifosDialogBox

/**
 * 401 forced-logout dialog gate — behavior (b) of the four `MifosPayApp.kt`
 * shell concerns re-homed onto the `:cmp-navigation` template shell
 * (Phase 2 T4, `02-topology-reconciliation.md`).
 *
 * The dialog is rendered when [showUnauthorizedDialog] is `true`. On confirm,
 * [onForcedLogout] fires — the caller typically wires this to
 * `MifosPayViewModel.logOut()` + `GlobalAuthManager.reset()` + a navigation
 * back to the login graph. The route change itself is handled by
 * `RootNavViewModel` which already re-routes to [`RootNavState.Auth`] as soon
 * as `GlobalAuthManager.isUnauthorized` flips to `true` (see
 * `cmp.navigation.rootnav.RootNavViewModel`); this gate augments that route
 * change with the user-visible "Please login again to continue" notice so the
 * user isn't silently dumped back on the auth screen.
 *
 * The 401 signal originates in
 * `core/network/utils/KtorInterceptor` which calls
 * `GlobalAuthManager.markUnauthorized()` on any HTTP 401 response.
 *
 * The dialog copy is intentionally kept as literal English strings for parity
 * with the fork's `MifosPayApp` — hoisting into resources is a follow-up
 * cleanup (RULE-IMPL-NO-HARDCODED-STRING-001) that will land alongside the
 * broader shell i18n pass.
 *
 * @param showUnauthorizedDialog Bool observed by the caller from
 *        `AppViewModel.state.showUnauthorizedDialog` (which in turn observes
 *        `GlobalAuthManager.isUnauthorized`).
 * @param onForcedLogout Called on the "Ok" button — must clear the session,
 *        reset [org.mifospay.core.common.GlobalAuthManager], and dispatch to
 *        the login graph.
 * @param onDismiss No-op by default (the fork's dialog was non-dismissable
 *        outside of the "Ok" button; kept as an override for tests).
 */
@Composable
fun UnauthorizedDialogGate(
    showUnauthorizedDialog: Boolean,
    onForcedLogout: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    if (showUnauthorizedDialog) {
        MifosDialogBox(
            title = "Unauthorized User",
            showDialogState = true,
            confirmButtonText = "Ok",
            onConfirm = onForcedLogout,
            onDismiss = onDismiss,
            message = "Please login again to continue",
        )
    }
}
