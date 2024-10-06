/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import mobile_wallet.mifospay_shared.generated.resources.Res
import mobile_wallet.mifospay_shared.generated.resources.faq
import mobile_wallet.mifospay_shared.generated.resources.not_connected
import mobile_wallet.mifospay_shared.generated.resources.settings
import org.jetbrains.compose.resources.stringResource
import org.mifospay.core.designsystem.component.MifosBackground
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosNavigationBar
import org.mifospay.core.designsystem.component.MifosNavigationBarItem
import org.mifospay.core.designsystem.component.MifosNavigationRail
import org.mifospay.core.designsystem.component.MifosNavigationRailItem
import org.mifospay.core.designsystem.component.MifosTopAppBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.GradientColors
import org.mifospay.core.designsystem.theme.LocalGradientColors
import org.mifospay.shared.navigation.MifosNavHost
import org.mifospay.shared.utils.TopLevelDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MifosApp(
    appState: MifosAppState,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shouldShowGradientBackground =
        appState.currentTopLevelDestination == TopLevelDestination.HOME
    var showHomeMenuOption by rememberSaveable { mutableStateOf(false) }

    MifosBackground(modifier) {
        MifosGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            val snackbarHostState = remember { SnackbarHostState() }

            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            // If user is not connected to the internet show a snack bar to inform them.
            val notConnectedMessage = stringResource(Res.string.not_connected)
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = Indefinite,
                    )
                }
            }

            AnimatedVisibility(showHomeMenuOption) {
                HomeMenu(
                    showHomeMenuOption = showHomeMenuOption,
                    onDismissRequest = { showHomeMenuOption = false },
                    onClickLogout = onClickLogout,
                    onNavigateToFaq = {},
                    onNavigateToSettings = {},
                )
            }

            Scaffold(
                modifier = Modifier,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        MifosBottomBar(
                            destinations = appState.topLevelDestinations,
                            destinationsWithUnreadResources = emptySet(),
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination,
                            modifier = Modifier.testTag("NiaBottomBar"),
                        )
                    }
                },
            ) { padding ->
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal,
                            ),
                        ),
                ) {
                    if (appState.shouldShowNavRail) {
                        MifosNavRail(
                            destinations = appState.topLevelDestinations,
                            destinationsWithUnreadResources = emptySet(),
                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentDestination,
                            modifier =
                            Modifier
                                .testTag("NiaNavRail")
                                .safeDrawingPadding(),
                        )
                    }

                    Column(Modifier.fillMaxSize()) {
                        // Show the top app bar on top level destinations.
                        val destination = appState.currentTopLevelDestination
                        if (destination != null) {
                            MifosTopAppBar(
                                titleRes = stringResource(destination.titleText),
                                actionIcon = MifosIcons.MoreVert,
                                actionIconContentDescription = stringResource(
                                    destination.titleText,
                                ),
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Transparent,
                                ),
                                onActionClick = { showHomeMenuOption = true },
                            )
                        }

                        MifosNavHost(
                            appState = appState,
                            onClickLogout = onClickLogout,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeMenu(
    showHomeMenuOption: Boolean,
    onDismissRequest: () -> Unit,
    onClickLogout: () -> Unit,
    onNavigateToFaq: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
            .padding(end = 24.dp)
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        DropdownMenu(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
            expanded = showHomeMenuOption,
            onDismissRequest = onDismissRequest,
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.faq),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                onClick = {
                    onDismissRequest()
                    onNavigateToFaq()
                },
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.settings),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                onClick = {
                    onDismissRequest()
                    onNavigateToSettings()
                },
            )

            // TODO:: this could be removed, just added for testing
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Logout",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                onClick = {
                    onDismissRequest()
                    onClickLogout()
                },
            )
        }
    }
}

@Composable
private fun MifosNavRail(
    destinations: List<TopLevelDestination>,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    MifosNavigationRail(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            val hasUnread = destinationsWithUnreadResources.contains(destination)
            MifosNavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.iconText)) },
            )
        }
    }
}

@Composable
private fun MifosBottomBar(
    destinations: List<TopLevelDestination>,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    MifosNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            val hasUnread = destinationsWithUnreadResources.contains(destination)
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            MifosNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.iconText)) },
            )
        }
    }
}

private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // This is based on the dimensions of the NavigationBar's "indicator pill";
                // however, its parameters are private, so we must depend on them implicitly
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                center =
                center +
                    Offset(
                        64.dp.toPx() * .45f,
                        32.dp.toPx() * -.45f - 6.dp.toPx(),
                    ),
            )
        }
    }

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false
