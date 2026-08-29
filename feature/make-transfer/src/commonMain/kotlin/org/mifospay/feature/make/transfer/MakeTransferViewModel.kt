/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-pay/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer

import kotlinx.coroutines.flow.combine
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.PocketAccount
import org.jetbrains.compose.resources.getString
import kpt.core.ui.generated.resources.core_ui_pocket_added_successfully
import kpt.core.ui.generated.resources.core_ui_pocket_add_failed
import kpt.core.ui.generated.resources.core_ui_success
import kpt.core.ui.generated.resources.Res as UiRes
import kpt.core.ui.generated.resources.core_ui_error_msg_generic
import kpt.core.ui.generated.resources.core_ui_error_failed_to_load_accounts
import kpt.core.ui.generated.resources.core_ui_error_no_network
import kpt.core.ui.generated.resources.core_ui_error_session_expired
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.repository.PocketRepository
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import mifos_pay.feature.make_transfer.generated.resources.Res
import mifos_pay.feature.make_transfer.generated.resources.feature_make_transfer_error_empty_amount
import mifos_pay.feature.make_transfer.generated.resources.feature_make_transfer_error_empty_description
import mifos_pay.feature.make_transfer.generated.resources.feature_make_transfer_error_inactive_account
import mifos_pay.feature.make_transfer.generated.resources.feature_make_transfer_error_insufficient_balance
import mifos_pay.feature.make_transfer.generated.resources.feature_make_transfer_error_invalid_amount
import mifos_pay.feature.make_transfer.generated.resources.feature_make_transfer_error_same_account
import mifos_pay.feature.make_transfer.generated.resources.feature_make_transfer_error_select_account
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DateHelper
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.submit.SubmitState
import kpt.core.base.store.submit.submitHandler
import org.mifospay.core.common.utils.capitalizeWords
import org.mifospay.core.data.util.UpiQrCodeProcessor
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.AccountTransferPayload
import org.mifospay.core.model.utils.PaymentQrData
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.make.transfer.MakeTransferState.DialogState.Error
import org.mifospay.feature.make.transfer.navigation.TRANSFER_ARG

