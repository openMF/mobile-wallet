/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.beneficiary.addupdatebeneficiary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import mobile_wallet.feature.beneficiary.generated.resources.Res
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_account_type_other
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_account_type_wallet
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_button_save
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_button_update
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_error_empty_account_number
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_error_empty_beneficiary_name
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_error_empty_office_name
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_error_invalid_transfer_limit
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_error_select_account_type
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_error_select_locale
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_title_add
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_title_update
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.data.repository.LocalAssetRepository
import org.mifospay.core.data.repository.OfficeRepository
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.model.office.Office
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.core.model.utils.QrCodeType
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.beneficiary.addupdatebeneficiary.AEBAction.Internal.HandleBeneficiaryAddEditResult
import org.mifospay.feature.beneficiary.addupdatebeneficiary.AEBState.Companion.DEFAULT_OFFICE
import org.mifospay.feature.beneficiary.addupdatebeneficiary.AEBState.DialogState.Error

@OptIn(ExperimentalCoroutinesApi::class)
internal class AddEditBeneficiaryViewModel(
    private val localAssetRepository: LocalAssetRepository,
    private val repository: SelfServiceRepository,
    private val officeRepository: OfficeRepository,
    private val json: Json,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AEBState, AEBEvent, AEBAction>(
    initialState = savedStateHandle.getSerialized(ADD_EDIT_BENEFICIARY_KEY) ?: run {
        when (val addEditType = BeneficiaryAddEditArgs(savedStateHandle).addEditType) {
            is BeneficiaryAddEditType.AddItem -> {
                // Parse sourceQrData if available (for post-add navigation)
                val sourceQrData = addEditType.sourceQrData?.let { qrDataJson ->
                    try {
                        json.decodeFromString(QrCodeData.serializer(), qrDataJson)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (addEditType.beneficiary != null) {
                    // Pre-filled from QR scan
                    val beneficiary = json.decodeFromString(
                        Beneficiary.serializer(),
                        addEditType.beneficiary,
                    )
                    AEBState(
                        name = beneficiary.name,
                        accountNumber = beneficiary.accountNumber,
                        transferLimit = beneficiary.transferLimit,
                        officeName = beneficiary.officeName,
                        officeId = beneficiary.officeId,
                        addEditType = addEditType,
                        sourceQrType = addEditType.sourceQrType,
                        sourceQrData = sourceQrData,
                    )
                } else {
                    // Empty form for manual add
                    AEBState(
                        name = "",
                        accountNumber = "",
                        transferLimit = 0,
                        addEditType = addEditType,
                        sourceQrType = addEditType.sourceQrType,
                        sourceQrData = sourceQrData,
                    )
                }
            }

            is BeneficiaryAddEditType.EditItem -> {
                val beneficiary = json.decodeFromString(
                    Beneficiary.serializer(),
                    addEditType.beneficiary,
                )

                AEBState(
                    name = beneficiary.name,
                    accountNumber = beneficiary.accountNumber,
                    transferLimit = beneficiary.transferLimit,
                    officeName = beneficiary.officeName,
                    beneficiaryId = beneficiary.id,
                    addEditType = addEditType,
                )
            }
        }
    },
) {

    companion object {
        private const val ADD_EDIT_BENEFICIARY_KEY = "AddEditBeneficiaryViewModel"
    }

    val filteredLocalList = localAssetRepository.localeList.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val officeList = officeRepository.getOffices()
        .mapLatest { dataState ->
            when (dataState) {
                is DataState.Success -> dataState.data.ifEmpty { listOf(DEFAULT_OFFICE) }
                else -> listOf(DEFAULT_OFFICE)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf(DEFAULT_OFFICE),
        )

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = ADD_EDIT_BENEFICIARY_KEY, value = it) }
            .launchIn(viewModelScope)

        // Resolve office name from officeId when office list is loaded
        officeList
            .onEach { offices ->
                val currentState = state
                val officeId = currentState.officeId
                // Only resolve if officeId is set and current officeName is the fallback
                if (officeId != null && currentState.officeName == DEFAULT_OFFICE.name) {
                    val resolvedOffice = offices.find { it.id == officeId }
                    if (resolvedOffice != null) {
                        mutableStateFlow.update { it.copy(officeName = resolvedOffice.name) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: AEBAction) {
        when (action) {
            is AEBAction.ChangeName -> {
                mutableStateFlow.update {
                    it.copy(name = action.name)
                }
            }

            is AEBAction.ChangeTransferLimit -> {
                mutableStateFlow.update {
                    it.copy(transferLimit = action.transferLimit.toIntOrNull() ?: 0)
                }
            }

            is AEBAction.ChangeAccountNumber -> {
                mutableStateFlow.update {
                    it.copy(accountNumber = action.accountNumber)
                }
            }

            is AEBAction.ChangeAccountType -> {
                mutableStateFlow.update {
                    it.copy(accountType = action.accountType)
                }
            }

            is AEBAction.ChangeLocale -> {
                mutableStateFlow.update {
                    it.copy(locale = action.locale)
                }
            }

            is AEBAction.ChangeOfficeName -> {
                mutableStateFlow.update {
                    it.copy(officeName = action.officeName)
                }
            }

            AEBAction.NavigateBack -> {
                sendEvent(AEBEvent.NavigateBack)
            }

            AEBAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            AEBAction.SaveBeneficiary -> initiateSaveBeneficiary()

            AEBAction.OnQrScanClicked -> sendEvent(AEBEvent.NavigateToQr)

            is HandleBeneficiaryAddEditResult -> handleBeneficiaryAddEditResult(action)
        }
    }

    private fun initiateSaveBeneficiary() = when {
        state.name.isBlank() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_beneficiary_error_empty_beneficiary_name))
            }
        }

        state.accountNumber.isBlank() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_beneficiary_error_empty_account_number))
            }
        }

        state.transferLimit <= 0 -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_beneficiary_error_invalid_transfer_limit))
            }
        }

        state.locale.isBlank() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_beneficiary_error_select_locale))
            }
        }

        state.officeName.isBlank() -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_beneficiary_error_empty_office_name))
            }
        }

        state.accountType == 0 -> {
            mutableStateFlow.update {
                it.copy(dialogState = Error.ResourceMessage(Res.string.feature_beneficiary_error_select_account_type))
            }
        }

        else -> handleSaveBeneficiary()
    }

    private fun handleSaveBeneficiary() {
        mutableStateFlow.update {
            it.copy(dialogState = AEBState.DialogState.Loading)
        }

        when (state.addEditType) {
            is BeneficiaryAddEditType.AddItem -> {
                val payload = BeneficiaryPayload(
                    name = state.name,
                    accountNumber = state.accountNumber,
                    transferLimit = state.transferLimit,
                    locale = state.locale,
                    officeName = state.officeName,
                    accountType = state.accountType,
                )

                viewModelScope.launch {
                    val result = repository.createBeneficiary(payload)

                    sendAction(HandleBeneficiaryAddEditResult(result))
                }
            }

            is BeneficiaryAddEditType.EditItem -> {
                val payload = BeneficiaryUpdatePayload(
                    name = state.name,
                    transferLimit = state.transferLimit,
                )

                viewModelScope.launch {
                    val beneficiaryId = state.beneficiaryId ?: return@launch

                    val result = repository.updateBeneficiary(beneficiaryId, payload)

                    sendAction(HandleBeneficiaryAddEditResult(result))
                }
            }
        }
    }

    private fun handleBeneficiaryAddEditResult(action: HandleBeneficiaryAddEditResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = AEBState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                val message = extractMifosErrorMessage(action.result.exception)
                mutableStateFlow.update {
                    it.copy(dialogState = Error.StringMessage(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
                sendEvent(AEBEvent.ShowToast(action.result.data))

                // Navigate based on source QR type and QR data
                val qrData = state.sourceQrData
                when (state.sourceQrType) {
                    QrCodeType.INTRA_BANK -> {
                        if (qrData != null) {
                            // Use full QR data for navigation to TransferConfirm
                            sendEvent(
                                AEBEvent.NavigateToIntraBankTransfer(
                                    officeId = qrData.officeId.toInt(),
                                    clientId = qrData.clientId,
                                    accountTypeId = qrData.accountTypeId.toInt(),
                                    accountId = qrData.accountId.toInt(),
                                    amount = qrData.amount.toIntOrNull() ?: 0,
                                    accountName = qrData.clientName,
                                    accountNo = qrData.accountNo,
                                ),
                            )
                        } else {
                            // Fallback: no QR data available, go back
                            sendEvent(AEBEvent.NavigateBack)
                        }
                    }
                    QrCodeType.INTER_BANK -> {
                        sendEvent(
                            AEBEvent.NavigateToInterbankTransfer(
                                accountNumber = state.accountNumber,
                                recipientName = state.name,
                            ),
                        )
                    }
                    else -> {
                        // No QR source or other types - just go back
                        sendEvent(AEBEvent.NavigateBack)
                    }
                }
            }
        }
    }

    /**
     * Extract readable error message from MifosError JSON structure.
     *
     * Server errors come as JSON: {"defaultUserMessage":"...", ...}
     * This extracts the user-friendly message instead of showing raw JSON.
     */
    private fun extractMifosErrorMessage(exception: Throwable): String {
        val rawMessage = exception.message ?: return "Unknown error"

        // Try to parse MifosError JSON structure
        return try {
            // Pattern: {"defaultUserMessage":"...", ...}
            val regex = """"defaultUserMessage"\s*:\s*"([^"]+)"""".toRegex()
            regex.find(rawMessage)?.groupValues?.get(1) ?: rawMessage
        } catch (e: Exception) {
            rawMessage
        }
    }
}

