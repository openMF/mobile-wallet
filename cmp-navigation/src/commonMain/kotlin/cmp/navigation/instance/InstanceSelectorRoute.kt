/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("MatchingDeclarationName")

package cmp.navigation.instance

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * Typed nav route + destination + navigate helper for the fork-only Supabase
 * multi-instance selector — Phase 2 T5 of `02-topology-reconciliation.md`.
 *
 * The selector screen + its ViewModel + `SupabaseInstanceConfigLoader` are
 * preserved wholesale in
 * `cmp-shared/src/commonMain/kotlin/org/mifospay/shared/instance/`
 * (T5 explicitly forbids deleting `cmp-shared/instance`); this file is the
 * new **reachability entry-point** from the template-shape `:cmp-navigation`
 * nav graph.
 *
 * The concrete `InstanceSelectorScreen` composable is **caller-supplied** —
 * `:cmp-navigation` does not depend on `:cmp-shared` (`cmp-shared` already
 * depends on `cmp-navigation`; adding the reverse would be a Gradle cycle),
 * so the login graph in `cmp-shared` provides the screen via
 * [instanceSelectorDestination]'s `screen` lambda.
 *
 * Two integration paths remain supported (choose per host graph):
 *
 *  1. **Type-safe nav destination** ([InstanceSelectorRoute] +
 *     [instanceSelectorDestination] + [navigateToInstanceSelector]) — for the
 *     login-graph rewire in Phase 2 T2; drops in as a regular
 *     `composable<InstanceSelectorRoute> { ... }` alongside the login screens.
 *  2. **Bottom-sheet overlay** — preserved by
 *     `AppViewModel.state.showInstanceSelector` + `ComposeApp`'s
 *     `instanceSelectorOverlay` callback parameter; matches the fork's
 *     `RootNavGraph` in-place overlay pattern driven by the multi-tap gesture
 *     on `LoginScreen` (see `feature/auth/.../LoginScreen.kt:163`
 *     `detectMultiTapGesture`).
 *
 * Neither path mutates `InstanceSelectorScreen` itself — its dismiss callback
 * remains the single boundary the outer host controls.
 */
@Serializable
data object InstanceSelectorRoute

/**
 * Push the instance selector destination onto the current back stack.
 * Login-graph consumers call this from the multi-tap gesture handler (in
 * type-safe mode) or trigger the overlay path (in bottom-sheet mode).
 */
fun NavController.navigateToInstanceSelector(navOptions: NavOptions? = null) =
    navigate(route = InstanceSelectorRoute, navOptions = navOptions)

/**
 * Register the instance-selector destination under the type-safe
 * [InstanceSelectorRoute].
 *
 * @param screen Caller-supplied composable — the login graph in `:cmp-shared`
 *        passes `{ onDismiss -> InstanceSelectorScreen(onDismiss = onDismiss) }`.
 *        This inversion keeps the nav-registration API in `:cmp-navigation`
 *        without creating a `cmp-navigation → cmp-shared` cycle.
 */
fun NavGraphBuilder.instanceSelectorDestination(
    screen: @Composable (onDismiss: () -> Unit) -> Unit,
    onDismiss: () -> Unit,
) {
    composable<InstanceSelectorRoute> {
        screen(onDismiss)
    }
}