internal class MakeTransferViewModel(
    private val accountRepository: AccountRepository,
    private val pocketRepository: PocketRepository,
    repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MakeTransferState, MakeTransferEvent, MakeTransferAction>(
    initialState = run {
        val fromClientId = requireNotNull(repository.clientId.value)
        val defaultAccountId = requireNotNull(repository.defaultAccountId.value)
        val paymentData = requireNotNull(savedStateHandle.get<String>(TRANSFER_ARG))
        val clientData = UpiQrCodeProcessor.decodeUpiString(paymentData)

        MakeTransferState(
            fromClientId = fromClientId,
            defaultAccountId = defaultAccountId,
            toClientData = clientData,
        )
    },
) {
    // Phase-3 fold: `getSelfAccounts` was migrated to `Flow<ScreenState<List<Account>>>`;
    // fold the 6-branch ScreenState into the existing 4-branch ViewState.
    // Content(emptyList) is defensively mapped to Empty (repo already emits
    // ScreenState.Empty for empty responses, but the guard preserves prior
    // behavior). NoNetwork / Unauthenticated fold into Error until Phase-4
    // differentiates.
    @OptIn(ExperimentalCoroutinesApi::class)
    val accountsState = combine(
            accountRepository.getSelfAccountsStream(state.fromClientId, viewModelScope).state,
            pocketRepository.observeLinkedPocketAccounts(state.fromClientId)
        ) { accountsState, pocketAccounts ->
            if (accountsState is ScreenState.Loading) {
                return@combine ViewState.Loading
            }
            val pocketAccountIds = pocketAccounts
                .filter { it.accountType == AccountType.SAVINGS }
                .map { it.accountId }
                .toSet()

            mutableStateFlow.update { it.copy(pocketAccountIds = pocketAccountIds) }

            when (accountsState) {
                is ScreenState.Content<*> -> {
                    if (accountsState.data.isEmpty()) {
                        ViewState.Empty
                    } else {
                        /* 
                         * Pocket Account Suggestion Feature:
                         * Sorts the accounts so that linked pocket accounts appear at the top.
                         */
                        val sortedAccounts = accountsState.data.sortedByDescending { it.id in pocketAccountIds }
                        val account = state.selectedAccount ?: sortedAccounts.firstOrNull()
                        if (account != null) {
                            mutableStateFlow.update { it.copy(selectedAccount = account) }
                        }
                        ViewState.Content(sortedAccounts)
                    }
                }
                is ScreenState.Empty -> ViewState.Empty
                is ScreenState.Error -> ViewState.Error(
                    accountsState.error.message ?: getString(UiRes.string.core_ui_error_failed_to_load_accounts),
                )
                is ScreenState.NoNetwork -> ViewState.Error(
                    getString(UiRes.string.core_ui_error_no_network),
                )
                is ScreenState.Unauthenticated -> ViewState.Error(
                    getString(UiRes.string.core_ui_error_session_expired),
                )
                is ScreenState.Loading -> ViewState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewState.Loading,
        )

    // Template idiom (core-base/store): the money-movement transfer write goes through a
    // SubmitHandler instead of a hand-folded DataState result action. The handler owns the
    // Submitting/Submitted/Failed lifecycle (and is idempotent while Submitting — this is the
    // double-submit guard for the transfer); we observe it to drive this screen's EXISTING
    // Loading dialog / error dialog / OnTransferSuccess navigation, so the Screen is unchanged.
    private val submitTransfer = viewModelScope.submitHandler<Unit>()
    private val submitLink = viewModelScope.submitHandler<Unit>()

    init {
        submitTransfer.state
            .onEach { submitState ->
                when (submitState) {
                    is SubmitState.Submitting -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = MakeTransferState.DialogState.Loading)
                        }
                    }

                    is SubmitState.Submitted -> {
                        mutableStateFlow.update { it.copy(dialogState = null) }
                        sendEvent(MakeTransferEvent.OnTransferSuccess)
                        submitTransfer.reset()
                    }

                    is SubmitState.Failed -> {
                        // Preserve the exact prior error UX: DataState.Error.message was
                        // `exception.message.toString()`; the submit block throws that same
                        // exception, so this reproduces the identical dialog string.
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = Error.StringMessage(
                                    submitState.error.message.toString(),
                                ),
                            )
                        }
                        submitTransfer.reset()
                    }

                    SubmitState.Idle -> Unit
                }
            }
            .launchIn(viewModelScope)

        submitLink.state
            .onEach { submitState ->
                when (submitState) {
                    is SubmitState.Submitting -> Unit
                    is SubmitState.Submitted -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = MakeTransferState.DialogState.Success(
                                    kpt.core.ui.generated.resources.Res.string.core_ui_pocket_added_successfully,
                                ),
                                pocketAccountIds = it.pocketAccountIds + (it.selectedAccount?.id ?: 0L),
                            )
                        }
                        submitLink.reset()
                    }
                    is SubmitState.Failed -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = MakeTransferState.DialogState.Error.ResourceMessage(
                                    kpt.core.ui.generated.resources.Res.string.core_ui_pocket_add_failed,
                                ),
                            )
                        }
                        submitLink.reset()
                    }
                    SubmitState.Idle -> Unit
                }
            }
            .launchIn(viewModelScope)

        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: MakeTransferAction) {
        when (action) {
            MakeTransferAction.NavigateBack -> navigateBack()
            is MakeTransferAction.AmountChanged -> updateAmount(action.amount)
            is MakeTransferAction.DescriptionChanged -> updateDescription(action.desc)
            is MakeTransferAction.SelectAccount -> selectAccount(action)
            is MakeTransferAction.DismissDialog -> dismissDialog()
            is MakeTransferAction.InitiateTransfer -> validateTransfer()
            is MakeTransferAction.ConfirmAddToPocket -> confirmAddToPocket(action.add)
        }
    }

    private fun navigateBack() {
        sendEvent(MakeTransferEvent.OnNavigateBack)
    }

    private fun updateAmount(amount: String) {
        mutableStateFlow.update { it.copy(amount = amount) }
    }

    private fun updateDescription(desc: String) {
        mutableStateFlow.update { it.copy(description = desc) }
    }

    private fun selectAccount(action: MakeTransferAction.SelectAccount) {
        mutableStateFlow.update { it.copy(selectedAccount = action.account) }
        if (action.account.id !in state.pocketAccountIds ) {
            mutableStateFlow.update { it.copy(dialogState = MakeTransferState.DialogState.AddToPocketConfirmation) }
        }
    }

    private fun dismissDialog() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }

    private fun validateTransfer() = when {
        state.amount.isBlank() -> updateErrorState(Res.string.feature_make_transfer_error_empty_amount)

        state.amount.toDoubleOrNull() == null -> updateErrorState(Res.string.feature_make_transfer_error_invalid_amount)

        state.description.isBlank() -> updateErrorState(Res.string.feature_make_transfer_error_empty_description)

        state.selectedAccount == null -> updateErrorState(Res.string.feature_make_transfer_error_select_account)

        state.selectedAccount?.status?.active == false -> {
            updateErrorState(Res.string.feature_make_transfer_error_inactive_account)
        }

        state.selectedAccount?.id == state.toClientData.accountId -> {
            updateErrorState(Res.string.feature_make_transfer_error_same_account)
        }

        state.amount.toDouble() > state.selectedAccount?.balance!! -> {
            updateErrorState(Res.string.feature_make_transfer_error_insufficient_balance)
        }

        else -> initiateTransfer()
    }


    /**
     * Handles the user's decision to add the selected account to their Pocket.
     * If true, it initiates a link submission using the PocketRepository.
     * If false, it simply proceeds to the next step.
     */
    private fun confirmAddToPocket(add: Boolean) {
        mutableStateFlow.update { it.copy(dialogState = null) }
        if (add) {
            val accountId = state.selectedAccount?.id
            val accountNumber = state.selectedAccount?.number
            if (accountId == null || accountNumber.isNullOrBlank()) {
                return
            }
            val detailedAccount = DetailedPocketAccount(
                        pocket = PocketAccount(
                            pocketId = 0,
                            id = 0,
                            accountId = accountId,
                            accountType = AccountType.SAVINGS,
                            accountNumber = accountNumber
                        ),
                        productName = state.selectedAccount?.productName ?: state.selectedAccount?.name,
                        balance = state.selectedAccount?.balance,
                        currencyCode = state.selectedAccount?.currency?.code,
                        decimalPlaces = state.selectedAccount?.currency?.decimalPlaces,
                        status = state.selectedAccount?.status?.let {
                            when {
                                it.active -> AccountStatus.ACTIVE
                                it.approved -> AccountStatus.APPROVED
                                it.rejected -> AccountStatus.REJECTED
                                it.closed -> AccountStatus.CLOSED
                                it.submittedAndPendingApproval -> AccountStatus.PENDING
                                else -> null
                            }
                        },
                        currencyDisplaySymbol = state.selectedAccount?.currency?.displaySymbol
                    )
            submitLink.submit {
                pocketRepository.linkAccounts(listOf(detailedAccount), state.fromClientId)
            }
        }
    }

    private fun initiateTransfer() {
        // Show the loading dialog immediately (mirrors prior UX); the handler's Submitting
        // state re-affirms it. Submit is idempotent while Submitting, so a rapid second tap
        // cannot launch a second transfer.
        mutableStateFlow.update {
            it.copy(dialogState = MakeTransferState.DialogState.Loading)
        }

        submitTransfer.submit {
            accountRepository.makeTransfer(state.transferPayload)
        }
    }

    private fun updateErrorState(message: StringResource) {
        mutableStateFlow.update {
            it.copy(dialogState = Error.ResourceMessage(message))
        }
    }
}

