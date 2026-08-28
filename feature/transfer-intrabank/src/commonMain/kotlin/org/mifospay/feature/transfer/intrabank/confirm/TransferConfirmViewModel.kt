/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.transfer.intrabank.confirm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mifos_pay.feature.transfer_intrabank.generated.resources.Res
import mifos_pay.feature.transfer_intrabank.generated.resources.feature_make_transfer_error_empty_amount
import mifos_pay.feature.transfer_intrabank.generated.resources.feature_make_transfer_error_empty_description
import mifos_pay.feature.transfer_intrabank.generated.resources.feature_make_transfer_error_insufficient_balance
import mifos_pay.feature.transfer_intrabank.generated.resources.feature_make_transfer_error_invalid_amount
import mifos_pay.feature.transfer_intrabank.generated.resources.feature_make_transfer_error_same_account
import mifos_pay.feature.transfer_intrabank.generated.resources.feature_make_transfer_error_select_account
import mifos_pay.feature.transfer_intrabank.generated.resources.feature_make_transfer_user_verification_failed
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DateHelper
import org.mifospay.core.common.ScreenState
import org.mifospay.core.common.StringResourceSerializer
import org.mifospay.core.common.UiError
import org.mifospay.core.common.toUiError
import org.mifospay.core.common.utils.capitalizeWords
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.ThirdPartyTransferRepository
import org.mifospay.core.data.repository.UserVerificationRepository
import org.mifospay.core.network.model.entity.payload.TransferPayload
import org.mifospay.core.network.model.entity.templates.account.AccountOption
import org.mifospay.core.ui.DefaultErrorMessageProvider
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.transfer.intrabank.navigation.TransferConfirmRoute

/**
 * `SavedStateHandle` key written by `internalMifosPasscodeScreen` and read by
 * [TransferConfirmScreen] for the intra-bank transfer auth-gate round trip
 * (MW-390).
 *
 * Mirrors the disable-biometrics flow in `:feature:settings`, but with its
 * own key so the two flows don't cross-fire.
 */
const val INTRA_BANK_TRANSFER_VERIFICATION_KEY = "intra-banking_transfer_verification_key"

/**
 * ViewModel for the intra-bank transfer confirmation screen.
 *
 * Owns the **passcode/biometric auth gate** required before initiating a
 * transfer (MW-390):
 *  1. On [TransferConfirmAction.InitiateTransfer], runs validation; if it
 *     passes, calls `handleUserVerification` which emits
 *     [TransferConfirmEvent.NavigateForPasscodeVerification]. The screen
 *     responds by navigating to `internalMifosPasscodeScreen` with
 *     [INTRA_BANK_TRANSFER_VERIFICATION_KEY].
 *  2. The screen observes its own saved-state-handle for the round-trip
 *     boolean and re-dispatches it as
 *     [TransferConfirmAction.UpdateUserVerificationResult]. The VM resumes
 *     `handleUserVerification` on the result.
 *  3. On `success = true`, the VM calls
 *     [UserVerificationRepository.consumeVerification]. If the 30 s token is
 *     still valid the transfer proceeds; if expired, the transfer is
 *     cancelled with a "user verification failed" dialog.
 *
 * The VM does **not** import any `org.mifos.authenticator.*` symbol — the
 * library integration is purely on the screen and navigation side. This
 * class only needs the [UserVerificationRepository] contract.
 */
