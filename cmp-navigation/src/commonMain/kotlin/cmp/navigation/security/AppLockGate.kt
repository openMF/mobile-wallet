/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package cmp.navigation.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Background re-auth lock gate — behavior (a) of the four `MifosPayApp.kt`
 * shell concerns re-homed onto the `:cmp-navigation` template shell (Phase 2 T4,
 * `02-topology-reconciliation.md`).
 *
 * This composable is a **thin lifecycle observer** — it forwards
 * `Lifecycle.Event.ON_STOP` to [onBackgrounded] and `Lifecycle.Event.ON_RESUME`
 * to [onForegrounded]. All qualifying logic (elapsed-time threshold,
 * app-lock snapshot, passcode-step snapshot, `TimeSource.Monotonic` marker,
 * event emission) lives in [cmp.navigation.AppViewModel] so the state
 * (`lastStopMark`) survives config-change UI rebuilds without a `remember`
 * shortcut and so the check happens on the VM thread rather than the UI
 * composition callback.
 *
 * The fork's original implementation in `MifosPayApp.kt` co-located the
 * timer state with the observer via `remember { mutableStateOf<...>(...) }`;
 * that pattern loses the mark on any composition recreate (rotation, theme
 * change) — moving the state into the VM fixes that class of bug in the
 * template shell without changing observable behavior.
 *
 * @param onBackgrounded Invoked on `Lifecycle.Event.ON_STOP` — the VM records
 *        the monotonic-clock mark for the paired resume.
 * @param onForegrounded Invoked on `Lifecycle.Event.ON_RESUME` — the VM
 *        evaluates the three qualifying conditions and, if satisfied, emits
 *        `AppEvent.ReAuthRequired`.
 */
@Composable
fun AppLockGate(
    onBackgrounded: () -> Unit,
    onForegrounded: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> onBackgrounded()
                Lifecycle.Event.ON_RESUME -> onForegrounded()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}
