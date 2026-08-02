/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.accounts

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mobile_wallet.feature.accounts.generated.resources.Res
import mobile_wallet.feature.accounts.generated.resources.delete_beneficiary_subtitle
import mobile_wallet.feature.accounts.generated.resources.delete_beneficiary_title
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_beneficiary_deleted
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_default_account_updated
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.accounts.AccountAction.Internal.BeneficiaryDeleteResultReceived
import org.mifospay.feature.accounts.AccountAction.Internal.DeleteBeneficiary
import org.mifospay.feature.accounts.AccountEvent.OnAddEditSavingsAccount
import org.mifospay.feature.accounts.savingsaccount.SavingsAddEditType
import org.mifospay.feature.beneficiary.addupdatebeneficiary.BeneficiaryAddEditType

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModel(
    private val userRepository: UserPreferencesRepository,
    private val repository: SelfServiceRepository,
    private val json: Json,
) : BaseViewModel<AccountState, AccountEvent, AccountAction>(
    initialState = run {
        val clientId = userRepository.clientId.value
        val defaultAccount = userRepository.defaultAccountId.value

        AccountState(
            clientId = clientId,
            defaultAccountId = defaultAccount,
        )
    },
) {
    // Phase-5 Batch-4: cut over from the transitional
    // `getAccountAndBeneficiaryList` (Flow<DataState<T>>) to the store-native
    // `getAccountAndBeneficiaryListScreen` (Flow<ScreenState<T>>), which folds
    // the network-account stream + store-backed beneficiary stream through a
    // manual `combine {}` fold over the FORK's ScreenState (priority ladder:
    // NoNetwork > Loading > Unauthenticated > Error > Empty > Content). See
    // `SelfServiceRepositoryImpl.foldAccountAndBeneficiary` for the fold's
    // implementation; the template's `combineScreenStates` helper cannot be
    // used verbatim because it is typed against the template's ScreenState.
    val accountState = mutableStateFlow
        .flatMapLatest { currentState ->
            val clientId = currentState.clientId
            if (clientId != null) {
                repository.getAccountAndBeneficiaryListScreen(clientId, viewModelScope)
            } else {
                flowOf(
                    ScreenState.Error(IllegalStateException("Client ID not available")) as ScreenState<org.mifospay.core.model.account.AccountContent>,
                )
            }
        }
        .mapLatest { screenState ->
            when (screenState) {
                is ScreenState.Loading -> AccountState.ViewState.Loading

                is ScreenState.Empty ->
                    // Empty account-list branch → render an empty Content
                    // (screen still shows the FAB + empty-state copy; no
                    // dedicated Empty ViewState variant exists here).
                    AccountState.ViewState.Content(
                        accounts = emptyList(),
                        beneficiaries = emptyList(),
                    )

                is ScreenState.NoNetwork ->
                    AccountState.ViewState.Error("No network connection")

                is ScreenState.Unauthenticated ->
                    AccountState.ViewState.Error("Session expired — please sign in")

                is ScreenState.Error ->
                    AccountState.ViewState.Error(screenState.error.message.toString())

                is ScreenState.Content -> {
                    val sortedAccounts = screenState.data.accounts.sortedWith(
                        compareByDescending<Account> { account -> account.status.active }
                            .thenBy { account -> account.number },
                    )

                    AccountState.ViewState.Content(
                        accounts = sortedAccounts,
                        beneficiaries = screenState.data.beneficiaries,
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AccountState.ViewState.Loading,
        )

    override fun handleAction(action: AccountAction) {
        when (action) {
            is AccountAction.CreateSavingsAccount -> {
                sendEvent(OnAddEditSavingsAccount(SavingsAddEditType.AddItem))
            }

            is AccountAction.EditSavingsAccount -> {
                sendEvent(OnAddEditSavingsAccount(SavingsAddEditType.EditItem(action.accountId)))
            }

            is AccountAction.ViewAccountDetails -> {
                sendEvent(AccountEvent.OnNavigateToAccountDetail(action.accountId))
            }

            is AccountAction.AddTPTBeneficiary -> {
                sendEvent(AccountEvent.OnAddOrEditTPTBeneficiary(BeneficiaryAddEditType.AddItem()))
            }

            is AccountAction.EditBeneficiary -> {
                launchIO {
                    val beneficiary = json.encodeToString<Beneficiary>(action.beneficiary)
                    sendEvent(
                        AccountEvent.OnAddOrEditTPTBeneficiary(
                            BeneficiaryAddEditType.EditItem(beneficiary),
                        ),
                    )
                }
            }

            is AccountAction.DeleteBeneficiary -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = AccountState.DialogState.DeleteBeneficiary(
                            title = Res.string.delete_beneficiary_title,
                            message = Res.string.delete_beneficiary_subtitle,
                            onConfirm = {
                                trySendAction(DeleteBeneficiary(action.beneficiaryId))
                            },
                        ),
                    )
                }
            }

            is AccountAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is AccountAction.SetDefaultAccount -> handleSetDefaultAccount(action)

            is AccountAction.ApplyForLoan -> {
                state.clientId?.let { clientId ->
                    sendEvent(AccountEvent.OnApplyForLoan(clientId))
                }
            }

            is DeleteBeneficiary -> handleDeleteBeneficiary(action)

            is BeneficiaryDeleteResultReceived -> handleBeneficiaryDeleteResult(action)
        }
    }

    private fun handleSetDefaultAccount(action: AccountAction.SetDefaultAccount) {
        launchIO {
            userRepository.updateDefaultAccount(
                DefaultAccount(
                    accountId = action.accountId,
                    accountNo = action.accountNo,
                ),
            )
        }

        mutableStateFlow.update { it.copy(defaultAccountId = action.accountId) }
        sendEvent(AccountEvent.ShowToast(Res.string.feature_accounts_default_account_updated))
    }

    private fun handleDeleteBeneficiary(action: DeleteBeneficiary) {
        mutableStateFlow.update { it.copy(dialogState = AccountState.DialogState.Loading) }

        launchIO {
            val result = repository.deleteBeneficiary(action.beneficiaryId)

            sendAction(BeneficiaryDeleteResultReceived(result))
        }
    }

    private fun handleBeneficiaryDeleteResult(action: BeneficiaryDeleteResultReceived) {
        when (action.result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }

                sendEvent(AccountEvent.ShowToast(Res.string.feature_accounts_beneficiary_deleted))
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()

                mutableStateFlow.update {
                    it.copy(dialogState = AccountState.DialogState.Error(message))
                }
            }

            DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(dialogState = AccountState.DialogState.Loading)
                }
            }
        }
    }
}