internal class TransferConfirmViewModel(
    private val repository: ThirdPartyTransferRepository,
    private val clientRepo: ClientRepository,
    private val userVerificationRepository: UserVerificationRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TransferConfirmState, TransferConfirmEvent, TransferConfirmAction>(
    initialState = run {
        val route = savedStateHandle.toRoute<TransferConfirmRoute>()
        TransferConfirmState(
            toOfficeId = route.toOfficeId,
            toClientId = route.toClientId,
            toAccountType = route.toAccountTypeId,
            toAccountId = route.accountId.toInt(),
            toAccountName = route.toAccountName,
            toAccountNo = route.toAccountNo,
            amount = if (route.amount == 0) "" else route.amount.toString(),
        )
    },
) {

    init {
        viewModelScope.launch {
            getFromAccounts()
        }
    }

    override fun handleAction(action: TransferConfirmAction) {
        when (action) {
            TransferConfirmAction.NavigateBack -> {
                sendEvent(TransferConfirmEvent.OnNavigateBack)
            }

            is TransferConfirmAction.AmountChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        amount = action.amount,
                    )
                }
            }

            is TransferConfirmAction.DescriptionChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        description = action.desc,
                    )
                }
            }

            is TransferConfirmAction.SelectAccount -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedAccount = action.account,
                        selectedAccountBalance = state.balanceMap.getOrElse(
                            action.account?.accountNo ?: "",
                        ) { 0.0 },
                        showBottomSheet = false,
                    )
                }
            }

            is TransferConfirmAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is TransferConfirmAction.InitiateTransfer -> {
                if (state.isProcessing) return
                mutableStateFlow.update {
                    it.copy(
                        isProcessing = true,
                        dialogState = null,
                    )
                }
                validateTransfer()
            }

            TransferConfirmAction.RetryTransfer -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
                trySendAction(TransferConfirmAction.InitiateTransfer)
            }

            TransferConfirmAction.CloseBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }

            TransferConfirmAction.OpenBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = true)
                }
            }

            is TransferConfirmAction.UpdateUserVerificationResult -> {
                mutableStateFlow.update {
                    it.copy(
                        userVerificationResult = action.result,
                        isAwaitingPasscodeVerification = false,
                    )
                }
            }
        }
    }

    private suspend fun getFromAccounts() {
        try {
            val res = repository.getTransferTemplate()
            val fromAccounts = res.fromAccountOptions?.filter {
                it.accountType?.id == 2
            }
            if (fromAccounts.isNullOrEmpty()) {
                mutableStateFlow.update {
                    it.copy(
                        state = TransferConfirmState.State.NoAccounts,
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        fromAccountOptions = fromAccounts,
                    )
                }
                getBalanceOfAccounts()
            }
        } catch (e: Exception) {
            mutableStateFlow.update {
                it.copy(
                    state = TransferConfirmState.State.Error(e.message ?: ""),
                )
            }
        }
    }

    private suspend fun getBalanceOfAccounts() {
        try {
            val clientId = state.fromAccountOptions?.first()?.clientId ?: -1
            val result = clientRepo.getClientAccounts(clientId)
            val balanceMap: Map<String, Double> =
                result.savingsAccounts.associate { account ->
                    account.accountNo to account.accountBalance
                }
            mutableStateFlow.update {
                it.copy(
                    state = TransferConfirmState.State.Success,
                    balanceMap = balanceMap,
                )
            }
            val firstAccount = state.fromAccountOptions?.first()
            mutableStateFlow.update {
                it.copy(
                    selectedAccount = firstAccount,
                    selectedAccountBalance = balanceMap.getOrElse(
                        firstAccount?.accountNo ?: "",
                        { 0.0 },
                    ),
                )
            }
        } catch (e: Exception) {
            mutableStateFlow.update {
                it.copy(
                    state = TransferConfirmState.State.Error(e.message ?: ""),
                )
            }
        }
    }

    private fun validateTransfer() {
        if (state.amount.isBlank()) {
            updateValidationError(Res.string.feature_make_transfer_error_empty_amount)
            return
        }

        val transferAmount = state.amount.toDoubleOrNull()
        if (transferAmount == null || transferAmount <= 0.0) {
            updateValidationError(Res.string.feature_make_transfer_error_invalid_amount)
            return
        }

        when {
            state.description.isBlank() -> updateValidationError(Res.string.feature_make_transfer_error_empty_description)

            state.selectedAccount == null -> updateValidationError(Res.string.feature_make_transfer_error_select_account)

            state.selectedAccount?.accountId == state.toAccountId -> {
                updateValidationError(Res.string.feature_make_transfer_error_same_account)
            }

            transferAmount > state.selectedAccountBalance -> {
                updateValidationError(Res.string.feature_make_transfer_error_insufficient_balance)
            }

            else -> initiateTransfer()
        }
    }

    private fun initiateTransfer() {
        viewModelScope.launch {
            handleUserVerification(
                onSuccess = {
                    if (userVerificationRepository.consumeVerification()) {
                        handleTransfer()
                    } else {
                        mutableStateFlow.update {
                            it.copy(
                                isProcessing = false,
                                dialogState = TransferConfirmState.DialogState
                                    .Error.ValidationError(Res.string.feature_make_transfer_user_verification_failed),
                            )
                        }
                    }
                },
                onFailed = {
                    mutableStateFlow.update {
                        it.copy(
                            isProcessing = false,
                            dialogState = TransferConfirmState.DialogState
                                .Error.ValidationError(Res.string.feature_make_transfer_user_verification_failed),
                        )
                    }
                },
            )
        }
    }

    private suspend fun handleTransfer() {
        mutableStateFlow.update {
            it.copy(dialogState = TransferConfirmState.DialogState.Loading)
        }
        // Phase-3 fold: `ThirdPartyTransferRepository.makeTransfer` was
        // migrated to `ScreenStateStream<TPTResponse>`. Content produces the
        // TransferResult; Empty is defensively surfaced as a generic error (a
        // submit endpoint should not emit Empty); NoNetwork / Unauthenticated
        // fold into the existing `Error.ApiError` surface until Phase-4
        // differentiates them.
        repository.makeTransfer(state.transferPayload).collect { result ->
            when (result) {
                is ScreenState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            isProcessing = true,
                            dialogState = null,
                        )
                    }
                }

                is ScreenState.Empty -> {
                    val uiError = IllegalStateException(
                        "Transfer submission returned no response.",
                    ).toUiError(DefaultErrorMessageProvider)
                    mutableStateFlow.update {
                        it.copy(
                            isProcessing = false,
                            dialogState = TransferConfirmState.DialogState.Error.ApiError(uiError),
                        )
                    }
                }

                is ScreenState.Error -> {
                    // Use UiError for automatic error message parsing with localized strings
                    val uiError = result.error.toUiError(DefaultErrorMessageProvider)
                    mutableStateFlow.update {
                        it.copy(
                            isProcessing = false,
                            dialogState = TransferConfirmState.DialogState.Error.ApiError(uiError),
                        )
                    }
                }

                is ScreenState.NoNetwork -> {
                    val uiError = Exception(
                        "No network. Please check your connection.",
                    ).toUiError(DefaultErrorMessageProvider)
                    mutableStateFlow.update {
                        it.copy(
                            isProcessing = false,
                            dialogState = TransferConfirmState.DialogState.Error.ApiError(uiError),
                        )
                    }
                }

                is ScreenState.Unauthenticated -> {
                    val uiError = Exception(
                        "Session expired. Please log in again.",
                    ).toUiError(DefaultErrorMessageProvider)
                    mutableStateFlow.update {
                        it.copy(
                            isProcessing = false,
                            dialogState = TransferConfirmState.DialogState.Error.ApiError(uiError),
                        )
                    }
                }

                is ScreenState.Content -> {
                    val response = result.data
                    val transferResult = TransferResult(
                        transactionId = response.resourceId ?: "",
                        amount = state.amount.toDoubleOrNull() ?: 0.0,
                        fromAccountNo = state.selectedAccount?.accountNo ?: "",
                        fromAccountName = state.selectedAccount?.clientName ?: "",
                        toAccountNo = state.toAccountNo,
                        toAccountName = state.toAccountName,
                        transferDate = DateHelper.formattedShortDate,
                        description = state.description.trim(),
                    )
                    mutableStateFlow.update {
                        it.copy(
                            isProcessing = false,
                            dialogState = null,
                            transferResult = transferResult,
                        )
                    }
                }
            }
        }
    }

    /**
     * Suspends until the passcode/biometric gate round trip completes.
     *
     * Emits [TransferConfirmEvent.NavigateForPasscodeVerification], then
     * waits for [TransferConfirmState.userVerificationResult] to flip from
     * null. The screen is responsible for calling
     * [TransferConfirmAction.UpdateUserVerificationResult] with the boolean
     * written under [INTRA_BANK_TRANSFER_VERIFICATION_KEY] in the
     * saved-state-handle.
     *
     * Branches:
     *  - `true` → [onSuccess] (caller still needs to call
     *    [UserVerificationRepository.consumeVerification] to confirm the
     *    30 s token is valid).
     *  - `false` → [onFailed] (user cancelled or rejected).
     *  - `null` → no-op (defensive; the filter above prevents this).
     */
    private suspend fun handleUserVerification(
        onSuccess: suspend () -> Unit,
        onFailed: suspend () -> Unit,
    ) {
        mutableStateFlow.update {
            it.copy(
                userVerificationResult = null,
                isAwaitingPasscodeVerification = true,
            )
        }

        sendEvent(TransferConfirmEvent.NavigateForPasscodeVerification)

        val result = stateFlow
            .map { it.userVerificationResult }
            .filter { it != null }
            .first()

        when (result) {
            true -> onSuccess()
            false -> onFailed()
            null -> {}
        }
    }

    private fun updateValidationError(message: StringResource) {
        mutableStateFlow.update {
            it.copy(
                isProcessing = false,
                dialogState = TransferConfirmState.DialogState.Error.ValidationError(message),
            )
        }
    }
}

