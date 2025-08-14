/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
fun PayeeDetailsScreen(
    onBackClick: () -> Unit,
    onNavigateToUpiPayment: (PayeeDetailsState) -> Unit,
    onNavigateToFineractPayment: (PayeeDetailsState) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PayeeDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            PayeeDetailsEvent.NavigateBack -> onBackClick.invoke()
            is PayeeDetailsEvent.NavigateToUpiPayment -> onNavigateToUpiPayment.invoke(event.state)
            is PayeeDetailsEvent.NavigateToFineractPayment -> onNavigateToFineractPayment.invoke(event.state)
        }
    }

    MifosGradientBackground {
        MifosScaffold(
            modifier = modifier,
            topBar = {
                MifosTopBar(
                    topBarTitle = "Payee Details",
                    backPress = {
                        viewModel.trySendAction(PayeeDetailsAction.NavigateBack)
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(horizontal = KptTheme.spacing.lg)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.lg),
            ) {
                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                PayeeProfileSection(state)

                Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

                PaymentDetailsSection(
                    state = state,
                    onAmountChange = { amount ->
                        viewModel.trySendAction(PayeeDetailsAction.UpdateAmount(amount))
                    },
                    onNoteChange = { note ->
                        viewModel.trySendAction(PayeeDetailsAction.UpdateNote(note))
                    },
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

                ProceedButton(
                    state = state,
                    onProceedClick = {
                        viewModel.trySendAction(PayeeDetailsAction.ProceedToPayment)
                    },
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.lg))
            }
        }
    }
}

@Composable
private fun PayeeProfileSection(
    state: PayeeDetailsState,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(KptTheme.spacing.md),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = KptTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = MifosIcons.Person,
                    contentDescription = "Payee Profile",
                    modifier = Modifier.size(40.dp),
                    tint = KptTheme.colorScheme.onPrimaryContainer,
                )
            }

            if (state.payeeName.isNotEmpty()) {
                Text(
                    text = state.payeeName,
                    style = KptTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = KptTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }

            val contactInfo = if (state.isUpiCode) {
                state.upiId
            } else {
                state.phoneNumber
            }

            if (contactInfo.isNotEmpty()) {
                Text(
                    text = contactInfo,
                    style = KptTheme.typography.bodyLarge,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentDetailsSection(
    state: PayeeDetailsState,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(KptTheme.spacing.md),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.lg),
        ) {
            Text(
                text = "Payment Details",
                style = KptTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = KptTheme.colorScheme.onSurface,
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                enabled = state.isAmountEditable,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = MifosIcons.Currency,
                        contentDescription = "Amount",
                        tint = KptTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )

            OutlinedTextField(
                value = state.note,
                onValueChange = { newValue ->
                    if (newValue.length <= 50) {
                        onNoteChange(newValue)
                    }
                },
                placeholder = { Text("Add note") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                singleLine = false,
            )
        }
    }
}

@Composable
private fun ProceedButton(
    state: PayeeDetailsState,
    onProceedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAmountValid = state.amount.isNotEmpty() && state.amount.toDoubleOrNull() != null
    val isContactValid = state.upiId.isNotEmpty() || state.phoneNumber.isNotEmpty()

    Button(
        onClick = onProceedClick,
        enabled = isAmountValid && isContactValid,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = KptTheme.colorScheme.primary,
            contentColor = KptTheme.colorScheme.onPrimary,
        ),
        shape = RoundedCornerShape(KptTheme.spacing.sm),
    ) {
        Text(
            text = if (state.isUpiCode) "Proceed to UPI Payment" else "Proceed to Payment",
            style = KptTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = KptTheme.spacing.sm),
        )
    }
}
