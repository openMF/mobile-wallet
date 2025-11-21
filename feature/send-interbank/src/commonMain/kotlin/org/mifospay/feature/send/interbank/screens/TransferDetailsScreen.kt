/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.interbank.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import mobile_wallet.feature.send_interbank.generated.resources.Res
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_transfer_details
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_from_account
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_to_account
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_amount
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_date
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_description
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_continue
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.savingsaccount.Currency
import org.mifospay.core.model.savingsaccount.Status
import org.mifospay.core.model.interbank.InterBankPartyInfoResponse
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TransferDetailsScreen(
    fromAccount: Account?,
    recipient: InterBankPartyInfoResponse?,
    amount: String,
    onAmountChanged: (String) -> Unit,
    date: String,
    onDateChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    availableAccounts: List<Account> = emptyList(),
    onFromAccountChange: (Account) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()
    MifosScaffold(
        modifier = modifier,
        topBar = {
            MifosTopBar(
                topBarTitle = stringResource(Res.string.feature_send_interbank_transfer_details),
                backPress = onBackClick,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                MifosButton(
                    onClick = onContinueClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = amount.isNotEmpty() && description.isNotEmpty(),
                ) {
                    Text(stringResource(Res.string.feature_send_interbank_continue))
                }
            }
        },
        containerColor = KptTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(KptTheme.spacing.md)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            /*item {
                Text(
                    text = stringResource(Res.string.feature_send_interbank_transfer_details),
                    style = KptTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }*/

            // From Account Selection
            item {
                if (availableAccounts.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Show account selection dialog */ },
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_send_interbank_from_account),
                            style = KptTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = KptTheme.colorScheme.onSurfaceVariant,
                        )
                        TransferInfoCard(
                            title = "",
                            name = fromAccount?.name ?: "Select Account",
                            accountNo = fromAccount?.number ?: "N/A",
                        )
                    }
                } else {
                    TransferInfoCard(
                        title = stringResource(Res.string.feature_send_interbank_from_account),
                        name = fromAccount?.name ?: "Unknown",
                        accountNo = fromAccount?.number ?: "N/A",
                    )
                }
            }

            // To Account Info
            item {
                TransferInfoCard(
                    title = stringResource(Res.string.feature_send_interbank_to_account),
                    name = "${recipient?.firstName ?: ""} ${recipient?.lastName ?: ""}".trim(),
                    accountNo = recipient?.partyId ?: "N/A",
                )
            }

            // Amount Input
            item {
                val amountError = if (amount.isNotEmpty()) {
                    val amountValue = amount.toDoubleOrNull()
                    when {
                        amountValue == null -> "Invalid amount"
                        amountValue <= 0 -> "Amount must be greater than 0"
                        fromAccount != null && amountValue > fromAccount.balance -> 
                            "Insufficient balance. Available: ${fromAccount.balance}"
                        else -> null
                    }
                } else {
                    null
                }
                
                MifosTextField(
                    label = stringResource(Res.string.feature_send_interbank_amount),
                    value = amount,
                    onValueChange = onAmountChanged,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = amountError != null,
                )
            }

            // Date Input with Picker
            item {
                MifosTextField(
                    label = stringResource(Res.string.feature_send_interbank_date),
                    value = date,
                    onValueChange = {},
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                )
            }

            // Description Input
            item {
                MifosTextField(
                    label = stringResource(Res.string.feature_send_interbank_description),
                    value = description,
                    onValueChange = onDescriptionChanged,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
            }

            item {
                Box(modifier = Modifier.padding(vertical = KptTheme.spacing.md))
            }
        }
    }

    // Create a DatePicker state
    val datePickerState = rememberDatePickerState()
    // Create a formatter
    val dateFormatter = remember { DatePickerDefaults.dateFormatter("dd MMMM yyyy") }


    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        /*dateState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                            val formatter = DateTimeFormatBuilder.b.ofPattern("dd MMMM yyyy")
                            onDateChanged(localDate.format(formatter))
                        }*/
                        showDatePicker = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
                dateFormatter = dateFormatter,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        scaleX = 1f,
                        scaleY = 1f,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
            )
           // DatePicker(state = dateState)
        }
    }
}

