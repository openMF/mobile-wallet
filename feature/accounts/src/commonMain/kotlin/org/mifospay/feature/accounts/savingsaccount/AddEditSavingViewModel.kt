/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.accounts.savingsaccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.IgnoredOnParcel
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.LocalAssetRepository
import org.mifospay.core.data.repository.SavingsAccountRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.savingsaccount.CreateNewSavingEntity
import org.mifospay.core.model.savingsaccount.SavingAccountTemplate
import org.mifospay.core.model.savingsaccount.UpdateSavingAccountEntity
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.accounts.savingsaccount.AESAction.CreateOrUpdateSavingAccount
import org.mifospay.feature.accounts.savingsaccount.AESAction.Internal.HandleSavingAddEditResult
import org.mifospay.feature.accounts.savingsaccount.AESAction.Internal.HandleSavingTemplateResult
import org.mifospay.feature.accounts.savingsaccount.AESState.ViewState.Error
import org.mifospay.feature.accounts.savingsaccount.AESState.DialogState.Error as DialogStateError

private const val KEY = "add_edit_saving_state"

internal class AddEditSavingViewModel(
    private val repository: SavingsAccountRepository,
    private val userRepository: UserPreferencesRepository,
    localAssetRepository: LocalAssetRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AESState, AESEvent, AESAction>(
    initialState = savedStateHandle[KEY] ?: run {
        val clientId = requireNotNull(userRepository.clientId.value)
        val type = SavingAccountAddEditArgs(savedStateHandle).savingsAddEditType

        AESState(
            clientId = clientId,
            type = type,
            viewState = AESState.ViewState.Loading,
            dialogState = null,
        )
    },
) {
    val localeList = localAssetRepository.localeList.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
//        stateFlow
//            .onEach { savedStateHandle[KEY] = it }
//            .launchIn(viewModelScope)

        repository.getSavingAccountTemplate(state.clientId).onEach {
            sendAction(HandleSavingTemplateResult(it))
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: AESAction) {
        when (action) {
            is AESAction.ExternalIdChanged -> {
                updateContent { content ->
                    content.copy(externalId = action.externalId)
                }
            }

            is AESAction.ProductChanged -> {
                updateContent { content ->
                    content.copy(productId = action.productId)
                }
            }

            is AESAction.LocaleChanged -> {
                updateContent { content ->
                    content.copy(locale = action.locale)
                }
            }

            is AESAction.AllowOverdraftChanged -> {
                updateContent { content ->
                    content.copy(allowOverdraft = !content.allowOverdraft)
                }
            }

            is AESAction.EnforceMinRequiredBalanceChanged -> {
                updateContent { content ->
                    content.copy(enforceMinRequiredBalance = !content.enforceMinRequiredBalance)
                }
            }

            is AESAction.MinRequiredOpeningBalanceChanged -> {
                updateContent { content ->
                    content.copy(minRequiredOpeningBalance = action.balance.toLong())
                }
            }

            is AESAction.NominalAnnualInterestRateChanged -> {
                updateContent { content ->
                    content.copy(nominalAnnualInterestRate = action.rate.toDouble())
                }
            }

            is AESAction.OverdraftLimitChanged -> {
                updateContent { content ->
                    content.copy(overdraftLimit = action.overdraftLimit)
                }
            }

            is AESAction.WithHoldTaxChanged -> {
                updateContent { content ->
                    content.copy(withHoldTax = !content.withHoldTax)
                }
            }

            is AESAction.WithdrawalFeeForTransfersChanged -> {
                updateContent { content ->
                    content.copy(withdrawalFeeForTransfers = !content.withdrawalFeeForTransfers)
                }
            }

            is AESAction.NavigateBack -> {
                sendEvent(AESEvent.OnNavigateBack)
            }

            is AESAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is CreateOrUpdateSavingAccount -> initiateCreateOrUpdateSavingAccount()

            is HandleSavingAddEditResult -> handleSavingAddEditResult(action)

            is HandleSavingTemplateResult -> handleSavingTemplateResult(action)
        }
    }

    private fun initiateCreateOrUpdateSavingAccount() {
        onContent { content ->
            when (state.type) {
                is SavingsAddEditType.AddItem -> when {
                    content.productId == 0L -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Select a Saving Product"))
                        }
                    }

                    content.clientId.isEmpty() -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Client ID is required"))
                        }
                    }

                    content.externalId.isEmpty() -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("External ID is required"))
                        }
                    }

                    content.externalId.length < 8 -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("External ID must be at least 8 characters"))
                        }
                    }

                    content.enforceMinRequiredBalance && content.minRequiredOpeningBalance == 0L -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Min Required Opening Balance is required"))
                        }
                    }

                    content.allowOverdraft && content.overdraftLimit.isEmpty() -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Overdraft Limit is required"))
                        }
                    }

                    content.locale.isEmpty() -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Locale is required"))
                        }
                    }

                    content.submittedOnDate.isEmpty() -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Submitted On Date is required"))
                        }
                    }

                    content.dateFormat.isEmpty() -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Date Format is required"))
                        }
                    }

                    else -> initiateCreateSavingAccount()
                }

                is SavingsAddEditType.EditItem -> when {
                    content.productId == 0L -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Select a Saving Product"))
                        }
                    }

                    content.clientId.isEmpty() -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = DialogStateError("Client ID is required"))
                        }
                    }

                    else -> initiateUpdateSavingAccount()
                }
            }
        }
    }

    private fun initiateCreateSavingAccount() {
        onContent { content ->
            viewModelScope.launch {
                val result = repository.createSavingsAccount(content.createSavingEntity)
                sendAction(HandleSavingAddEditResult(result))
            }
        }
    }

    private fun initiateUpdateSavingAccount() {
        onContent { content ->
            val accountId = requireNotNull(state.type.savingsAccountId) {
                "Account ID is required for updating saving account"
            }

            viewModelScope.launch {
                val result = repository.updateSavingsAccount(accountId, content.updateSavingEntity)
                sendAction(HandleSavingAddEditResult(result))
            }
        }
    }

    private fun handleSavingTemplateResult(action: HandleSavingTemplateResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = AESState.ViewState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(viewState = Error(action.result.exception.message.toString()))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(viewState = AESState.ViewState.Content(action.result.data))
                }
            }
        }
    }

    private fun handleSavingAddEditResult(action: HandleSavingAddEditResult) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = AESState.DialogState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()
                mutableStateFlow.update {
                    it.copy(dialogState = DialogStateError(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
                sendEvent(AESEvent.ShowToast(action.result.data))
                sendEvent(AESEvent.OnNavigateBack)
            }
        }
    }

    private inline fun onContent(
        crossinline block: (AESState.ViewState.Content) -> Unit,
    ) {
        (state.viewState as? AESState.ViewState.Content)?.let(block)
    }

    private inline fun updateContent(
        crossinline block: (
            AESState.ViewState.Content,
        ) -> AESState.ViewState.Content?,
    ) {
        val currentViewState = state.viewState
        val updatedContent = (currentViewState as? AESState.ViewState.Content)
            ?.let(block)
            ?: return
        mutableStateFlow.update { it.copy(viewState = updatedContent) }
    }
}

