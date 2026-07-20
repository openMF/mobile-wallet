/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.viewmodels

/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile_wallet.feature.pocket.generated.resources.Res
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_delink_account
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_link_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_load_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_unknown_account
import org.jetbrains.compose.resources.getString
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.payload.PocketLinkPayload
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.model.pocket.PocketAccount
import org.mifospay.core.ui.utils.BaseViewModel

internal class ManagePocketViewModel(
    private val pocketRepository: PocketRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<ManagePocketState, ManagePocketEvent, ManagePocketAction>(
    initialState = ManagePocketState(
        clientId = requireNotNull(userPreferencesRepository.clientId.value),
    ),
) {
    private var linkedAccountsJob: Job? = null
    private var availableAccountsJob: Job? = null

    init {
        loadLinkedAccounts()
    }

    private fun updateState(update: (ManagePocketState) -> ManagePocketState) {
        mutableStateFlow.update(update)
    }

    override fun handleAction(action: ManagePocketAction) {
        when (action) {
            ManagePocketAction.NavigateBack -> sendEvent(ManagePocketEvent.NavigateBack)
            ManagePocketAction.Retry -> loadLinkedAccounts(forceRefresh = true)
            ManagePocketAction.OpenLinkAccounts -> openLinkAccounts()
            ManagePocketAction.DismissDialog -> dismissDialog()
            is ManagePocketAction.OpenDelinkConfirmation -> openDelinkConfirmation(action.account)
            is ManagePocketAction.TabSelected -> updateState { it.copy(selectedTab = action.accountType) }
            is ManagePocketAction.SearchQueryChanged -> updateState { it.copy(searchQuery = action.query) }
            is ManagePocketAction.AccountSelectionChanged -> updateSelectedAccount(
                accountId = action.accountId,
                accountType = action.accountType,
                selected = action.selected,
            )
            ManagePocketAction.LinkSelectedAccounts -> linkSelectedAccounts()
            is ManagePocketAction.DelinkAccount -> delinkAccount(action.account)
            is ManagePocketAction.Internal.ReceiveLinkedAccounts -> handleLinkedAccounts(action.dataState, action.unknownAccount)
            is ManagePocketAction.Internal.ReceiveAvailableAccounts -> handleAvailableAccounts(action.dataState, action.unknownAccount)
        }
    }

    private fun loadLinkedAccounts(forceRefresh: Boolean = false) {
        linkedAccountsJob?.cancel()
        linkedAccountsJob = viewModelScope.launch {
            val unknownAccount = getString(Res.string.feature_pocket_unknown_account)
            pocketRepository.getDetailedPocketAccounts(state.clientId, forceRefresh)
                .collect { dataState ->
                    trySendAction(ManagePocketAction.Internal.ReceiveLinkedAccounts(dataState, unknownAccount))
                }
        }
    }

    private fun loadAvailableAccounts() {
        availableAccountsJob?.cancel()
        availableAccountsJob = viewModelScope.launch {
            val unknownAccount = getString(Res.string.feature_pocket_unknown_account)
            pocketRepository.getAvailableAccountsToLink(state.clientId)
                .collect { dataState ->
                    trySendAction(ManagePocketAction.Internal.ReceiveAvailableAccounts(dataState, unknownAccount))
                }
        }
    }

    private fun openLinkAccounts() {
        updateState {
            it.copy(
                dialogState = ManagePocketDialogState.LinkAccounts,
            )
        }
        loadAvailableAccounts()
    }

    private fun dismissDialog() {
        updateState {
            it.copy(
                dialogState = null,
            )
        }
    }

    private fun openDelinkConfirmation(account: ManagePocketAccount) {
        updateState {
            it.copy(dialogState = ManagePocketDialogState.DelinkConfirmation(account))
        }
    }

    private fun updateSelectedAccount(accountId: Long, accountType: AccountType, selected: Boolean) {
        updateState {
            val identifier = "${accountId}_${accountType.name}"
            val updated = if (selected) {
                it.selectedAccountIdentifiers + identifier
            } else {
                it.selectedAccountIdentifiers - identifier
            }

            it.copy(selectedAccountIdentifiers = updated)
        }
    }

    private fun linkSelectedAccounts() {
        val accountsToLink = state.availableAccounts.filter {
            "${it.accountId}_${it.accountType.name}" in state.selectedAccountIdentifiers
        }

        if (accountsToLink.isEmpty()) return

        viewModelScope.launch {
            updateState { it.copy(dialogState = ManagePocketDialogState.Loading) }

            val payload = PocketLinkPayload(
                accountsDetail = accountsToLink.map {
                    PocketLinkPayload.AccountDetail(
                        accountId = it.accountId.toString(),
                        accountType = it.accountType,
                    )
                },
            )

            val explicitAccounts = accountsToLink.map { it.toDetailedPocketAccount() }

            when (
                val dataState = pocketRepository.linkAccounts(
                    payload = payload,
                    explicitlyAddedAccounts = explicitAccounts,
                    clientId = state.clientId,
                )
            ) {
                is DataState.Success -> {
                    updateState {
                        it.copy(
                            dialogState = null,
                            selectedAccountIdentifiers = emptySet(),
                            searchQuery = "",
                        )
                    }
                    loadLinkedAccounts(forceRefresh = true)
                }

                is DataState.Error -> {
                    updateState {
                        it.copy(
                            dialogState = ManagePocketDialogState.Error(
                                Res.string.feature_pocket_error_link_accounts,
                            ),
                        )
                    }
                }

                DataState.Loading -> Unit
            }
        }
    }

    private fun delinkAccount(account: ManagePocketAccount) {
        viewModelScope.launch {
            updateState { it.copy(dialogState = ManagePocketDialogState.Loading) }

            when (
                val dataState = pocketRepository.delinkAccounts(
                    pocketAccountMappingIds = listOf(account.mappingId),
                    clientId = state.clientId,
                )
            ) {
                is DataState.Success -> {
                    updateState { it.copy(dialogState = null) }
                    loadLinkedAccounts(forceRefresh = true)
                }

                is DataState.Error -> {
                    updateState {
                        it.copy(
                            dialogState = ManagePocketDialogState.Error(
                                Res.string.feature_pocket_error_delink_account,
                            ),
                        )
                    }
                }

                DataState.Loading -> Unit
            }
        }
    }

    private fun handleLinkedAccounts(dataState: DataState<List<DetailedPocketAccount>>, unknownAccount: String) {
        when (dataState) {
            DataState.Loading -> {
                updateState { it.copy(uiState = ManagePocketUiState.Loading) }
            }

            is DataState.Error -> {
                updateState {
                    it.copy(
                        uiState = ManagePocketUiState.Error(Res.string.feature_pocket_error_load_accounts),
                    )
                }
            }

            is DataState.Success -> {
                val linkedAccounts = dataState.data.map { it.toManagePocketAccount(unknownAccount) }
                updateState {
                    it.copy(
                        linkedAccounts = linkedAccounts,
                        uiState = ManagePocketUiState.Success,
                    )
                }
            }
        }
    }

    private fun handleAvailableAccounts(dataState: DataState<List<LinkableAccount>>, unknownAccount: String) {
        when (dataState) {
            DataState.Loading -> updateState {
                it.copy(isAvailableAccountsLoading = true)
            }

            is DataState.Error -> updateState {
                it.copy(
                    isAvailableAccountsLoading = false,
                    dialogState = ManagePocketDialogState.Error(
                        Res.string.feature_pocket_error_load_accounts,
                    ),
                )
            }

            is DataState.Success -> {
                val availableAccounts = dataState.data.map { account ->
                    account.toAvailablePocketAccount(unknownAccount)
                }
                updateState {
                    it.copy(
                        availableAccounts = availableAccounts,
                        isAvailableAccountsLoading = false,
                    )
                }
            }
        }
    }

    private fun DetailedPocketAccount.toManagePocketAccount(unknownAccount: String): ManagePocketAccount {
        return ManagePocketAccount(
            accountId = pocket.accountId,
            mappingId = pocket.id,
            name = productName ?: unknownAccount,
            accountNumber = pocket.accountNumber,
            accountType = pocket.accountType,
        )
    }

    private fun LinkableAccount.toAvailablePocketAccount(unknownAccount: String): AvailablePocketAccount {
        return AvailablePocketAccount(
            accountId = accountId,
            name = productName ?: unknownAccount,
            accountNumber = accountNumber.orEmpty(),
            accountType = accountType,
            balance = balance,
            currencyCode = currencyCode,
            currencyDisplaySymbol = currencyDisplaySymbol,
            decimalPlaces = decimalPlaces,
            status = status,
        )
    }

    private fun AvailablePocketAccount.toDetailedPocketAccount(): DetailedPocketAccount {
        return DetailedPocketAccount(
            pocket = PocketAccount(
                pocketId = -1,
                id = -1,
                accountId = accountId,
                accountType = accountType,
                accountNumber = accountNumber,
            ),
            productName = name,
            balance = balance,
            currencyCode = currencyCode,
            currencyDisplaySymbol = currencyDisplaySymbol,
            decimalPlaces = decimalPlaces,
            status = status,
        )
    }
}

internal data class ManagePocketState(
    val clientId: Long = 0,
    val linkedAccounts: List<ManagePocketAccount> = emptyList(),
    val availableAccounts: List<AvailablePocketAccount> = emptyList(),
    val selectedAccountIdentifiers: Set<String> = emptySet(),
    val selectedTab: AccountType = AccountType.SAVINGS,
    val searchQuery: String = "",
    val uiState: ManagePocketUiState = ManagePocketUiState.Loading,
    val dialogState: ManagePocketDialogState? = null,
    val isAvailableAccountsLoading: Boolean = false,
)

internal sealed interface ManagePocketUiState {
    data object Loading : ManagePocketUiState
    data class ErrorString(val message: String) : ManagePocketUiState
    data class Error(val message: org.jetbrains.compose.resources.StringResource) : ManagePocketUiState
    data object Success : ManagePocketUiState
}

data class ManagePocketAccount(
    val accountId: Long,
    val mappingId: Long,
    val name: String,
    val accountNumber: String,
    val accountType: AccountType,
)

internal data class AvailablePocketAccount(
    val accountId: Long,
    val name: String,
    val accountNumber: String,
    val accountType: AccountType,
    val balance: Double? = null,
    val currencyCode: String? = null,
    val currencyDisplaySymbol: String? = null,
    val decimalPlaces: Int? = null,
    val status: AccountStatus? = null,
)

internal sealed interface ManagePocketDialogState {
    data object LinkAccounts : ManagePocketDialogState
    data object Loading : ManagePocketDialogState
    data class DelinkConfirmation(val account: ManagePocketAccount) : ManagePocketDialogState
    data class Error(val message: org.jetbrains.compose.resources.StringResource) : ManagePocketDialogState
}

internal sealed interface ManagePocketEvent {
    data object NavigateBack : ManagePocketEvent
}

internal sealed interface ManagePocketAction {
    data object NavigateBack : ManagePocketAction
    data object Retry : ManagePocketAction
    data object OpenLinkAccounts : ManagePocketAction
    data object DismissDialog : ManagePocketAction
    data object LinkSelectedAccounts : ManagePocketAction
    data class OpenDelinkConfirmation(val account: ManagePocketAccount) : ManagePocketAction
    data class DelinkAccount(val account: ManagePocketAccount) : ManagePocketAction
    data class TabSelected(val accountType: AccountType) : ManagePocketAction
    data class SearchQueryChanged(val query: String) : ManagePocketAction
    data class AccountSelectionChanged(
        val accountId: Long,
        val accountType: AccountType,
        val selected: Boolean,
    ) : ManagePocketAction

    sealed interface Internal : ManagePocketAction {
        data class ReceiveLinkedAccounts(
            val dataState: DataState<List<DetailedPocketAccount>>,
            val unknownAccount: String,
        ) : Internal

        data class ReceiveAvailableAccounts(
            val dataState: DataState<List<LinkableAccount>>,
            val unknownAccount: String,
        ) : Internal
    }
}
