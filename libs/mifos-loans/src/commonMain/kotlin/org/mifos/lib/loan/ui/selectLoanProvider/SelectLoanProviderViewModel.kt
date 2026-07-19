/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.lib.loan.ui.selectLoanProvider

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import org.mifospay.core.ui.utils.BaseViewModel

/**
 * `ViewModel` for the Select Loan Provider screen — the new first step of the loan-application
 * wizard.
 *
 * The list of providers is static placeholder data for now (no backend to source it from — see
 * [LoanProviderOption] kdoc). Selecting a provider simply forwards the client into the existing
 * wizard (Select Loan Type onwards); the chosen provider doesn't yet affect any downstream data.
 *
 * @param savedStateHandle Handle used to read the [SelectLoanProviderRoute] navigation arguments.
 */
internal class SelectLoanProviderViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SelectLoanProviderState, SelectLoanProviderEvent, SelectLoanProviderAction>(
    initialState = run {
        val route = savedStateHandle.toRoute<SelectLoanProviderRoute>()
        SelectLoanProviderState(
            clientId = route.clientId,
            providers = defaultLoanProviders,
        )
    },
) {

    override fun handleAction(action: SelectLoanProviderAction) {
        when (action) {
            SelectLoanProviderAction.NavigateBack -> {
                sendEvent(SelectLoanProviderEvent.NavigateBack)
            }

            is SelectLoanProviderAction.ProviderClicked -> {
                sendEvent(
                    SelectLoanProviderEvent.NavigateToSelectLoanType(
                        clientId = state.clientId,
                        providerId = action.providerId,
                    ),
                )
            }
        }
    }
}

/**
 * A dummy/placeholder loan provider (e.g. "HDFC Bank", "SBI") shown on the Select Loan Provider
 * screen.
 *
 * There is no backend for loan providers yet — this is illustrative UI data only, per the product
 * requirement to expose a `loanProvider` selection step ahead of the (currently dummy) product
 * flow. Once a real per-provider backend exists, this should be sourced from a repository instead
 * of hardcoded here.
 *
 * @property id A stable identifier for the provider.
 * @property name The provider's display name.
 */
internal data class LoanProviderOption(val id: String, val name: String)

/**
 * The static placeholder list of loan providers. See [LoanProviderOption] for why this is
 * hardcoded rather than fetched.
 */
internal val defaultLoanProviders = listOf(
    LoanProviderOption(id = "hdfc", name = "HDFC Bank"),
    LoanProviderOption(id = "sbi", name = "SBI"),
    LoanProviderOption(id = "icici", name = "ICICI Bank"),
    LoanProviderOption(id = "axis", name = "Axis Bank"),
)

/**
 * UI state for the Select Loan Provider screen.
 *
 * @property clientId The id of the client applying for a loan.
 * @property providers The list of selectable (currently dummy) loan providers.
 */
internal data class SelectLoanProviderState(
    val clientId: Long,
    val providers: List<LoanProviderOption> = defaultLoanProviders,
)

/**
 * One-shot navigation events emitted by [SelectLoanProviderViewModel].
 */
internal sealed interface SelectLoanProviderEvent {
    data object NavigateBack : SelectLoanProviderEvent

    data class NavigateToSelectLoanType(val clientId: Long, val providerId: String) : SelectLoanProviderEvent
}

/**
 * User actions handled by [SelectLoanProviderViewModel].
 */
internal sealed interface SelectLoanProviderAction {
    data object NavigateBack : SelectLoanProviderAction

    data class ProviderClicked(val providerId: String) : SelectLoanProviderAction
}
