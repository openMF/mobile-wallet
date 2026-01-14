/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package cmp.navigation.rootnav

import androidx.lifecycle.viewModelScope
import cmp.navigation.rootnav.RootNavAction.Internal.UserStateUpdateReceive
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.mifospay.core.model.AuthState
import org.mifospay.core.data.repository.UserDataRepository
import org.mifospay.core.model.UserData
import template.core.base.ui.BaseViewModel

class RootNavViewModel(
    userDataRepository: UserDataRepository,
) : BaseViewModel<RootNavState, Unit, RootNavAction>(
    initialState = RootNavState.Splash,
) {

    init {
        userDataRepository.userData.map { userData ->
            UserStateUpdateReceive(
                authState = AuthState.Authenticated("sample-token"),
                userData = userData,
            )
        }.onEach(::handleAction)
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: RootNavAction) {
        when (action) {
            is UserStateUpdateReceive -> handleUserStateUpdateReceive(action)
        }
    }

    private fun handleUserStateUpdateReceive(
        action: UserStateUpdateReceive,
    ) {
        val userData = action.userData

        // TODO:: Configure this based on the user state
        val updatedRootNavState = when {
            userData.firstTimeUser -> RootNavState.ShowOnboarding

            !userData.isAuthenticated -> RootNavState.Auth

            userData.passcode.isEmpty() -> RootNavState.UserLocked

            userData.isUnlocked -> {
                RootNavState.UserUnlocked(userData.activeUserId)
            }

            else -> RootNavState.UserLocked
        }

        mutableStateFlow.update { updatedRootNavState }
    }
}

sealed class RootNavState {
    data object Auth : RootNavState()

    data object ShowOnboarding : RootNavState()

    data object Splash : RootNavState()

    data object UserLocked : RootNavState()

    data class UserUnlocked(
        val activeUserId: String,
    ) : RootNavState()
}

sealed class RootNavAction {

    sealed class Internal {

        data class UserStateUpdateReceive(
            val authState: AuthState,
            val userData: UserData,
        ) : RootNavAction()
    }
}