internal data class MakeTransferState(
    val fromClientId: Long,
    val toClientData: PaymentQrData,
    val defaultAccountId: Long,
    val amount: String = toClientData.amount,
    val description: String = "",
    val selectedAccount: Account? = null,
    val pocketAccountIds: Set<Long> = emptySet(),
    @Transient val dialogState: DialogState? = null,
) {
    val amountIsValid: Boolean
        get() = amount.isNotEmpty() && amount.toDoubleOrNull() != null

    val descriptionIsValid: Boolean
        get() = description.isNotEmpty()

    val transferPayload: AccountTransferPayload
        get() = AccountTransferPayload(
            fromOfficeId = toClientData.officeId,
            fromClientId = fromClientId,
            fromAccountType = toClientData.accountTypeId,
            fromAccountId = selectedAccount?.id ?: defaultAccountId,
            toOfficeId = toClientData.officeId,
            toClientId = toClientData.clientId,
            toAccountType = toClientData.accountTypeId,
            toAccountId = toClientData.accountId,
            transferAmount = amount,
            transferDescription = description.capitalizeWords(),
            locale = "en_IN",
            dateFormat = DateHelper.SHORT_MONTH,
            transferDate = DateHelper.formattedShortDate,
        )

    sealed interface DialogState {
        data object Loading : DialogState
        data object AddToPocketConfirmation : DialogState
        data class Success(val message: StringResource) : DialogState

        sealed interface Error : DialogState {
            data class StringMessage(val message: String) : Error
            data class ResourceMessage(val message: StringResource) : Error
        }
    }
}

internal sealed interface ViewState {
    data object Loading : ViewState
    data object Empty : ViewState
    data class Error(val message: String) : ViewState
    data class Content(val data: List<Account>) : ViewState
}

internal sealed interface MakeTransferEvent {
    data object OnNavigateBack : MakeTransferEvent
    data object OnTransferSuccess : MakeTransferEvent
}

internal sealed interface MakeTransferAction {
    data object NavigateBack : MakeTransferAction

    data object DismissDialog : MakeTransferAction

    data object InitiateTransfer : MakeTransferAction

    data class AmountChanged(val amount: String) : MakeTransferAction

    data class DescriptionChanged(val desc: String) : MakeTransferAction

    data class SelectAccount(val account: Account) : MakeTransferAction
    data class ConfirmAddToPocket(val add: Boolean) : MakeTransferAction
}
