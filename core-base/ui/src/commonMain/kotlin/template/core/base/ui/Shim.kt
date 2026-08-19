/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

/**
 * Foundation-migration shim (2026-08-01):
 *
 * Upstream KMP template renamed `template.core.base.ui.*` → `kpt.core.base.ui.*`
 * (with per-topic sub-packages: `.viewmodel.BaseViewModel`, `.nav.composableWithSlideTransitions`,
 * `.GestureDetector.detectMultiTapGesture`). The 12 fork consumers still import from
 * `template.core.base.ui.*` root — this shim re-exports each consumed symbol:
 * `BaseViewModel` via `typealias`, the two inline-reified nav helpers as `inline fun`
 * forwarders (reified type parameter carries through the inlined call site), and
 * `detectMultiTapGesture` as a Modifier extension forwarder.
 * Deletion tracker: safe to remove once every consumer imports from `kpt.core.base.ui.*`.
 */
@file:Suppress("PackageDirectoryMismatch", "ktlint:standard:filename")

package template.core.base.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType
import kpt.core.base.ui.detectMultiTapGesture as kptDetectMultiTapGesture
import kpt.core.base.ui.nav.composableWithSlideTransitions as kptComposableWithSlideTransitions
import kpt.core.base.ui.nav.composableWithStayTransitions as kptComposableWithStayTransitions

@Deprecated(
    message = "Use kpt.core.base.ui.viewmodel.BaseViewModel",
    replaceWith = ReplaceWith("kpt.core.base.ui.viewmodel.BaseViewModel"),
)
typealias BaseViewModel<S, E, A> = kpt.core.base.ui.viewmodel.BaseViewModel<S, E, A>

/**
 * Inline-reified forwarder — Kotlin propagates the reified [T] through the inlined
 * call site to the kpt target, so runtime type information is preserved.
 */
@Deprecated(
    message = "Use kpt.core.base.ui.nav.composableWithSlideTransitions",
    replaceWith = ReplaceWith(
        "kpt.core.base.ui.nav.composableWithSlideTransitions<T>(typeMap, deepLinks, content)",
    ),
)
inline fun <reified T : Any> NavGraphBuilder.composableWithSlideTransitions(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = kptComposableWithSlideTransitions<T>(typeMap, deepLinks, content)

@Deprecated(
    message = "Use kpt.core.base.ui.nav.composableWithStayTransitions",
    replaceWith = ReplaceWith(
        "kpt.core.base.ui.nav.composableWithStayTransitions<T>(typeMap, deepLinks, content)",
    ),
)
inline fun <reified T : Any> NavGraphBuilder.composableWithStayTransitions(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = kptComposableWithStayTransitions<T>(typeMap, deepLinks, content)

@Deprecated(
    message = "Use kpt.core.base.ui.detectMultiTapGesture",
    replaceWith = ReplaceWith(
        "kpt.core.base.ui.detectMultiTapGesture(tapCount, tapTimeoutMs, onGestureDetected)",
    ),
)
fun Modifier.detectMultiTapGesture(
    tapCount: Int = 5,
    tapTimeoutMs: Long = 1000L,
    onGestureDetected: () -> Unit,
): Modifier = this.kptDetectMultiTapGesture(tapCount, tapTimeoutMs, onGestureDetected)
