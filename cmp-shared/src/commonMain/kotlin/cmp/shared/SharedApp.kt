/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cmp.navigation.AppViewModel
import cmp.navigation.ComposeApp
import coil3.compose.LocalPlatformContext
import kpt.core.base.platform.LocalManagerProvider
import kpt.core.base.platform.context.LocalContext
import kpt.core.base.ui.util.LocalImageLoaderProvider
import kpt.core.base.ui.util.getDefaultImageLoader
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor
import org.mifospay.shared.MifosPayViewModel
import org.mifospay.shared.instance.InstanceSelectorScreen
import org.mifospay.shared.navigation.MifosNavGraph
import org.mifospay.shared.navigation.loginNavGraph
import org.mifospay.shared.ui.MifosApp

/**
 * Fork-shell wrapper around the template's [ComposeApp].
 *
 * Bridges the two directions of the Phase 2 topology reconciliation:
 *
 *  - The **template shell** (`:cmp-navigation` → `ComposeApp` → `RootNavScreen`)
 *    owns the app-lock timer state, 401 dialog rendering, and the outer nav
 *    graph shape (Splash / Auth / Authenticated) — Phase 2 T4.
 *  - The **fork shell** contributes:
 *      * concrete session-clear ([MifosPayViewModel.logOut]),
 *      * Supabase multi-instance selector ([InstanceSelectorScreen], Phase 2 T5),
 *      * the login/auth nav graph ([loginNavGraph], registered inside
 *        `RootNavScreen`'s NavHost via the `authGraphBuilder` callback),
 *      * the full authenticated content area ([MifosApp] with its own Scaffold
 *        + 4-tab bottom nav + [org.mifospay.shared.navigation.MifosNavHost]
 *        holding all ~60 fork feature destinations).
 *
 * `SharedApp` composes both here — since `:cmp-shared` already depends on
 * `:cmp-navigation` (the `ComposeApp` import above), the fork VMs / screens /
 * graphs are passed to `ComposeApp` as **callback parameters** rather than
 * pulled in through a reverse-direction Gradle dependency (which would be a
 * cycle; see `cmp-navigation/build.gradle.kts` comments).
 *
 * Phase 2 nav-chunk (skeleton) — the three new callbacks
 * (`authGraphBuilder`, `authGraphRoute`, `authenticatedContent`) added to
 * `ComposeApp` this chunk fill the remaining wiring:
 *  - `authGraphBuilder = { nav -> loginNavGraph(nav, onShowInstanceSelector = ...) }`
 *    plugs the fork's LOGIN_GRAPH into the template's outer NavHost.
 *  - `authGraphRoute = MifosNavGraph.LOGIN_GRAPH` tells `RootNavScreen` where
 *    to navigate on `RootNavState.Auth`.
 *  - `authenticatedContent = { MifosApp(...) }` bridges the fork's ENTIRE
 *    authenticated area — scaffold, bottom nav, top-app-bar, `MifosNavHost` —
 *    as the sole composable rendered inside the template's
 *    `AuthenticatedNavbarRoute` slot. Per-feature typed-route conversion of
 *    the ~60 destinations follows in later chunks.
 */
