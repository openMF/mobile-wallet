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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kpt.core.base.store.screen.ScreenState
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.ui.utils.BaseViewModel

internal class PocketDashboardViewModel(
    private val pocketRepository: PocketRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<PocketDashboardState, PocketDashboardEvent, PocketDashboardAction>(
    initialState = PocketDashboardState(
        clientId = requireNotNull(userPreferencesRepository.clientId.value),
    ),
) {
    // Template idiom (core-base/store): hold the native ScreenDataStream and
    // expose its pre-decided `state` straight to the Screen's `ScreenContent`.
    // The pre-Batch-2 `refreshTrigger` + `flatMapLatest` re-subscribe fold and
    // the 6→4 `handleScreenState` / `populateFromContent` fork are GONE — the
    // stream's DecisionEngine owns every Loading / Empty / NoNetwork /
    // Unauthenticated / Error / Content transition, and `refresh()` drives
    // pull-to-refresh + retry.
    //
    // Exposed as `uiState` (NOT `state`) because [BaseViewModel] already owns a
    // `protected val state: S` for the MVI action/event state — the two names
    // cannot collide. Bucketing the raw accounts into savings/loan/share
    // sections + the multi-currency total now happens screen-side (it needs
    // `stringResource` fallbacks for missing product name / status), so the
    // ViewModel carries the raw `List<DetailedPocketAccount>` payload straight
    // through.
    private val stream = pocketRepository.getDetailedPocketAccountsStream(
        clientId = state.clientId,
        scope = viewModelScope,
    )

    val uiState: StateFlow<ScreenState<List<DetailedPocketAccount>>> = stream.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    fun retry() = stream.refresh()

    override fun handleAction(action: PocketDashboardAction) {
        when (action) {
            PocketDashboardAction.NavigateBack -> sendEvent(PocketDashboardEvent.NavigateBack)
            PocketDashboardAction.ManagePocket -> sendEvent(PocketDashboardEvent.ManagePocket)
            PocketDashboardAction.LinkFirstAccount -> sendEvent(PocketDashboardEvent.ManagePocket)
            PocketDashboardAction.Retry -> retry()
            PocketDashboardAction.Refresh -> retry()
            is PocketDashboardAction.NavigateToLoanDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToLoanDetail(action.accountId))
            }
            is PocketDashboardAction.NavigateToSavingsDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToSavingsDetail(action.accountId))
            }
            is PocketDashboardAction.NavigateToShareDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToShareDetail(action.accountId))
            }
        }
    }
}

data class PocketDashboardState(
    val clientId: Long = 0,
)

data class DetailedPocket(
    val accountId: Long,
    val name: String,
    val accountNumber: String,
    val balanceOrStatus: String,
    val status: AccountStatus,
)

internal sealed interface PocketDashboardEvent {
    data object NavigateBack : PocketDashboardEvent
    data object ManagePocket : PocketDashboardEvent
    data class NavigateToLoanDetail(val accountId: Long) : PocketDashboardEvent
    data class NavigateToSavingsDetail(val accountId: Long) : PocketDashboardEvent
    data class NavigateToShareDetail(val accountId: Long) : PocketDashboardEvent
}

internal sealed interface PocketDashboardAction {
    data object NavigateBack : PocketDashboardAction
    data class NavigateToLoanDetail(val accountId: Long) : PocketDashboardAction
    data class NavigateToSavingsDetail(val accountId: Long) : PocketDashboardAction
    data class NavigateToShareDetail(val accountId: Long) : PocketDashboardAction
    data object ManagePocket : PocketDashboardAction
    data object LinkFirstAccount : PocketDashboardAction
    data object Refresh : PocketDashboardAction
    data object Retry : PocketDashboardAction
}
