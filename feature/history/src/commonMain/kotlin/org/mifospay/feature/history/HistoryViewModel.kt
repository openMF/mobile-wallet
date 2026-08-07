/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history

import androidx.lifecycle.viewModelScope
import com.mobilebytelabs.kmptoolkit.pdfgenerator.ExperimentalPdfGeneratorApi
import com.mobilebytelabs.kmptoolkit.pdfgenerator.PageConfig
import com.mobilebytelabs.kmptoolkit.pdfgenerator.PdfError
import com.mobilebytelabs.kmptoolkit.pdfgenerator.PdfGenerator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile_wallet.feature.history.generated.resources.Res
import mobile_wallet.feature.history.generated.resources.feature_history_error
import mobile_wallet.feature.history.generated.resources.feature_history_no_account
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.history.components.HistoryHtmlTemplate

class HistoryViewModel
@OptIn(ExperimentalPdfGeneratorApi::class)
constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val repository: SelfServiceRepository,
    private val pdfGenerator: PdfGenerator,
) : BaseViewModel<HistoryState, HistoryEvent, HistoryAction>(
    initialState = run {
        val clientId = requireNotNull(preferencesRepository.clientId.value)
        HistoryState(
            clientId = clientId,
            viewState = HistoryState.ViewState.Loading,
        )
    },
) {
    init {
        loadActiveAccounts()
    }

    override fun handleAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.SetTransactionType -> handleSetTransactionType(action.filter)

            is HistoryAction.ViewTransaction -> handleViewTransaction(action.transferId)

            is HistoryAction.OnFilterClick -> handleFilterClick()

            is HistoryAction.OnApplyFilterClick -> handleApplyFilterClick()

            is HistoryAction.SetSelectedAccount -> handleSetSelectedAccount(action.account)

            is HistoryAction.ExportPdf -> {
                viewModelScope.launch {
                    exportPdf()
                }
            }

            HistoryAction.ClearFilters -> handleClearFilters()
        }
    }

    private fun loadTransactions(accountId: Long) {
        repository.getTransactions(accountId, null).onEach { result ->

            when (result) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(viewState = HistoryState.ViewState.Error(Res.string.feature_history_error))
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(viewState = HistoryState.ViewState.Loading)
                    }
                }

                is DataState.Success -> {
                    val transactions = result.data
                    if (transactions.isNotEmpty()) {
                        mutableStateFlow.update {
                            it.copy(
                                transactions = transactions,
                                viewState = HistoryState.ViewState.Content(transactions),
                            )
                        }
                        applyFilter(transactions)
                    } else {
                        mutableStateFlow.update {
                            it.copy(
                                transactions = transactions,
                                viewState = HistoryState.ViewState.Empty,
                            )
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadActiveAccounts() {
        repository.getActiveAccounts(state.clientId).onEach { result ->
            when (result) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(viewState = HistoryState.ViewState.Error(Res.string.feature_history_error))
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(viewState = HistoryState.ViewState.Loading)
                    }
                }

                is DataState.Success -> {
                    val accounts = result.data
                    if (accounts.isNotEmpty()) {
                        loadTransactions(accounts.first().id)
                        mutableStateFlow.update { state ->
                            state.copy(
                                accounts = accounts,
                                selectedAccount = accounts.firstOrNull(),
                            )
                        }
                    } else {
                        mutableStateFlow.update {
                            it.copy(viewState = HistoryState.ViewState.Error(Res.string.feature_history_no_account))
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun handleClearFilters() {
        mutableStateFlow.update {
            it.copy(
                selectedTransactionType = TransactionType.OTHER,
                showFilter = false,
            )
        }
        applyFilter(state.transactions)
    }

    private fun handleApplyFilterClick() {
        mutableStateFlow.update {
            it.copy(
                showFilter = false,
            )
        }
        val accountIdOfLoadedTransactions: Long? =
            if (state.transactions.isNotEmpty()) state.transactions.first().accountId else null

        if (state.selectedAccount?.id == accountIdOfLoadedTransactions) {
            applyFilter(state.transactions)
        } else {
            state.selectedAccount?.let { account ->
                loadTransactions(account.id)
            }
        }
    }

    private fun handleSetTransactionType(filter: TransactionType) {
        mutableStateFlow.update {
            it.copy(selectedTransactionType = filter)
        }
    }

    private fun handleSetSelectedAccount(account: Account) {
        mutableStateFlow.update {
            it.copy(selectedAccount = account)
        }
    }

    private fun handleViewTransaction(transferId: Long) {
        sendEvent(HistoryEvent.OnTransactionDetail(transferId))
    }

    private fun handleFilterClick() {
        mutableStateFlow.update {
            it.copy(showFilter = !state.showFilter)
        }
    }

    private fun applyFilter(transactions: List<Transaction>) {
        val filter = state.selectedTransactionType
        val filteredTransactions = transactions.filter {
            if (filter == TransactionType.OTHER) {
                true
            } else {
                it.transactionType == filter
            }
        }

        mutableStateFlow.update {
            it.copy(
                viewState = HistoryState.ViewState.Content(filteredTransactions),
            )
        }
    }

    @OptIn(ExperimentalPdfGeneratorApi::class)
    private suspend fun exportPdf() {
        if (state.isExportingPdf) return
        val content = state.viewState as? HistoryState.ViewState.Content ?: return

        mutableStateFlow.update { it.copy(isExportingPdf = true) }
        try {
            val html = HistoryHtmlTemplate.generate(
                accountNumber = state.selectedAccount?.number.orEmpty(),
                transactionType = state.selectedTransactionType.name,
                transactions = content.list,
            )

            try {
                pdfGenerator.generateAndSharePdf(
                    htmlContent = html,
                    fileName = "transaction_history.pdf",
                    pageConfig = PageConfig(),
                )
            } catch (e: PdfError) {
                sendEvent(HistoryEvent.PdfExportError(message = "Couldn't export PDF: ${e.message}"))
            }
        } finally {
            mutableStateFlow.update { it.copy(isExportingPdf = false) }
        }
    }
}

data class HistoryState(
    val clientId: Long,
    val viewState: ViewState,
    val selectedTransactionType: TransactionType = TransactionType.OTHER,
    val transactions: List<Transaction> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val showFilter: Boolean = false,
    val isExportingPdf: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Empty : ViewState
        data class Error(val message: StringResource) : ViewState
        data class Content(val list: List<Transaction>) : ViewState
    }
}

sealed interface HistoryEvent {
    data class OnTransactionDetail(val transferId: Long) : HistoryEvent
    data class PdfExportError(val message: String) : HistoryEvent
}

sealed interface HistoryAction {
    data class SetTransactionType(val filter: TransactionType) : HistoryAction
    data class ViewTransaction(val transferId: Long) : HistoryAction
    data object OnFilterClick : HistoryAction
    data class SetSelectedAccount(val account: Account) : HistoryAction
    data object OnApplyFilterClick : HistoryAction
    data object ClearFilters : HistoryAction

    data object ExportPdf : HistoryAction
}