@Serializable
internal data class AEBState(
    val addEditType: BeneficiaryAddEditType,
    val name: String,
    val accountNumber: String,
    val transferLimit: Int,
    val locale: String = "en_US",
    val officeName: String = DEFAULT_OFFICE.name,
    val officeId: Long? = null,
    val accountType: Int = SAVINGS_ACC_ID,
    val beneficiaryId: Long? = null,
    val sourceQrType: QrCodeType? = null,
    val sourceQrData: QrCodeData? = null,
    @Transient
    val dialogState: DialogState? = null,
) {
    private val isAddMode: Boolean
        get() = addEditType is BeneficiaryAddEditType.AddItem

    val btnText: StringResource
        get() = if (isAddMode) {
            Res.string.feature_beneficiary_button_save
        } else {
            Res.string.feature_beneficiary_button_update
        }

    val title: StringResource
        get() = if (isAddMode) {
            Res.string.feature_beneficiary_title_add
        } else {
            Res.string.feature_beneficiary_title_update
        }

    val accountTypeName: StringResource
        get() = if (accountType == SAVINGS_ACC_ID) {
            Res.string.feature_beneficiary_account_type_wallet
        } else {
            Res.string.feature_beneficiary_account_type_other
        }

    sealed interface DialogState {
        data object Loading : DialogState
        sealed interface Error : DialogState {
            data class StringMessage(val message: String) : Error
            data class ResourceMessage(val message: StringResource) : Error
        }
    }

    companion object {
        const val SAVINGS_ACC_ID = 2
        val DEFAULT_OFFICE = Office(id = 1, name = "Head Office", nameDecorated = "Head Office")
    }
}

