/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.interbank

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.send.interbank.screens.PreviewTransferScreen
import org.mifospay.feature.send.interbank.screens.SearchRecipientScreen
import org.mifospay.feature.send.interbank.screens.SelectAccountScreen
import org.mifospay.feature.send.interbank.screens.TransferDetailsScreen
import org.mifospay.feature.send.interbank.screens.TransferFailedScreen
import org.mifospay.feature.send.interbank.screens.TransferSuccessScreen

/**
 * Main orchestrator screen for the interbank transfer flow
 * Manages navigation between all steps of the transfer process
 */
@Composable
fun InterbankTransferFlowScreen(
    onBackClick: () -> Unit,
    onTransferSuccess: () -> Unit,
    onContactSupport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InterbankTransferViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<RecipientInfo>>(emptyList()) }

    EventsEffect(viewModel) { event ->
        when (event) {
            InterbankTransferEvent.OnNavigateBack -> onBackClick()
            InterbankTransferEvent.OnTransferSuccess -> onTransferSuccess()
            is InterbankTransferEvent.OnTransferFailed -> {
                // Error is handled in the state
            }
        }
    }

    when (state.currentStep) {
        InterbankTransferState.Step.SelectAccount -> {
            SelectAccountScreen(
                accounts = state.fromAccounts,
                isLoading = state.loadingState is InterbankTransferState.LoadingState.Loading,
                error = (state.loadingState as? InterbankTransferState.LoadingState.Error)?.message,
                onAccountSelected = { account ->
                    viewModel.trySendAction(
                        InterbankTransferAction.NavigateToRecipientSearch(account),
                    )
                },
                onBackClick = {
                    viewModel.trySendAction(InterbankTransferAction.NavigateBack)
                },
                modifier = modifier,
            )
        }

        InterbankTransferState.Step.SearchRecipient -> {
            SearchRecipientScreen(
                searchQuery = searchQuery,
                onSearchQueryChanged = { query ->
                    searchQuery = query
                    // TODO: Implement actual recipient search from API
                    // For now, mock search results
                    searchResults = if (query.isNotEmpty()) {
                        listOf(
                            RecipientInfo(
                                clientId = 1L,
                                officeId = 1,
                                accountId = 1,
                                accountType = 2,
                                clientName = "Pedro Barreto",
                                accountNo = "9880000020",
                            ),
                        )
                    } else {
                        emptyList()
                    }
                },
                recipients = searchResults,
                onRecipientSelected = { recipient ->
                    viewModel.trySendAction(
                        InterbankTransferAction.NavigateToTransferDetails(recipient),
                    )
                },
                onBackClick = {
                    viewModel.trySendAction(InterbankTransferAction.NavigateBack)
                },
                modifier = modifier,
            )
        }

        InterbankTransferState.Step.TransferDetails -> {
            TransferDetailsScreen(
                fromAccount = state.selectedFromAccount,
                recipient = state.selectedRecipient,
                amount = state.transferAmount,
                onAmountChanged = { amount ->
                    viewModel.trySendAction(InterbankTransferAction.UpdateAmount(amount))
                },
                date = state.transferDate,
                onDateChanged = { date ->
                    viewModel.trySendAction(InterbankTransferAction.UpdateDate(date))
                },
                description = state.transferDescription,
                onDescriptionChanged = { desc ->
                    viewModel.trySendAction(InterbankTransferAction.UpdateDescription(desc))
                },
                onContinueClick = {
                    viewModel.trySendAction(InterbankTransferAction.NavigateToPreview)
                },
                onBackClick = {
                    viewModel.trySendAction(InterbankTransferAction.NavigateBack)
                },
                modifier = modifier,
            )
        }

        InterbankTransferState.Step.PreviewTransfer -> {
            PreviewTransferScreen(
                transferPayload = state.transferPayload,
                fromAccountName = state.selectedFromAccount?.name ?: "Unknown",
                fromAccountNo = state.selectedFromAccount?.number ?: "N/A",
                recipientInfo = state.selectedRecipient,
                isProcessing = state.isProcessing,
                onEditClick = {
                    viewModel.trySendAction(InterbankTransferAction.NavigateBack)
                },
                onConfirmClick = {
                    viewModel.trySendAction(InterbankTransferAction.ConfirmTransfer)
                },
                onBackClick = {
                    viewModel.trySendAction(InterbankTransferAction.NavigateBack)
                },
                modifier = modifier,
            )
        }

        InterbankTransferState.Step.TransferSuccess -> {
            TransferSuccessScreen(
                recipientName = state.selectedRecipient?.clientName ?: "Recipient",
                amount = state.transferAmount,
                onDownloadReceipt = {
                    // TODO: Implement receipt download
                },
                onBackToHome = onTransferSuccess,
                modifier = modifier,
            )
        }

        InterbankTransferState.Step.TransferFailed -> {
            TransferFailedScreen(
                errorMessage = state.errorMessage ?: "Unknown error occurred",
                onRetry = {
                    viewModel.trySendAction(InterbankTransferAction.RetryTransfer)
                },
                onContactSupport = onContactSupport,
                onBackToHome = onBackClick,
                modifier = modifier,
            )
        }
    }
}
