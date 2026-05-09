/*
 * Copyright 2024 Mifos Initiative
 * ...license header...
 */
package org.mifospay.feature.kyc

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.KycLevelRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.kyc.KYCLevel1Details
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.kyc.KycLevel3Action.Internal.HandleLevel1Result

class KYCLevel3ViewModel(
    private val kycLevelRepository: KycLevelRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<KycLevel3State, KycLevel3Event, KycLevel3Action>(
    initialState = KycLevel3State(
        clientId = requireNotNull(userPreferencesRepository.clientId.value),
    ),
) {
    init {
        kycLevelRepository.fetchKYCLevel1Details(state.clientId)
            .onEach { sendAction(HandleLevel1Result(it)) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: KycLevel3Action) {
        when (action) {
            KycLevel3Action.NavigateBack -> sendEvent(KycLevel3Event.OnNavigateBack)
            KycLevel3Action.ConfirmAndSubmit -> handleConfirmSubmit()
            is HandleLevel1Result -> handleLevel1Result(action)
        }
    }

    private fun handleConfirmSubmit() {
        mutableStateFlow.update { it.copy(dialogState = KycLevel3State.DialogState.Loading) }
        // KYC Level 3 marks the current level as complete — update level field
        viewModelScope.launch {
            val details = state.kycDetails?.copy(currentLevel = KycLevel.KYC_LEVEL_3.name)
                ?: return@launch
            val result = kycLevelRepository.updateKYCLevel1Details(state.clientId, details)
            when (result) {
                is DataState.Success -> {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                    sendEvent(KycLevel3Event.OnKycComplete)
                }
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(dialogState = KycLevel3State.DialogState.Error(
                            result.exception.message ?: "Submission failed"
                        ))
                    }
                }
                else -> Unit
            }
        }
    }

    private fun handleLevel1Result(action: HandleLevel1Result) {
        when (action.result) {
            is DataState.Success -> mutableStateFlow.update {
                it.copy(kycDetails = action.result.data, isLoading = false)
            }
            is DataState.Loading -> mutableStateFlow.update { it.copy(isLoading = true) }
            else -> mutableStateFlow.update { it.copy(isLoading = false) }
        }
    }
}

data class KycLevel3State(
    val clientId: Long,
    val kycDetails: KYCLevel1Details? = null,
    val isLoading: Boolean = true,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}

sealed interface KycLevel3Event {
    data object OnNavigateBack : KycLevel3Event
    data object OnKycComplete : KycLevel3Event
}

sealed interface KycLevel3Action {
    data object NavigateBack : KycLevel3Action
    data object ConfirmAndSubmit : KycLevel3Action
    sealed interface Internal : KycLevel3Action {
        data class HandleLevel1Result(val result: DataState<KYCLevel1Details?>) : Internal
    }
}