internal sealed interface AEBEvent {
    data object NavigateBack : AEBEvent
    data object NavigateToQr : AEBEvent
    data class ShowToast(val message: String) : AEBEvent

    /**
     * Navigate to intra-bank transfer screen after successfully adding beneficiary from QR scan.
     * Contains all QR data needed for navigating to TransferConfirm screen.
     */
    data class NavigateToIntraBankTransfer(
        val officeId: Int,
        val clientId: Long,
        val accountTypeId: Int,
        val accountId: Int,
        val amount: Int,
        val accountName: String,
        val accountNo: String,
    ) : AEBEvent

    /**
     * Navigate to inter-bank transfer screen after successfully adding beneficiary from QR scan.
     * Contains phone number/external ID for participant lookup.
     */
    data class NavigateToInterbankTransfer(
        val accountNumber: String,
        val recipientName: String,
    ) : AEBEvent
}

internal sealed interface AEBAction {
    data class ChangeLocale(val locale: String) : AEBAction
    data class ChangeName(val name: String) : AEBAction
    data class ChangeOfficeName(val officeName: String) : AEBAction
    data class ChangeAccountNumber(val accountNumber: String) : AEBAction
    data class ChangeAccountType(val accountType: Int) : AEBAction
    data class ChangeTransferLimit(val transferLimit: String) : AEBAction
    data object OnQrScanClicked : AEBAction

    data object DismissDialog : AEBAction
    data object NavigateBack : AEBAction
    data object SaveBeneficiary : AEBAction

    sealed interface Internal : AEBAction {
        data class HandleBeneficiaryAddEditResult(val result: DataState<String>) : Internal
    }
}
