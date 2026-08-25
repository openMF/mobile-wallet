/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.pocket.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.submit.SubmitState
import kpt.core.base.store.submit.submitHandler
import mifos_pay.feature.pocket.generated.resources.Res
import mifos_pay.feature.pocket.generated.resources.feature_pocket_error_delink_account
import mifos_pay.feature.pocket.generated.resources.feature_pocket_error_link_accounts
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.ui.utils.BaseViewModel

@OptIn(ExperimentalCoroutinesApi::class)
internal class ManagePocketViewModel(
    private val pocketRepository: PocketRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<ManagePocketState, ManagePocketEvent, ManagePocketAction>(
    initialState = ManagePocketState(
        clientId = requireNotNull(userPreferencesRepository.clientId.value),
    ),
) {

    private val linkedStream = pocketRepository.getDetailedPocketAccountsStream(
        clientId = state.clientId,
        scope = viewModelScope,
    )

    val linkedUiState: StateFlow<ScreenState<List<DetailedPocketAccount>>> = linkedStream.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    val linkedFreshness: StateFlow<FreshnessSignal> = linkedStream.freshness.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FreshnessSignal.initial(),
    )

    private val availableStream = pocketRepository.getAvailableAccountsToLinkStream(
        clientId = state.clientId,
        scope = viewModelScope,
    )

    val availableUiState: StateFlow<ScreenState<List<LinkableAccount>>> = availableStream.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    private val submitLink = viewModelScope.submitHandler<Unit>()
    private val submitDelink = viewModelScope.submitHandler<Unit>()

    init {
        observeLinkSubmit()
        observeDelinkSubmit()
    }

    private fun observeLinkSubmit() {
        submitLink.state
            .onEach { submitState ->
                when (submitState) {
                    is SubmitState.Submitting -> {
                        updateState { it.copy(dialogState = ManagePocketDialogState.Loading) }
                    }

                    is SubmitState.Submitted -> {
                        updateState {
                            it.copy(
                                dialogState = null,
                                selectedAccountIdentifiers = emptySet(),
                                searchQuery = "",
                            )
                        }
                        availableStream.refresh()
                        linkedStream.refresh()
                        submitLink.reset()
                    }

                    is SubmitState.Failed -> {
                        updateState {
                            it.copy(
                                dialogState = ManagePocketDialogState.Error(
                                    Res.string.feature_pocket_error_link_accounts,
                                ),
                            )
                        }
                        submitLink.reset()
                    }

                    SubmitState.Idle -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeDelinkSubmit() {
        submitDelink.state
            .onEach { submitState ->
                when (submitState) {
                    is SubmitState.Submitting -> {
                        updateState { it.copy(dialogState = ManagePocketDialogState.Loading) }
                    }

                    is SubmitState.Submitted -> {
                        updateState { it.copy(dialogState = null) }
                        availableStream.refresh()
                        linkedStream.refresh()
                        submitDelink.reset()
                    }

                    is SubmitState.Failed -> {
                        updateState {
                            it.copy(
                                dialogState = ManagePocketDialogState.Error(
                                    Res.string.feature_pocket_error_delink_account,
                                ),
                            )
                        }
                        submitDelink.reset()
                    }

                    SubmitState.Idle -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateState(update: (ManagePocketState) -> ManagePocketState) {
        mutableStateFlow.update(update)
    }

    override fun handleAction(action: ManagePocketAction) {
        when (action) {
            ManagePocketAction.NavigateBack -> sendEvent(ManagePocketEvent.NavigateBack)
            ManagePocketAction.Retry -> linkedStream.refresh()
            ManagePocketAction.RetryAvailable -> availableStream.refresh()
            ManagePocketAction.OpenLinkAccounts -> openLinkAccounts()
            ManagePocketAction.DismissDialog -> dismissDialog()
            is ManagePocketAction.OpenDelinkConfirmation -> openDelinkConfirmation(
                accountId = action.accountId,
                accountName = action.accountName,
                accountNumber = action.accountNumber,
            )
            is ManagePocketAction.TabSelected -> updateState { it.copy(selectedTab = action.accountType) }
            is ManagePocketAction.SearchQueryChanged -> updateState { it.copy(searchQuery = action.query) }
            is ManagePocketAction.AccountSelectionChanged -> updateSelectedAccount(
                accountId = action.accountId,
                accountType = action.accountType,
                selected = action.selected,
            )
            is ManagePocketAction.LinkSelectedAccounts -> linkSelectedAccounts(action.explicitlyAddedAccounts)
            is ManagePocketAction.DelinkAccount -> delinkAccount(action.mappingId)
        }
    }

    private fun openLinkAccounts() {
        updateState {
            it.copy(
                dialogState = ManagePocketDialogState.LinkAccounts,
            )
        }
    }

    private fun dismissDialog() {
        updateState {
            it.copy(
                dialogState = null,
                selectedAccountIdentifiers = emptySet(),
                searchQuery = "",
            )
        }
    }

    private fun openDelinkConfirmation(accountId: Long, accountName: String, accountNumber: String) {
        updateState {
            it.copy(dialogState = ManagePocketDialogState.DelinkConfirmation(accountId, accountName, accountNumber))
        }
    }

    private fun updateSelectedAccount(
        accountId: Long,
        accountType: AccountType,
        selected: Boolean,
    ) {
        updateState { currentState ->
            val identifier = "${accountId}_${accountType.name}"
            val newSelection = if (selected) {
                currentState.selectedAccountIdentifiers + identifier
            } else {
                currentState.selectedAccountIdentifiers - identifier
            }
            currentState.copy(selectedAccountIdentifiers = newSelection)
        }
    }

    private fun linkSelectedAccounts(explicitlyAddedAccounts: List<DetailedPocketAccount>) {
        if (state.selectedAccountIdentifiers.isEmpty()) return

        submitLink.submit {
            pocketRepository.linkAccounts(
                explicitlyAddedAccounts = explicitlyAddedAccounts,
                clientId = state.clientId,
            )
        }
    }

    private fun delinkAccount(mappingId: Long) {
        submitDelink.submit {
            pocketRepository.delinkAccounts(
                pocketAccountMappingIds = listOf(mappingId),
                clientId = state.clientId,
            )
        }
    }
}

internal data class ManagePocketState(
    val clientId: Long = 0,
    val selectedAccountIdentifiers: Set<String> = emptySet(),
    val selectedTab: AccountType = AccountType.SAVINGS,
    val searchQuery: String = "",
    val dialogState: ManagePocketDialogState? = null,
)

internal sealed interface ManagePocketDialogState {
    data object LinkAccounts : ManagePocketDialogState
    data object Loading : ManagePocketDialogState
    data class DelinkConfirmation(
        val accountId: Long,
        val accountName: String,
        val accountNumber: String,
    ) : ManagePocketDialogState
    data class Error(val message: org.jetbrains.compose.resources.StringResource) : ManagePocketDialogState
}

internal sealed interface ManagePocketEvent {
    data object NavigateBack : ManagePocketEvent
}

internal sealed interface ManagePocketAction {
    data object NavigateBack : ManagePocketAction
    data object Retry : ManagePocketAction
    data object RetryAvailable : ManagePocketAction
    data object OpenLinkAccounts : ManagePocketAction
    data object DismissDialog : ManagePocketAction
    data class LinkSelectedAccounts(val explicitlyAddedAccounts: List<DetailedPocketAccount>) : ManagePocketAction
    data class OpenDelinkConfirmation(
        val accountId: Long,
        val accountName: String,
        val accountNumber: String,
    ) : ManagePocketAction
    data class DelinkAccount(val mappingId: Long) : ManagePocketAction
    data class TabSelected(val accountType: AccountType) : ManagePocketAction
    data class SearchQueryChanged(val query: String) : ManagePocketAction
    data class AccountSelectionChanged(
        val accountId: Long,
        val accountType: AccountType,
        val selected: Boolean,
    ) : ManagePocketAction
}
