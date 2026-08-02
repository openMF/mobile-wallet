/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

/**
 * Pops the back stack only when the current destination is fully [Lifecycle.State.RESUMED].
 *
 * Prevents the "double-back wipes nav graph" bug: after Phase 08 symmetrized exit transitions
 * to 450ms, a natural double-tap lands the second call while the screen is still exiting
 * (lifecycle = STARTED). Without this guard that second call pops past the parent destination
 * and leaves an empty dark gray nav graph.
 *
 * Use this instead of [NavController.popBackStack] everywhere in nav-graph builders and
 * Composable content blocks:
 *
 * ```kotlin
 * composableWithPushTransitions<MyRoute> {
 *     MyScreen(onBackClick = { navController.popBackStackSafely() })
 * }
 * ```
 *
 * For Composable call sites that also want a time-based debounce on top of the lifecycle
 * guard, use [rememberSafeBackPress] instead.
 */
fun NavController.popBackStackSafely() {
    if (currentBackStackEntry?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true) {
        popBackStack()
    }
}

/**
 * Returns a stable lambda that guards [NavController.popBackStack] with two layers:
 *
 * 1. **Lifecycle guard** — only fires when the current destination is [Lifecycle.State.RESUMED].
 *    Rejects taps that arrive while the exit animation is running (lifecycle = STARTED).
 * 2. **Time debounce** — rejects a second tap within [debounceMs] of the first, even if the
 *    destination somehow stays RESUMED (e.g. very fast animation, disabled transitions in tests).
 *
 * Prefer [popBackStackSafely] for non-Composable nav-graph-builder call sites (it needs no
 * `@Composable` context). Use this overload in `@Composable` screen bodies that want both
 * layers of protection.
 *
 * ```kotlin
 * @Composable
 * fun MyScreen(navController: NavController) {
 *     val onBack = navController.rememberSafeBackPress()
 *     KptTopAppBar(title = "…", onNavigationIconClick = onBack)
 * }
 * ```
 */
@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun NavController.rememberSafeBackPress(debounceMs: Long = 500L): () -> Unit {
    val navController = this
    val lastTapMark = remember { androidx.compose.runtime.mutableStateOf<kotlin.time.TimeMark?>(null) }
    return remember(navController, debounceMs) {
        action@{
            val entry = navController.currentBackStackEntry ?: return@action
            if (!entry.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return@action
            val mark = lastTapMark.value
            if (mark != null && mark.elapsedNow().inWholeMilliseconds < debounceMs) return@action
            lastTapMark.value = kotlin.time.TimeSource.Monotonic.markNow()
            navController.popBackStack()
        }
    }
}
