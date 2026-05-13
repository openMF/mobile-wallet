/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeStorageAdapter
import org.mifospay.core.data.repository.AppLockRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.user.UserInfo

class MifosPayViewModel(
    private val userDataRepository: UserPreferencesRepository,
    private val passcodeManager: PasscodeManager,
    private val appLockRepository: AppLockRepository,
    private val passcodeStorageAdapter: PasscodeStorageAdapter,
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = userDataRepository.userInfo.map {
        MainUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    fun isPasscodeCreated(): Boolean {
        return !passcodeStorageAdapter.loadPasscode().isNullOrBlank()
    }

    fun logOut() {
        viewModelScope.launch {
            userDataRepository.logOut()
            appLockRepository.deleteLock()
            passcodeManager.logOut()
        }
    }
    fun isAppUnlocked(): Boolean {
        return !appLockRepository.isAppLocked()
    }
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val userData: UserInfo) : MainUiState
}