@Parcelize
internal data class AESState(
    val clientId: Long,
    val type: SavingsAddEditType,
    val viewState: ViewState,
    val dialogState: DialogState?,
) : Parcelable {

    val isInEditMode: Boolean
        get() = type is SavingsAddEditType.EditItem

    val btnText: String
        get() = if (!isInEditMode) "Save" else "Update"

    val title: String
        get() = if (!isInEditMode) "Create Saving Account" else "Update Saving Account"

    sealed interface ViewState : Parcelable {
        @Parcelize
        data object Loading : ViewState

        @Parcelize
        data class Error(val message: String) : ViewState

        @Parcelize
        data class Content(
            val template: SavingAccountTemplate,
            val minRequiredOpeningBalance: Long = 0,
            val overdraftLimit: String = "",
            val externalId: String = "",
            val locale: String = "en_IN",
            val submittedOnDate: String = DateHelper.formattedShortDate,
            val dateFormat: String = DateHelper.SHORT_MONTH,
            val clientId: String = template.clientId,
            val productId: Long = template.productOptions.first().id,
            val nominalAnnualInterestRate: Double = template.nominalAnnualInterestRate,
            val withdrawalFeeForTransfers: Boolean = template.withdrawalFeeForTransfers,
            val allowOverdraft: Boolean = template.allowOverdraft,
            val enforceMinRequiredBalance: Boolean = template.enforceMinRequiredBalance,
            val withHoldTax: Boolean = template.withHoldTax,
        ) : ViewState {

            @IgnoredOnParcel
            val createSavingEntity: CreateNewSavingEntity
                get() = CreateNewSavingEntity(
                    clientId = clientId,
                    productId = productId,
                    nominalAnnualInterestRate = nominalAnnualInterestRate,
                    minRequiredOpeningBalance = minRequiredOpeningBalance,
                    withdrawalFeeForTransfers = withdrawalFeeForTransfers,
                    allowOverdraft = allowOverdraft,
                    overdraftLimit = overdraftLimit,
                    enforceMinRequiredBalance = enforceMinRequiredBalance,
                    withHoldTax = withHoldTax,
                    externalId = externalId,
                    submittedOnDate = submittedOnDate,
                    locale = locale,
                    dateFormat = dateFormat,
                )

            @IgnoredOnParcel
            val updateSavingEntity: UpdateSavingAccountEntity
                get() = UpdateSavingAccountEntity(
                    clientId = clientId,
                    productId = productId,
                )
        }
    }

    sealed interface DialogState : Parcelable {
        @Parcelize
        data object Loading : DialogState

        @Parcelize
        data class Error(val message: String) : DialogState
    }
}

internal sealed interface AESEvent {
    data object OnNavigateBack : AESEvent
    data class ShowToast(val message: String) : AESEvent
}

internal sealed interface AESAction {
    data class ProductChanged(val productId: Long) : AESAction
    data class ExternalIdChanged(val externalId: String) : AESAction
    data class LocaleChanged(val locale: String) : AESAction

    data class NominalAnnualInterestRateChanged(val rate: String) : AESAction

    data class MinRequiredOpeningBalanceChanged(val balance: String) : AESAction
    data object EnforceMinRequiredBalanceChanged : AESAction

    data object AllowOverdraftChanged : AESAction
    data class OverdraftLimitChanged(val overdraftLimit: String) : AESAction

    data object WithdrawalFeeForTransfersChanged : AESAction
    data object WithHoldTaxChanged : AESAction

    data object DismissDialog : AESAction
    data object NavigateBack : AESAction

    data object CreateOrUpdateSavingAccount : AESAction

    sealed interface Internal : AESAction {
        data class HandleSavingAddEditResult(val result: DataState<String>) : Internal
        data class HandleSavingTemplateResult(val result: DataState<SavingAccountTemplate>) :
            Internal
    }
}
