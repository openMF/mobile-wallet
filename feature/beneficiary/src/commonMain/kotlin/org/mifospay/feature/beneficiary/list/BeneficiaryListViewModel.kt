/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.beneficiary.list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.ScreenState
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.beneficiary.addupdatebeneficiary.BeneficiaryAddEditType

@OptIn(ExperimentalCoroutinesApi::class)
class BeneficiaryListViewModel(
    private val userRepository: UserPreferencesRepository,
    private val repository: SelfServiceRepository,
    private val json: Json,
) : BaseViewModel<BeneficiaryListState, BeneficiaryListEvent, BeneficiaryListAction>(
    initialState = run {
        val clientId = requireNotNull(userRepository.clientId.value)
        val defaultAccount = userRepository.defaultAccountId.value

        BeneficiaryListState(
            clientId = clientId,
            defaultAccountId = defaultAccount,
        )
    },
) {
    /**
     * Explicit refresh trigger - only emits when refresh is explicitly requested.
     * This prevents unwanted refreshes when showing bottom sheets.
     */
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    val accountState = refreshTrigger
        .flatMapLatest {
            // Phase-5 Batch-1 LEDGER read (GOAL D13) — switched from the
            // transitional `getBeneficiaryList()` (`asScreenStateFlow` shim over
            // the raw Ktorfit flow) to the store-native `getBeneficiaryListScreen(...)`
            // that consumes the `beneficiary` Store5 read (`createStore` + Room SoT
            // + CACHE_FIRST_SWR + atomic replacePage). Same `Flow<ScreenState<List<Beneficiary>>>`
            // shape; consumer branches are unchanged. Requires `viewModelScope` for
            // the stream's internal reconnect + periodic + SWR side-fetch coroutines.
            //
            // Note: the VM's explicit refreshTrigger still fires on `RefreshList`
            // action — each refresh re-subscribes to a new `asScreenStream(...)`
            // stream which triggers a fetch via the store's onStart-emit;
            // asScreenStream's own auto-refresh (reconnect, periodic) coexists
            // with the manual refresh cleanly.
            repository.getBeneficiaryListScreen(state.clientId, scope = viewModelScope)
        }
        .mapLatest {
            // Fold the 6-branch ScreenState back into the feature's 3-branch
            // ViewState (Loading/Error/Content). Empty projects to Content with
            // an empty list so the Screen's existing empty-list rendering path
            // continues to fire. NoNetwork/Unauthenticated are folded into the
            // Error branch until Phase-4 wires per-branch surfaces.
            when (it) {
                is ScreenState.Loading -> BeneficiaryListState.ViewState.Loading

                is ScreenState.Empty -> BeneficiaryListState.ViewState.Content(
                    beneficiaries = emptyList(),
                )

                is ScreenState.Content -> BeneficiaryListState.ViewState.Content(
                    beneficiaries = it.data,
                )

                is ScreenState.Error ->
                    BeneficiaryListState.ViewState.Error(it.error.message.toString())

                is ScreenState.NoNetwork ->
                    BeneficiaryListState.ViewState.Error("No network. Please check your connection.")

                is ScreenState.Unauthenticated ->
                    BeneficiaryListState.ViewState.Error("Session expired. Please log in again.")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BeneficiaryListState.ViewState.Loading,
        )

    override fun handleAction(action: BeneficiaryListAction) {
        when (action) {
            is BeneficiaryListAction.AddTPTBeneficiary -> {
                sendEvent(BeneficiaryListEvent.OnAddOrEditTPTBeneficiary(BeneficiaryAddEditType.AddItem()))
            }

            is BeneficiaryListAction.EditBeneficiary -> {
                viewModelScope.launch {
                    val beneficiary = json.encodeToString<Beneficiary>(action.beneficiary)
                    sendEvent(
                        BeneficiaryListEvent.OnAddOrEditTPTBeneficiary(
                            BeneficiaryAddEditType.EditItem(beneficiary),
                        ),
                    )
                }
            }

            is BeneficiaryListAction.RefreshList -> {
                refreshTrigger.tryEmit(Unit)
            }
        }
    }

    /**
     * Refreshes the beneficiary list.
     * Called after successful delete from DeleteBeneficiaryViewModel.
     */
    fun refreshBeneficiaryList() {
        refreshTrigger.tryEmit(Unit)
    }
}

data class BeneficiaryListState(
    val clientId: Long,
    val defaultAccountId: Long? = null,
) {
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
            val beneficiaries: List<Beneficiary>,
        ) : ViewState {
            override val hasFab: Boolean get() = true
            override val isPullToRefreshEnabled: Boolean get() = true
        }
    }
}

sealed interface BeneficiaryListEvent {
    data class OnAddOrEditTPTBeneficiary(val type: BeneficiaryAddEditType) : BeneficiaryListEvent

    data class ShowToast(val message: StringResource) : BeneficiaryListEvent
}

sealed interface BeneficiaryListAction {
    data object AddTPTBeneficiary : BeneficiaryListAction
    data class EditBeneficiary(val beneficiary: Beneficiary) : BeneficiaryListAction
    data object RefreshList : BeneficiaryListAction
}