@Serializable
internal data class TransferConfirmState(
    val toOfficeId: Int? = null,
    val toClientId: Long? = null,
    val toAccountType: Int? = null,
    val toAccountId: Int? = null,
    val toAccountName: String = "",
    val toAccountNo: String = "",
    val amount: String = "",

    val showBottomSheet: Boolean = false,
    val state: State = State.Loading,
    val description: String = "",
    val selectedAccount: AccountOption? = null,
    val selectedAccountBalance: Double = 0.0,
    val dialogState: DialogState? = null,
    val fromAccountOptions: List<AccountOption>? = emptyList(),
    val balanceMap: Map<String, Double> = emptyMap(),
    val isProcessing: Boolean = false,
    /**
     * `true` between [TransferConfirmEvent.NavigateForPasscodeVerification]
     * being emitted and [TransferConfirmAction.UpdateUserVerificationResult]
     * being received. Used by the screen to suppress double-taps on the
     * confirm button during the round trip.
     */
    val isAwaitingPasscodeVerification: Boolean = false,
    val transferResult: TransferResult? = null,
    /**
     * Round-trip result from the passcode/biometric gate:
     *  - `null` — not yet returned (initial / mid-flight),
     *  - `true` — verified; transfer can proceed,
     *  - `false` — cancelled / rejected; transfer is aborted.
     *
     * Cleared back to `null` on each new [handleUserVerification] call.
     */
    val userVerificationResult: Boolean? = null,
) {
    val amountIsValid: Boolean
        get() = amount.isNotEmpty() &&
            amount.toDoubleOrNull()?.let { it > 0.0 && it <= selectedAccountBalance } == true

    val descriptionIsValid: Boolean
        get() = description.trim().isNotEmpty()

    val transferPayload: TransferPayload
        get() = TransferPayload(
            fromOfficeId = selectedAccount?.officeId,
            fromClientId = selectedAccount?.clientId,
            fromAccountType = selectedAccount?.accountType?.id,
            fromAccountId = selectedAccount?.accountId,
            toOfficeId = toOfficeId,
            toClientId = toClientId,
            toAccountType = toAccountType,
            toAccountId = toAccountId,
            transferDate = DateHelper.formattedShortDate,
            transferAmount = amount.toDoubleOrNull() ?: 0.0,
            transferDescription = description.trim().capitalizeWords(),
            locale = "en_IN",
            dateFormat = DateHelper.SHORT_MONTH,
        )

    @Serializable
    sealed interface DialogState {
        @Serializable
        data object Loading : DialogState

        @Serializable
        sealed class Error : DialogState {
            abstract val canRetry: Boolean

            /**
             * Validation error with a string resource message.
             * Used for client-side validation errors.
             */
            @Serializable
            data class ValidationError(
                @Serializable(with = StringResourceSerializer::class)
                val message: StringResource,
            ) : Error() {
                override val canRetry: Boolean = false
            }

            /**
             * API error that wraps [UiError] for automatic error message handling.
             * The [UiError] contains user-friendly title, message and retry capability.
             */
            data class ApiError(
                val error: UiError,
            ) : Error() {
                override val canRetry: Boolean = error.canRetry
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data object NoAccounts : State
        data object Success : State
        data class Error(val message: String) : State
    }
}

/**
 * Transfer result data containing all the details of a successful transfer.
 */
@Serializable
data class TransferResult(
    val transactionId: String,
    val amount: Double,
    val fromAccountNo: String,
    val fromAccountName: String,
    val toAccountNo: String,
    val toAccountName: String,
    val transferDate: String,
    val description: String,
)

internal sealed interface TransferConfirmEvent {
    data object OnNavigateBack : TransferConfirmEvent

    /**
     * Tells the screen to push `internalMifosPasscodeScreen` with
     * [INTRA_BANK_TRANSFER_VERIFICATION_KEY]. The screen is responsible for
     * observing its own saved-state-handle for the round-trip boolean and
     * dispatching it back as
     * [TransferConfirmAction.UpdateUserVerificationResult].
     */
    data object NavigateForPasscodeVerification : TransferConfirmEvent
}

internal sealed interface TransferConfirmAction {
    data object NavigateBack : TransferConfirmAction
    data object DismissDialog : TransferConfirmAction
    data object InitiateTransfer : TransferConfirmAction
    data object RetryTransfer : TransferConfirmAction
    data class AmountChanged(val amount: String) : TransferConfirmAction
    data class DescriptionChanged(val desc: String) : TransferConfirmAction
    data class SelectAccount(val account: AccountOption?) : TransferConfirmAction
    data object OpenBottomSheet : TransferConfirmAction
    data object CloseBottomSheet : TransferConfirmAction

    /**
     * Round-trip result from the passcode/biometric gate, dispatched by
     * [TransferConfirmScreen] after observing
     * [INTRA_BANK_TRANSFER_VERIFICATION_KEY] flip on its saved-state-handle.
     * `true` lets the transfer proceed (subject to a still-valid
     * [UserVerificationRepository] token); `false` aborts.
     */
    data class UpdateUserVerificationResult(val result: Boolean) : TransferConfirmAction
}
