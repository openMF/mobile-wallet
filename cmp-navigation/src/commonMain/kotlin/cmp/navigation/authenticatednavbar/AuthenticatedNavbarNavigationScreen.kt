/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.authenticatednavbar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import cmp.navigation.registry.BackboneRegistry
import cmp.navigation.registry.TabRegistry
import cmp.navigation.ui.KptRootScaffold
import cmp.navigation.ui.ScaffoldNavigationData
import cmp.navigation.ui.logDestinationChanged
import cmp.navigation.ui.rememberKptNavController
import io.github.mobilebytelabs.kmptoolkit.firebase.compose.rememberAnalyticsHelper
import kotlinx.collections.immutable.toImmutableList
import kpt.core.base.designsystem.theme.motion
import kpt.core.base.ui.effects.EventsEffect
import kpt.core.base.ui.util.RootTransitionProviders
import kpt.feature.home.HomeDestination
import kpt.feature.home.homeGraph
import kpt.feature.home.navigateToHome
import kpt.feature.profile.navigateToProfile
import kpt.feature.profile.profileDestination
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AuthenticatedNavbarNavigationScreen(
    navigateToSettingsScreen: () -> Unit,
    homeBody: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberKptNavController(
        name = "AuthenticatedNavbarScreen",
    ),
    viewModel: AuthenticatedNavbarNavigationViewModel = koinViewModel(),
) {
    val analyticsHelper = rememberAnalyticsHelper()

    EventsEffect(eventFlow = viewModel.eventFlow) { event ->
        navController.apply {
            when (event) {
                AuthenticatedNavBarEvent.NavigateToHomeScreen -> {
                    analyticsHelper.logDestinationChanged(event.tab.startDestinationRoute)
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                        navigateToHome(navOptions = it)
                    }
                }

                AuthenticatedNavBarEvent.NavigateToProfileScreen -> {
                    analyticsHelper.logDestinationChanged(event.tab.startDestinationRoute)
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                        navigateToProfile(navOptions = it)
                    }
                }
            }
        }
    }

    AuthenticatedNavbarNavigationScreenContent(
        navController = navController,
        modifier = modifier,
        navigateToSettingsScreen = navigateToSettingsScreen,
        homeBody = homeBody,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
internal fun AuthenticatedNavbarNavigationScreenContent(
    navController: NavHostController,
    navigateToSettingsScreen: () -> Unit,
    homeBody: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (AuthenticatedNavBarAction) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Tabs from the fork-owned TabRegistry seam (backbone Home/Profile + fork extraTabs) — the
    // navbar renders the list generically instead of a hardcoded item list (S6 heal, T7).
    val navigationItems = TabRegistry.tabs.toImmutableList()

    KptRootScaffold(
        contentWindowInsets = WindowInsets(0.dp),
        navigationData = ScaffoldNavigationData(
            navigationItems = navigationItems,
            selectedNavigationItem = navigationItems.find {
                navBackStackEntry.isCurrentRoute(route = it.graphRoute)
            },
            onNavigationClick = { navigationItem ->
                when (navigationItem) {
                    is AuthenticatedNavBarTabItem.HomeTab -> {
                        onAction(AuthenticatedNavBarAction.HomeTabClick)
                    }

                    is AuthenticatedNavBarTabItem.ProfileTab -> {
                        onAction(AuthenticatedNavBarAction.SettingsTabClick)
                    }

                    // Fork extraTabs (TabRegistry.extraTabs) own their own navigation; the backbone
                    // navbar has no built-in action for them.
                    else -> Unit
                }
            },
            shouldShowNavigation = navigationItems.any {
                navBackStackEntry.isCurrentRoute(route = it.startDestinationRoute)
            },
        ),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier,
    ) {
        // Because this Scaffold has a bottom navigation bar, the NavHost will:
        // - consume the vertical navigation bar insets.
        // - consume the IME insets.
        // Snapshot motion tokens once so the non-Composable enterTransition lambdas capture
        // theme-resolved values rather than the hardcoded fallbacks.
        val motion = MaterialTheme.motion
        NavHost(
            navController = navController,
            startDestination = HomeDestination,
            // Sibling navigation (bottom-nav tab switch) uses M3 fade-through pattern.
            enterTransition = RootTransitionProviders.Kpt.Enter.fadeThrough(motion),
            exitTransition = RootTransitionProviders.Kpt.Exit.fadeThrough(motion),
            popEnterTransition = RootTransitionProviders.Kpt.Enter.fadeThrough(motion),
            popExitTransition = RootTransitionProviders.Kpt.Exit.fadeThrough(motion),
        ) {
            // TOP LEVEL DESTINATIONS
            homeGraph(
                onSettingsClick = navigateToSettingsScreen,
                homeBody = homeBody,
            )

            // Profile inner content from the fork-owned BackboneRegistry.profileBody seam (default: demo
            // body). The template shell forwards this opaque body — a fork edits BackboneRegistry, not this
            // file. (WS01 base-feature seam, epic AC7 — mirrors the homeBody wiring.)
            profileDestination(profileBody = { BackboneRegistry.profileBody(navController) })
        }
    }
}

private fun NavController.navigateToTabOrRoot(
    tabToNavigateTo: AuthenticatedNavBarTabItem,
    navigate: (NavOptions) -> Unit,
) {
    if (tabToNavigateTo.startDestinationRoute == currentDestination?.route) {
        return
    } else if (currentDestination?.parent?.route == tabToNavigateTo.graphRoute) {
        popBackStack(route = tabToNavigateTo.startDestinationRoute, inclusive = false)
    } else {
        navigate(
            navOptions {
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            },
        )
    }
}

private fun NavBackStackEntry?.isCurrentRoute(route: String): Boolean = this
    ?.destination
    ?.hierarchy
    ?.any { it.route == route } == true
