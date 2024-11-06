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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mobile_wallet.feature.accounts.generated.resources.Res
import mobile_wallet.feature.accounts.generated.resources.delete_beneficiary_subtitle
import mobile_wallet.feature.accounts.generated.resources.delete_beneficiary_title
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.accounts.AccountAction.Internal.BeneficiaryDeleteResultReceived
import org.mifospay.feature.accounts.AccountAction.Internal.DeleteBeneficiary
import org.mifospay.feature.accounts.AccountEvent.OnAddEditSavingsAccount
import org.mifospay.feature.accounts.beneficiary.BeneficiaryAddEditType
import org.mifospay.feature.accounts.savingsaccount.SavingsAddEditType

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModel(
    private val userRepository: UserPreferencesRepository,
    private val repository: SelfServiceRepository,
    private val json: Json,
) : BaseViewModel<AccountState, AccountEvent, AccountAction>(
    initialState = run {
        val clientId = requireNotNull(userRepository.clientId.value)
        val defaultAccount = userRepository.defaultAccountId.value

        AccountState(
            clientId = clientId,
            defaultAccountId = defaultAccount,
        )
    },
) {
    val accountState = repository.getAccountAndBeneficiaryList(state.clientId)
        .mapLatest {
            when (it) {
                is DataState.Loading -> AccountState.ViewState.Loading
                is DataState.Error -> AccountState.ViewState.Error(it.exception.message.toString())
                is DataState.Success -> {
                    AccountState.ViewState.Content(it.data.accounts, it.data.beneficiaries)
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
                sendEvent(AccountEvent.OnAddOrEditTPTBeneficiary(BeneficiaryAddEditType.AddItem))
            }

            is AccountAction.EditBeneficiary -> {
                viewModelScope.launch {
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

            is DeleteBeneficiary -> handleDeleteBeneficiary(action)

            is BeneficiaryDeleteResultReceived -> handleBeneficiaryDeleteResult(action)
        }
    }

    private fun handleSetDefaultAccount(action: AccountAction.SetDefaultAccount) {
        viewModelScope.launch {
            userRepository.updateDefaultAccount(
                DefaultAccount(
                    accountId = action.accountId,
                    accountNo = action.accountNo,
                ),
            )
        }

        mutableStateFlow.update { it.copy(defaultAccountId = action.accountId) }
        sendEvent(AccountEvent.ShowToast("Default account updated"))
    }

    private fun handleDeleteBeneficiary(action: DeleteBeneficiary) {
        mutableStateFlow.update { it.copy(dialogState = AccountState.DialogState.Loading) }

        viewModelScope.launch {
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

                sendEvent(AccountEvent.ShowToast("Beneficiary deleted"))
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
    val clientId: Long,
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

    data class ShowToast(val message: String) : AccountEvent
}

sealed interface AccountAction {
    data object AddTPTBeneficiary : AccountAction
    data class EditBeneficiary(val beneficiary: Beneficiary) : AccountAction
    data class DeleteBeneficiary(val beneficiaryId: Long) : AccountAction

    data object CreateSavingsAccount : AccountAction
    data class EditSavingsAccount(val accountId: Long) : AccountAction
    data class ViewAccountDetails(val accountId: Long) : AccountAction
    data class SetDefaultAccount(val accountId: Long, val accountNo: String) : AccountAction

    data object DismissDialog : AccountAction

    sealed interface Internal : AccountAction {
        data class DeleteBeneficiary(val beneficiaryId: Long) : Internal
        data class BeneficiaryDeleteResultReceived(val result: DataState<String>) : Internal
    }
}
