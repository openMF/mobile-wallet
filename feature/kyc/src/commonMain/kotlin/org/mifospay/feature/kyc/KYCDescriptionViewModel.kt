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

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.KycLevelRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.utils.BaseViewModel

private const val KEY_STATE = "kyc_state"

class KYCDescriptionViewModel(
    private val repository: UserPreferencesRepository,
    kycLevelRepository: KycLevelRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<KycState, KycEvent, KycAction>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val clientId = requireNotNull(repository.clientId.value)
        KycState(clientId = clientId)
    },
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val kycState = kycLevelRepository.fetchKYCLevel1Details(state.clientId).mapLatest { result ->
        when (result) {
            is DataState.Loading -> {
                KYCDescriptionUiState.Loading
            }

            is DataState.Error -> {
                KYCDescriptionUiState.Error
            }

            is DataState.Success -> {
                val currentLevel = result.data?.let {
                    KycLevel.valueOf(it.currentLevel)
                }

                KYCDescriptionUiState.Content(currentLevel)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = KYCDescriptionUiState.Loading,
    )

    init {
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: KycAction) {
        when (action) {
            KycAction.Level1Clicked -> {
                sendEvent(KycEvent.OnLevel1Clicked)
            }

            KycAction.Level2Clicked -> {
                sendEvent(KycEvent.OnLevel2Clicked)
            }

            KycAction.Level3Clicked -> {
                sendEvent(KycEvent.OnLevel3Clicked)
            }
        }
    }
}

@Parcelize
data class KycState(
    val clientId: Long,
) : Parcelable

sealed interface KYCDescriptionUiState {
    data class Content(val currentLevel: KycLevel?) : KYCDescriptionUiState
    data object Error : KYCDescriptionUiState
    data object Loading : KYCDescriptionUiState
}

enum class KycLevel(
    val level: Int,
    val title: String,
    val icon: ImageVector,
) {
    KYC_LEVEL_1(level = 1, title = "Basic Details", icon = MifosIcons.Person),
    KYC_LEVEL_2(level = 2, title = "Upload Documents", icon = MifosIcons.Badge),
    KYC_LEVEL_3(level = 3, title = "Review & Submit", icon = MifosIcons.DataInfo),
}

sealed interface KycEvent {
    data object OnLevel1Clicked : KycEvent
    data object OnLevel2Clicked : KycEvent
    data object OnLevel3Clicked : KycEvent
}

sealed interface KycAction {
    data object Level1Clicked : KycAction
    data object Level2Clicked : KycAction
    data object Level3Clicked : KycAction
}