data class AccountState(
    val clientId: Long? = null,
    val defaultAccountId: Long? = null,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
        data class DeleteBeneficiary(
            val title: StringResource,
            val message: StringResource,
            val onConfirm: () -> Unit,
        ) : DialogState
    }

    sealed interface ViewState {
        val hasFab: Boolean

        val isPullToRefreshEnabled: Boolean

        data object Loading : ViewState {
            override val hasFab: Boolean get() = false
            override val isPullToRefreshEnabled: Boolean get() = false
        }

        data class Error(val message: String) : ViewState {
            override val hasFab: Boolean get() = false
            override val isPullToRefreshEnabled: Boolean get() = false
        }

        data class Content(
            val accounts: List<Account>,
            val beneficiaries: List<Beneficiary>,
        ) : ViewState {
            override val hasFab: Boolean get() = true
            override val isPullToRefreshEnabled: Boolean get() = true
        }
    }
}

sealed interface AccountEvent {
    data class OnAddEditSavingsAccount(val type: SavingsAddEditType) : AccountEvent
    data class OnNavigateToAccountDetail(val accountId: Long) : AccountEvent
    data class OnAddOrEditTPTBeneficiary(val type: BeneficiaryAddEditType) : AccountEvent
    data class OnApplyForLoan(val clientId: Long) : AccountEvent

    data class ShowToast(val message: StringResource) : AccountEvent
}

sealed interface AccountAction {
    data object AddTPTBeneficiary : AccountAction
    data class EditBeneficiary(val beneficiary: Beneficiary) : AccountAction
    data class DeleteBeneficiary(val beneficiaryId: Long) : AccountAction

    data object CreateSavingsAccount : AccountAction
    data class EditSavingsAccount(val accountId: Long) : AccountAction
    data class ViewAccountDetails(val accountId: Long) : AccountAction
    data class SetDefaultAccount(val accountId: Long, val accountNo: String) : AccountAction
    data object ApplyForLoan : AccountAction

    data object DismissDialog : AccountAction

    sealed interface Internal : AccountAction {
        data class DeleteBeneficiary(val beneficiaryId: Long) : Internal
        data class BeneficiaryDeleteResultReceived(val result: DataState<String>) : Internal
    }
}
