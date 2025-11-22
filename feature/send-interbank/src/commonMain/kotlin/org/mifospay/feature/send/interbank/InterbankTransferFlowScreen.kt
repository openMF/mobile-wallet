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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.CurrencyFormatter
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

    EventsEffect(viewModel) { event ->
        when (event) {
            InterbankTransferEvent.OnNavigateBack -> onBackClick()
            else -> {
                // Other steps don't require navigation
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
            val searchState = state.searchRecipientState
            val isSearching = searchState is InterbankTransferState.SearchRecipientState.Loading
            val searchError = (searchState as? InterbankTransferState.SearchRecipientState.Error)?.message

            SearchRecipientScreen(
                searchQuery = searchQuery,
                onSearchQueryChanged = { query ->
                    searchQuery = query
                },
                recipients = state.searchResults,
                onRecipientSelected = { participantInfo ->
                    viewModel.trySendAction(
                        InterbankTransferAction.NavigateToTransferDetails(participantInfo),
                    )
                },
                onSearchClick = { phoneNumber ->
                    viewModel.trySendAction(
                        InterbankTransferAction.SearchRecipient(phoneNumber),
                    )
                },
                isSearching = isSearching,
                searchError = searchError,
                onBackClick = {
                    viewModel.trySendAction(InterbankTransferAction.NavigateBack)
                },
                modifier = modifier,
            )
        }

        InterbankTransferState.Step.TransferDetails -> {
            TransferDetailsScreen(
                fromAccount = state.selectedFromAccount,
                recipient = state.selectedParticipantInfo,
                amount = state.transferAmount,
                onAmountChanged = { amount ->
                    viewModel.trySendAction(InterbankTransferAction.UpdateAmount(amount))
                },
                date = state.transferDate,
                initialDate = state.initialDate,
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
                onEditFromAccount = {
                    viewModel.trySendAction(InterbankTransferAction.EditFromAccount)
                },
                onEditRecipient = {
                    viewModel.trySendAction(InterbankTransferAction.EditRecipient)
                },
                modifier = modifier,
            )
        }

        InterbankTransferState.Step.PreviewTransfer -> {
            PreviewTransferScreen(
                amount = state.transferAmount,
                transferDate = state.transferDate,
                transferDescription = state.transferDescription,
                fromAccountName = state.selectedFromAccount?.name ?: "Unknown",
                fromAccountNo = state.selectedFromAccount?.number ?: "N/A",
                fromAccountBalance = state.selectedFromAccount?.balance ?: 0.0,
                fromAccountType = "${state.selectedFromAccount?.accountType?.value ?: ""} | ${state.selectedFromAccount?.currency?.name ?: ""}".trim(),
                recipientInfo = state.selectedParticipantInfo,
                isProcessing = state.isProcessing,
                currencyCode = state.selectedFromAccount?.currency?.code ?: "MXN",
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
                recipientName = "${state.selectedParticipantInfo?.firstName ?: ""} ${state.selectedParticipantInfo?.lastName ?: ""}".trim().ifEmpty { "Recipient" },
                amount = state.transferAmount,
                transactionReference = state.transferResponse ?: "N/A",
                fromAccount = state.selectedFromAccount?.number ?: "N/A",
                fromAccountName = state.selectedFromAccount?.name ?: "Unknown",
                toAccount = "${state.selectedParticipantInfo?.firstName ?: ""} ${state.selectedParticipantInfo?.lastName ?: ""}".trim(),
                toAccountNumber = "Account: ${state.selectedParticipantInfo?.partyId ?: "N/A"}",
                transactionDate = state.transferDate,
                description = state.transferDescription,
                currencyCode = state.selectedFromAccount?.currency?.code ?: "MXN",
                onBackToHome = onTransferSuccess,
                modifier = modifier,
            )
        }

        InterbankTransferState.Step.TransferFailed -> {
            TransferFailedScreen(
                errorMessage = state.errorMessage ?: "Unknown error occurred",
                errorTitle = "Transfer Failed",
                attemptedAmount = CurrencyFormatter.format(
                    state.transferAmount.toDoubleOrNull() ?: 0.0,
                    state.selectedFromAccount?.currency?.code ?: "MXN",
                    null,
                ),
                availableBalance = CurrencyFormatter.format(
                    state.selectedFromAccount?.balance ?: 0.0,
                    state.selectedFromAccount?.currency?.code ?: "MXN",
                    null,
                ),
                fromAccount = state.selectedFromAccount?.number ?: "N/A",
                fromAccountName = state.selectedFromAccount?.name ?: "Unknown",
                toAccount = "${state.selectedParticipantInfo?.firstName ?: ""} ${state.selectedParticipantInfo?.lastName ?: ""}".trim(),
                toAccountNumber = "Account: ${state.selectedParticipantInfo?.partyId ?: "N/A"}",
                transactionDate = state.transferDate,
                description = state.transferDescription,
                onRetry = {
                    viewModel.trySendAction(InterbankTransferAction.RetryTransfer)
                },
                onBackToHome = onBackClick,
                modifier = modifier,
            )
        }
    }
}
