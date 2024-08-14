/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.feature.kyc.KYCLevel3UiState.Loading
import javax.inject.Inject

@HiltViewModel
@Suppress("UnusedPrivateProperty")
class KYCLevel3ViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
) : ViewModel() {
    private val kycUiState = MutableStateFlow<KYCLevel3UiState>(Loading)
    val kyc3uiState: StateFlow<KYCLevel3UiState> = kycUiState

    // Todo: Implement KYCLevel3ViewModel flow
}

sealed interface KYCLevel3UiState {
    data object Loading : KYCLevel3UiState
    data object Success : KYCLevel3UiState
    data object Error : KYCLevel3UiState
}