@Composable
fun SharedApp(
    updateScreenCapture: (isScreenCaptureAllowed: Boolean) -> Unit,
    handleRecreate: () -> Unit,
    handleThemeMode: (osValue: Int) -> Unit,
    handleAppLocale: (locale: String?) -> Unit,
    modifier: Modifier = Modifier,
    onSplashScreenRemoved: () -> Unit,
) {
    // Fork session VM — resolves via the fork `sharedModule` Koin binding.
    // Phase 2 T6/T7 wire the fork's Koin modules into
    // `cmp.navigation.di.KoinModules.allModules`; until then this resolution
    // may fail at composition (correct signal, not a masked stub).
    val mifosPayViewModel: MifosPayViewModel = koinViewModel()

    // Same AppViewModel singleton that `ComposeApp` / `RootNavScreen` resolve;
    // needed here so `loginNavGraph(onShowInstanceSelector = ...)` receives the
    // sheet-toggle wired to the SAME state the ComposeApp overlay observes.
    val appViewModel: AppViewModel = koinViewModel()

    // MifosApp requires network/time-zone monitors — the same singletons the
    // legacy `MifosPaySharedApp` resolves. Koin returns the process singletons.
    val networkMonitor: NetworkMonitor = koinInject()
    val timeZoneMonitor: TimeZoneMonitor = koinInject()

    LocalManagerProvider(LocalContext.current) {
        LocalImageLoaderProvider(getDefaultImageLoader(LocalPlatformContext.current)) {
            ComposeApp(
                updateScreenCapture = updateScreenCapture,
                handleRecreate = handleRecreate,
                handleThemeMode = handleThemeMode,
                handleAppLocale = handleAppLocale,
                onSplashScreenRemoved = onSplashScreenRemoved,
                modifier = modifier,
                // (b) fan-out for `AppEvent.LogoutRequested` — clears session,
                // app-lock flag, and passcode in one atomic call. See
                // `MifosPayViewModel.logOut()` kdoc for the three-step order.
                onSessionLogOut = { mifosPayViewModel.logOut() },
                // (a) route target for `AppEvent.ReAuthRequired`. Now a no-op
                // at THIS layer — `RootNavScreen` handles the navigation
                // directly via its own `EventsEffect` (which has scope to the
                // NavHostController that `rememberKptNavController` creates
                // inside it). The callback remains for backward compat / for
                // any consumer that wants an additional side-effect at the
                // outer shell layer.
                onReAuthRequired = { /* handled inside RootNavScreen */ },
                // T5 — preserve the fork's Supabase multi-instance selector as
                // a bottom-sheet overlay reachable via the login screen's
                // multi-tap gesture. The overlay renders `InstanceSelectorScreen`
                // (unchanged fork code); its dismiss callback flips
                // `AppViewModel.showInstanceSelector` back to `false`.
                instanceSelectorOverlay = { onDismiss ->
                    InstanceSelectorScreen(onDismiss = onDismiss)
                },
                // (Task item 3) Login/auth graph injection. `loginNavGraph`
                // (internal to `:cmp-shared`) registers its own
                // `route = MifosNavGraph.LOGIN_GRAPH` nested nav graph
                // containing `loginScreen`, `signupMethodScreen`,
                // `signupScreen`, `mobileVerificationScreen`. The
                // `onShowInstanceSelector` lambda toggles
                // `AppState.showInstanceSelector` — the SAME state
                // `instanceSelectorOverlay` above observes.
                authGraphBuilder = { navController ->
                    loginNavGraph(
                        navController = navController,
                        onShowInstanceSelector = appViewModel::showInstanceSelectorSheet,
                    )
                },
                authGraphRoute = MifosNavGraph.LOGIN_GRAPH,
                // (Task items 1+2) Authenticated content wholesale bridge.
                // `MifosApp` composes its own Scaffold with:
                //   * MifosBottomBar / MifosNavRail with HOME/PAYMENTS/FINANCE/HISTORY
                //     tabs (`org.mifospay.shared.utils.TopLevelDestination`),
                //   * MifosAppBar top-app-bar,
                //   * MifosNavHost — the ~1000-line NavHost registering all
                //     ~60 fork feature destinations as string-route composables
                //     (see `cmp-shared/.../MifosNavHost.kt`).
                // The `onClickLogout` proxies to the fork's session-clear VM.
                authenticatedContent = {
                    MifosApp(
                        networkMonitor = networkMonitor,
                        timeZoneMonitor = timeZoneMonitor,
                        onClickLogout = { mifosPayViewModel.logOut() },
                        handleAppLocale = handleAppLocale,
                    )
                },
            )
        }
    }
}
