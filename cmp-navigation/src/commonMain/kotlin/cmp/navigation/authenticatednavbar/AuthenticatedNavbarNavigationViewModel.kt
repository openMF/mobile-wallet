/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package cmp.navigation.authenticatednavbar

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mifospay.core.data.repository.NetworkMonitor
import org.mifospay.core.model.UserData
import template.core.base.ui.BaseViewModel

internal class AuthenticatedNavbarNavigationViewModel(
    networkMonitor: NetworkMonitor,
) : BaseViewModel<Unit, AuthenticatedNavBarEvent, AuthenticatedNavBarAction>(
    initialState = Unit,
) {

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    override fun handleAction(action: AuthenticatedNavBarAction) {
        when (action) {
            AuthenticatedNavBarAction.SettingsTabClick -> handleSettingsTabClicked()
            AuthenticatedNavBarAction.HomeTabClick -> handleHomeTabClicked()
            is AuthenticatedNavBarAction.Internal -> handleInternalAction(action)
        }
    }

    private fun handleInternalAction(action: AuthenticatedNavBarAction.Internal) {
        when (action) {
            is AuthenticatedNavBarAction.Internal.UserStateUpdateReceive -> {
            }
        }
    }

    private fun handleHomeTabClicked() {
        sendEvent(AuthenticatedNavBarEvent.NavigateToHomeScreen)
    }

    private fun handleSettingsTabClicked() {
        sendEvent(AuthenticatedNavBarEvent.NavigateToProfileScreen)
    }
}

internal sealed class AuthenticatedNavBarAction {
    data object HomeTabClick : AuthenticatedNavBarAction()

    data object SettingsTabClick : AuthenticatedNavBarAction()

    sealed class Internal : AuthenticatedNavBarAction() {
        data class UserStateUpdateReceive(val userState: UserData?) : Internal()
    }
}

internal sealed class AuthenticatedNavBarEvent {

    abstract val tab: AuthenticatedNavBarTabItem

    data object NavigateToHomeScreen : AuthenticatedNavBarEvent() {
        override val tab: AuthenticatedNavBarTabItem = AuthenticatedNavBarTabItem.HomeTab
    }

    data object NavigateToProfileScreen : AuthenticatedNavBarEvent() {
        override val tab: AuthenticatedNavBarTabItem = AuthenticatedNavBarTabItem.ProfileTab
    }
}
