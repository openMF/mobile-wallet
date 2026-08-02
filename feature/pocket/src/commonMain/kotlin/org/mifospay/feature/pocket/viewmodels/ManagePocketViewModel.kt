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

// Upstream PR #2057 (manage-pocket) introduced this VM on top of the pre-migration
// `getDetailedPocketAccounts(clientId, forceRefresh): Flow<DataState<...>>` read
// path. This branch had already replaced that read path with the Store5 LEDGER
// `getDetailedPocketAccountsScreen(clientId, scope): Flow<ScreenState<...>>` (Phase-5
// Batch-2 — Room SoT + CACHE_FIRST_SWR + no in-memory cache), so this VM is
// re-authored to consume the ScreenState stream via a `refreshTrigger` +
// `flatMapLatest` re-subscribe (the same pattern `PocketDashboardViewModel` and
// `BeneficiaryListViewModel` use).
//
// The manage-pocket linkable-accounts Store5 migration THEN did the same for the
// second read path: `getAvailableAccountsToLink(clientId): Flow<DataState<...>>`
// was upstream PR #2057's DataState-shaped stream against the in-memory
// `PocketPreferencesDataSource.linkableAccounts` cache; this VM now consumes the
// Store5-backed `getAvailableAccountsToLinkScreen(clientId, scope): Flow<ScreenState<...>>`
// with a direct `collectLatest { handleAvailableAccounts(screenState, ...) }`
// fold (no `Internal.ReceiveAvailableAccounts` bridge). The UI-side sealed
// hierarchy (`ManagePocketState` / `ManagePocketAction` / `ManagePocketDialogState` /
// `ManagePocketEvent` / `ManagePocketAccount` / `AvailablePocketAccount`) is
// preserved so `ManagePocketScreen` is untouched by either migration.

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile_wallet.feature.pocket.generated.resources.Res
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_delink_account
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_link_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_load_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_unknown_account
import org.jetbrains.compose.resources.getString
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.payload.PocketLinkPayload
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.model.pocket.PocketAccount
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
    // Phase-5 Batch-2: refresh trigger cadence. Each emission re-subscribes to a
    // fresh `getDetailedPocketAccountsScreen(...)` stream — the Store5 native
    // way to force a re-fetch (parity with PocketDashboardViewModel /
    // BeneficiaryListViewModel). Replaces the pre-migration `forceRefresh: Boolean`
    // signal that used to flow through `getDetailedPocketAccounts(...)`.
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private var availableAccountsJob: Job? = null

    init {
        loadLinkedAccounts()
    }

    private fun loadLinkedAccounts() {
        viewModelScope.launch {
            val unknownAccount = getString(Res.string.feature_pocket_unknown_account)
            refreshTrigger.onStart { emit(Unit) }
                .flatMapLatest {
                    pocketRepository.getDetailedPocketAccountsScreen(
                        clientId = state.clientId,
                        scope = viewModelScope,
                    )
                }
                .collectLatest { screenState ->
                    handleLinkedAccounts(screenState, unknownAccount)
                }
        }
    }

    private fun updateState(update: (ManagePocketState) -> ManagePocketState) {
        mutableStateFlow.update(update)
    }

    override fun handleAction(action: ManagePocketAction) {
        when (action) {
            ManagePocketAction.NavigateBack -> sendEvent(ManagePocketEvent.NavigateBack)
            ManagePocketAction.Retry -> retry()
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
        }
    }

    private fun retry() {
        updateState { it.copy(uiState = ManagePocketUiState.Loading) }
        refreshTrigger.tryEmit(Unit)
    }

    private fun loadAvailableAccounts() {
        availableAccountsJob?.cancel()
        availableAccountsJob = viewModelScope.launch {
            val unknownAccount = getString(Res.string.feature_pocket_unknown_account)
            // manage-pocket linkable-accounts Store5 migration — consume the
            // ScreenState-shaped read path (`getAvailableAccountsToLinkScreen`)
            // that reads through the `linkableAccounts` Store5 store. Replaces
            // the pre-migration DataState-shaped `getAvailableAccountsToLink`
            // path (which is preserved on the repository interface for any
            // residual caller compat but no longer consumed by this VM).
            pocketRepository.getAvailableAccountsToLinkScreen(
                clientId = state.clientId,
                scope = viewModelScope,
            ).collectLatest { screenState ->
                handleAvailableAccounts(screenState, unknownAccount)
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
                pocketRepository.linkAccounts(
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
                    // Phase-5 Batch-2: re-fire the read stream so the newly-linked
                    // rows flow back through the Store5 pipe (Room-write is done by
                    // the store's writer on the next fetch — the repo write path is
                    // pure-online per RULE-GAP-IDEA-FIRST-001 / D1).
                    refreshTrigger.tryEmit(Unit)
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
                pocketRepository.delinkAccounts(
                    pocketAccountMappingIds = listOf(account.mappingId),
                    clientId = state.clientId,
                )
            ) {
                is DataState.Success -> {
                    updateState { it.copy(dialogState = null) }
                    // Phase-5 Batch-2: same rationale as `linkSelectedAccounts` —
                    // re-subscribe to pull the updated set through the store.
                    refreshTrigger.tryEmit(Unit)
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

    /**
     * Phase-5 Batch-2: fold the 6-branch [ScreenState] into the feature's UiState.
     * Replaces the pre-migration `handleLinkedAccounts(DataState<...>)` on the
     * same VM — the same `linkedAccounts` bucket population runs on the
     * `Content` branch; `Empty` also renders `linkedAccounts = emptyList()` +
     * `Success` (matching upstream's `DataState.Success(emptyList())` semantics
     * — the empty-state UI lives inside `ManagePocketContent`, not a distinct
     * UiState). `NoNetwork` / `Unauthenticated` fold into `Error` until a
     * future phase wires per-branch surfaces.
     */
    private fun handleLinkedAccounts(
        screenState: ScreenState<List<DetailedPocketAccount>>,
        unknownAccount: String,
    ) {
        when (screenState) {
            is ScreenState.Loading -> {
                updateState { it.copy(uiState = ManagePocketUiState.Loading) }
            }

            is ScreenState.Empty -> {
                updateState {
                    it.copy(
                        linkedAccounts = emptyList(),
                        uiState = ManagePocketUiState.Success,
                    )
                }
            }

            is ScreenState.Content -> {
                val linkedAccounts = screenState.data.map { it.toManagePocketAccount(unknownAccount) }
                updateState {
                    it.copy(
                        linkedAccounts = linkedAccounts,
                        uiState = ManagePocketUiState.Success,
                    )
                }
            }

            is ScreenState.Error -> {
                updateState {
                    it.copy(
                        uiState = ManagePocketUiState.Error(Res.string.feature_pocket_error_load_accounts),
                    )
                }
            }

            is ScreenState.NoNetwork -> {
                updateState {
                    it.copy(
                        uiState = ManagePocketUiState.Error(Res.string.feature_pocket_error_load_accounts),
                    )
                }
            }

            is ScreenState.Unauthenticated -> {
                updateState {
                    it.copy(
                        uiState = ManagePocketUiState.Error(Res.string.feature_pocket_error_load_accounts),
                    )
                }
            }
        }
    }

    /**
     * manage-pocket linkable-accounts Store5 migration — fold the 6-branch
     * [ScreenState] into the link-accounts dialog's uiState. Replaces the
     * pre-migration `handleAvailableAccounts(DataState<...>)` on the same VM
     * — the same `availableAccounts` bucket population runs on the `Content`
     * branch. `Empty` renders `availableAccounts = emptyList()` +
     * `isAvailableAccountsLoading = false` (an empty-linkable-set is a valid
     * outcome — every eligible account is already linked). `NoNetwork` /
     * `Unauthenticated` / `Error` fold into the dialog's Error surface (parity
     * with the pre-migration DataState.Error branch).
     */
    private fun handleAvailableAccounts(
        screenState: ScreenState<List<LinkableAccount>>,
        unknownAccount: String,
    ) {
        when (screenState) {
            is ScreenState.Loading -> updateState {
                it.copy(isAvailableAccountsLoading = true)
            }

            is ScreenState.Empty -> updateState {
                it.copy(
                    availableAccounts = emptyList(),
                    isAvailableAccountsLoading = false,
                )
            }

            is ScreenState.Content -> {
                val availableAccounts = screenState.data.map { account ->
                    account.toAvailablePocketAccount(unknownAccount)
                }
                updateState {
                    it.copy(
                        availableAccounts = availableAccounts,
                        isAvailableAccountsLoading = false,
                    )
                }
            }

            is ScreenState.Error, is ScreenState.NoNetwork, ScreenState.Unauthenticated -> {
                updateState {
                    it.copy(
                        isAvailableAccountsLoading = false,
                        dialogState = ManagePocketDialogState.Error(
                            Res.string.feature_pocket_error_load_accounts,
                        ),
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
        val temporaryId = -kotlin.random.Random.nextLong(1L, Long.MAX_VALUE)
        return DetailedPocketAccount(
            pocket = PocketAccount(
                pocketId = temporaryId,
                id = temporaryId,
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

    // manage-pocket linkable-accounts Store5 migration: the pre-migration
    // `Internal.ReceiveAvailableAccounts(DataState<List<LinkableAccount>>, unknownAccount)`
    // is REMOVED — the ScreenState stream from the new `getAvailableAccountsToLinkScreen`
    // is consumed DIRECTLY in `loadAvailableAccounts()` (via `collectLatest {
    // handleAvailableAccounts(screenState, unknownAccount) }`) without the
    // two-step `trySendAction` indirection (parity with the linked-accounts
    // read path in `loadLinkedAccounts()`). No `Internal` sealed hierarchy
    // remains — both reads now consume ScreenState directly.
}
