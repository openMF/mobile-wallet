/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.transfer.intrabank.hub

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.RecentPayeeRepository
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.data.repository.ThirdPartyTransferRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.RecentPayee
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.network.model.entity.templates.account.AccountOption
import org.mifospay.core.ui.utils.BaseViewModel

class IntraBankHubViewModel(
    private val recentPayeeRepository: RecentPayeeRepository,
    private val repository: ThirdPartyTransferRepository,
    private val selfServiceRepository: SelfServiceRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<IntraBankHubState, IntraBankHubEvent, IntraBankHubAction>(
    initialState = IntraBankHubState(),
) {

    init {
        loadRecentPayees()
        loadBeneficiaries()
        loadTptTemplate()
    }

    override fun handleAction(action: IntraBankHubAction) {
        when (action) {
            IntraBankHubAction.NavigateBack -> {
                sendEvent(IntraBankHubEvent.NavigateBack)
            }

            IntraBankHubAction.OnSearchBarClicked -> {
                sendEvent(IntraBankHubEvent.NavigateToSearchAccountSelection)
            }

            IntraBankHubAction.OnAddPayeeClicked -> {
                sendEvent(IntraBankHubEvent.NavigateToBeneficiary)
            }

            is IntraBankHubAction.OnPayRecentPayee -> {
                sendEvent(
                    IntraBankHubEvent.NavigateToTransfer(
                        toOfficeId = action.payee.officeId.toInt(),
                        toClientId = action.payee.clientId,
                        toAccountId = action.payee.accountId.toInt(),
                        accountName = action.payee.clientName,
                        accountNo = action.payee.accountNo,
                    ),
                )
            }

            is IntraBankHubAction.OnPayBeneficiary -> {
                val matchedAccount = state.toAccountOptionsTemplate
                    ?.firstOrNull {
                        it.accountNo == action.beneficiary.accountNumber
                    }

                matchedAccount?.let {
                    val officeId = requireNotNull(it.officeId)
                    val clientId = requireNotNull(it.clientId)
                    val accountId = requireNotNull(it.accountId)

                    sendEvent(
                        IntraBankHubEvent.NavigateToTransfer(
                            toOfficeId = officeId,
                            toClientId = clientId,
                            toAccountId = accountId,
                            accountName = action.beneficiary.name,
                            accountNo = action.beneficiary.accountNumber,
                        ),
                    )
                }
            }

            IntraBankHubAction.OnHistoryClicked -> {
                sendEvent(IntraBankHubEvent.NavigateToHistory)
            }

            IntraBankHubAction.OnScanQrClicked -> {
                sendEvent(IntraBankHubEvent.NavigateToScanQr)
            }

            IntraBankHubAction.OnRequestMoneyClicked -> {
                sendEvent(IntraBankHubEvent.NavigateToRequestMoney)
            }

            IntraBankHubAction.OnSeeAllTransactionsClicked -> {
                sendEvent(IntraBankHubEvent.NavigateToHistory)
            }

            IntraBankHubAction.RefreshBeneficiaries -> {
                loadBeneficiaries()
            }

            is IntraBankHubAction.OnTabSelected -> {
                mutableStateFlow.update {
                    it.copy(selectedTab = action.tab)
                }
            }
        }
    }

    private fun loadTptTemplate() {
        viewModelScope.launch {
            val toAccountOptions = repository.getTransferTemplate().toAccountOptions
            mutableStateFlow.update {
                it.copy(toAccountOptionsTemplate = toAccountOptions)
            }
        }
    }

    private fun loadRecentPayees() {
        val accountId = userPreferencesRepository.defaultAccountId.value
        if (accountId == null) {
            Logger.d { "RecentPayee: No default account ID found" }
            mutableStateFlow.update {
                it.copy(recentPayeesState = RecentPayeesState.Empty)
            }
            return
        }

        Logger.d { "RecentPayee: Loading recent payees for account $accountId" }

        recentPayeeRepository.getRecentPayees(accountId, RECENT_PAYEES_LIMIT)
            .onEach { result ->
                when (result) {
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(recentPayeesState = RecentPayeesState.Loading)
                        }
                    }
                    is DataState.Success -> {
                        Logger.d { "RecentPayee: Loaded ${result.data.size} recent payees" }
                        mutableStateFlow.update {
                            it.copy(
                                recentPayees = result.data,
                                recentPayeesState = if (result.data.isEmpty()) {
                                    RecentPayeesState.Empty
                                } else {
                                    RecentPayeesState.Success
                                },
                            )
                        }
                    }
                    is DataState.Error -> {
                        Logger.e(result.exception) { "RecentPayee: Failed to load" }
                        mutableStateFlow.update {
                            it.copy(recentPayeesState = RecentPayeesState.Error)
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadBeneficiaries() {
        viewModelScope.launch {
            selfServiceRepository.getBeneficiaryList()
                .onEach { result ->
                    when (result) {
                        is DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(beneficiariesState = BeneficiariesState.Loading)
                            }
                        }
                        is DataState.Success -> {
                            Logger.d { "Beneficiaries: Loaded ${result.data.size} beneficiaries" }
                            mutableStateFlow.update {
                                it.copy(
                                    beneficiaries = result.data,
                                    beneficiariesState = if (result.data.isEmpty()) {
                                        BeneficiariesState.Empty
                                    } else {
                                        BeneficiariesState.Success
                                    },
                                )
                            }
                        }
                        is DataState.Error -> {
                            Logger.e(result.exception) { "Beneficiaries: Failed to load" }
                            mutableStateFlow.update {
                                it.copy(beneficiariesState = BeneficiariesState.Error)
                            }
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    companion object {
        private const val RECENT_PAYEES_LIMIT = 10
    }
}

enum class PayeeTab {
    Recents,
    Beneficiaries,
}

@Serializable
data class IntraBankHubState(
    val selectedTab: PayeeTab = PayeeTab.Recents,
    val toAccountOptionsTemplate: List<AccountOption>? = null,
    @Transient
    val recentPayees: List<RecentPayee> = emptyList(),
    @Transient
    val recentPayeesState: RecentPayeesState = RecentPayeesState.Loading,
    @Transient
    val beneficiaries: List<Beneficiary> = emptyList(),
    @Transient
    val beneficiariesState: BeneficiariesState = BeneficiariesState.Loading,
)

enum class RecentPayeesState {
    Loading,
    Success,
    Empty,
    Error,
}

enum class BeneficiariesState {
    Loading,
    Success,
    Empty,
    Error,
}

sealed interface IntraBankHubEvent {
    data object NavigateToSearchAccountSelection : IntraBankHubEvent
    data object NavigateBack : IntraBankHubEvent
    data object NavigateToBeneficiary : IntraBankHubEvent
    data object NavigateToHistory : IntraBankHubEvent
    data object NavigateToScanQr : IntraBankHubEvent
    data object NavigateToRequestMoney : IntraBankHubEvent
    data class NavigateToTransfer(
        val toOfficeId: Int,
        val toClientId: Long,
        val toAccountId: Int,
        val accountName: String,
        val accountNo: String,
    ) : IntraBankHubEvent
    data class NavigateToTransferBeneficiary(
        val beneficiary: Beneficiary,
    ) : IntraBankHubEvent
}

sealed interface IntraBankHubAction {
    data object NavigateBack : IntraBankHubAction
    data object OnSearchBarClicked : IntraBankHubAction
    data object OnAddPayeeClicked : IntraBankHubAction
    data object OnHistoryClicked : IntraBankHubAction
    data object OnScanQrClicked : IntraBankHubAction
    data object OnRequestMoneyClicked : IntraBankHubAction
    data object OnSeeAllTransactionsClicked : IntraBankHubAction
    data class OnTabSelected(val tab: PayeeTab) : IntraBankHubAction
    data class OnPayRecentPayee(val payee: RecentPayee) : IntraBankHubAction
    data class OnPayBeneficiary(val beneficiary: Beneficiary) : IntraBankHubAction

    data object RefreshBeneficiaries : IntraBankHubAction
}