@Composable
private fun TransferInfoCard(
    title: String,
    name: String,
    accountNo: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
    ) {
        Text(
            text = title,
            style = KptTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = name,
            style = KptTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Account: $accountNo",
            style = KptTheme.typography.bodySmall,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
fun TransferDetailsScreenPreview() {
    MifosTheme {
        val mockFromAccount = Account(
            name = "John Doe",
            number = "9876-5432-1098-7654",
            balance = 1250.75,
            id = 101L,
            productId = 202L,
            currency = Currency(
                code = "USD",
                name = "US Dollar",
                displaySymbol = "$",
                displayLabel = "US Dollar ($)",
                decimalPlaces = 2,
                inMultiplesOf = 10,
                nameCode = "USD"
            ),
            status = Status(
                id = 300,
                code = "savingsAccountStatusType.active",
                value = "Active",
                submittedAndPendingApproval = false,
                approved = true,
                rejected = false,
                withdrawnByApplicant = false,
                active = true,
                closed = false,
                prematureClosed = false,
                transferInProgress = false,
                transferOnHold = false,
                matured = false
            ),
            image = "" // Optional: "https://example.com/path/to/image.png"
        )

        val mockRecipient = InterBankPartyInfoResponse(
            sourceFspId = "mifos-bank-1",
            destinationFspId = "blackbank-test",
            requestId = "req-001",
            partyId = "9880000020",
            currencyCode = "MXN",
            firstName = "Pedro",
            lastName = "Barreto",
            systemMessage = "Success",
            executionStatus = true,
            partyIdType = "MSISDN",
        )

        TransferDetailsScreen(
            fromAccount = mockFromAccount,
            recipient = mockRecipient,
            amount = "100.00",
            onAmountChanged = {},
            date = "11/09/25",
            onDateChanged = {},
            description = "Dinner share",
            onDescriptionChanged = {},
            onContinueClick = {},
            onBackClick = {},
        )
    }
}

@Preview
@Composable
fun TransferDetailsScreenEmptyPreview() {
    MifosTheme {
        val mockFromAccount = Account(
            name = "John Doe",
            number = "9876-5432-1098-7654",
            balance = 1250.75,
            id = 101L,
            productId = 202L,
            currency = Currency(
                code = "USD",
                name = "US Dollar",
                displaySymbol = "$",
                displayLabel = "US Dollar ($)",
                decimalPlaces = 2,
                inMultiplesOf = 10,
                nameCode = "USD"
            ),
            status = Status(
                id = 300,
                code = "savingsAccountStatusType.active",
                value = "Active",
                submittedAndPendingApproval = false,
                approved = true,
                rejected = false,
                withdrawnByApplicant = false,
                active = true,
                closed = false,
                prematureClosed = false,
                transferInProgress = false,
                transferOnHold = false,
                matured = false
            ),
            image = "" // Optional: "https://example.com/path/to/image.png"
        )

        val mockRecipient = InterBankPartyInfoResponse(
            sourceFspId = "mifos-bank-1",
            destinationFspId = "blackbank-test",
            requestId = "req-001",
            partyId = "9880000020",
            currencyCode = "MXN",
            firstName = "Pedro",
            lastName = "Barreto",
            systemMessage = "Success",
            executionStatus = true,
            partyIdType = "MSISDN",
        )

        TransferDetailsScreen(
            fromAccount = mockFromAccount,
            recipient = mockRecipient,
            amount = "",
            onAmountChanged = {},
            date = "",
            onDateChanged = {},
            description = "",
            onDescriptionChanged = {},
            onContinueClick = {},
            onBackClick = {},
        )
    }
}
