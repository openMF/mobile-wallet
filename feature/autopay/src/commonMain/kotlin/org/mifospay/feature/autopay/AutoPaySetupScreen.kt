/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.autopay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Clock
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.DateHelper
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.autopay.FrequencyOption
import org.mifospay.core.model.autopay.PaymentMethod
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.DropdownBox
import org.mifospay.core.ui.DropdownBoxItem
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoPaySetupScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AutoPaySetupViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    EventsEffect(viewModel) { event ->
        when (event) {
            is AutoPaySetupEvent.AutoPayActivated -> {
                showSuccessDialog = true
            }

            is AutoPaySetupEvent.NavigateToSuccess -> {
                onNavigateToSuccess()
            }

            is AutoPaySetupEvent.ShowError -> {
                // Handle error display
            }
        }
    }

    LaunchedEffect(showSuccessDialog) {
        if (showSuccessDialog) {
            kotlinx.coroutines.delay(2000)
            onNavigateToSuccess()
        }
    }

    MifosScaffold(
        modifier = modifier,
        topBar = {
            MifosTopBar(
                topBarTitle = "Setup AutoPay",
                backPress = onNavigateBack,
            )
        },
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                MifosLoadingWheel(
                    contentDesc = "Loading AutoPay setup",
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SetupProgressIndicator(currentStep = state.currentStep)

                when (state.currentStep) {
                    SetupStep.PAYMENT_METHOD -> {
                        PaymentMethodSelection(
                            selectedMethod = state.setupData.paymentMethod,
                            paymentMethods = state.paymentMethods,
                            onMethodSelected = { method ->
                                viewModel.trySendAction(
                                    AutoPaySetupAction.UpdatePaymentMethod(
                                        method,
                                    ),
                                )
                            },
                            onNext = {
                                viewModel.trySendAction(AutoPaySetupAction.NextStep)
                            },
                        )
                    }

                    SetupStep.SCHEDULE_CONFIG -> {
                        ScheduleConfiguration(
                            setupData = state.setupData,
                            frequencyOptions = state.frequencyOptions,
                            onFrequencyChanged = { frequency ->
                                viewModel.trySendAction(AutoPaySetupAction.UpdateFrequency(frequency))
                            },
                            onStartDateClick = { showStartDatePicker = true },
                            onEndDateClick = { showEndDatePicker = true },
                            onNext = {
                                viewModel.trySendAction(AutoPaySetupAction.NextStep)
                            },
                            onBack = {
                                viewModel.trySendAction(AutoPaySetupAction.PreviousStep)
                            },
                        )
                    }

                    SetupStep.AMOUNT_CONFIRMATION -> {
                        AmountConfirmation(
                            setupData = state.setupData,
                            onAmountChanged = { amount ->
                                viewModel.trySendAction(AutoPaySetupAction.UpdateAmount(amount))
                            },
                            onNext = {
                                viewModel.trySendAction(AutoPaySetupAction.NextStep)
                            },
                            onBack = {
                                viewModel.trySendAction(AutoPaySetupAction.PreviousStep)
                            },
                        )
                    }

                    SetupStep.TERMS_ACCEPTANCE -> {
                        TermsAcceptance(
                            isAccepted = state.setupData.termsAccepted,
                            onAcceptedChanged = { accepted ->
                                viewModel.trySendAction(
                                    AutoPaySetupAction.UpdateTermsAccepted(
                                        accepted,
                                    ),
                                )
                            },
                            onActivate = {
                                viewModel.trySendAction(AutoPaySetupAction.ActivateAutoPay)
                            },
                            onBack = {
                                viewModel.trySendAction(AutoPaySetupAction.PreviousStep)
                            },
                        )
                    }
                }
            }
        }

        if (showStartDatePicker) {
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = state.setupData.startDate ?: Clock.System.now()
                    .toEpochMilliseconds(),
            )

            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dateState.selectedDateMillis?.let { timestamp ->
                                viewModel.trySendAction(AutoPaySetupAction.UpdateStartDate(timestamp))
                            }
                            showStartDatePicker = false
                        },
                        enabled = dateState.selectedDateMillis != null,
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) {
                        Text("Cancel")
                    }
                },
                content = {
                    DatePicker(state = dateState)
                },
            )
        }

        if (showEndDatePicker) {
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = state.setupData.endDate ?: Clock.System.now()
                    .toEpochMilliseconds(),
            )

            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dateState.selectedDateMillis?.let { timestamp ->
                                viewModel.trySendAction(AutoPaySetupAction.UpdateEndDate(timestamp))
                            }
                            showEndDatePicker = false
                        },
                        enabled = dateState.selectedDateMillis != null,
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) {
                        Text("Cancel")
                    }
                },
                content = {
                    DatePicker(state = dateState)
                },
            )
        }

        if (showSuccessDialog) {
            AnimatedVisibility(
                visible = showSuccessDialog,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { it / 2 },
                ),
                exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { it / 2 },
                ),
            ) {
                SuccessDialog()
            }
        }

        if (state.isActivating) {
            MifosLoadingDialog(
                visibilityState = LoadingDialogState.Shown,
            )
        }
    }
}

@Composable
private fun SetupProgressIndicator(
    currentStep: SetupStep,
    modifier: Modifier = Modifier,
) {
    val steps = listOf(
        "Payment Method" to MifosIcons.CreditCard,
        "Schedule" to MifosIcons.CalenderMonth,
        "Amount" to MifosIcons.AttachMoney,
        "Terms" to MifosIcons.CheckCircle,
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        steps.forEachIndexed { index, (title, icon) ->
            val isActive = index <= currentStep.ordinal
            val isCompleted = index < currentStep.ordinal

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AvatarBox(
                    icon = icon,
                    backgroundColor = if (isActive) {
                        KptTheme.colorScheme.primary
                    } else {
                        KptTheme.colorScheme.outlineVariant
                    },
                    modifier = Modifier.size(40.dp),
                )

                Text(
                    text = title,
                    style = KptTheme.typography.labelSmall,
                    color = if (isActive) {
                        KptTheme.colorScheme.primary
                    } else {
                        KptTheme.colorScheme.onSurfaceVariant
                    },
                )
            }

            if (index < steps.lastIndex) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PaymentMethodSelection(
    selectedMethod: PaymentMethod?,
    paymentMethods: List<PaymentMethod>,
    onMethodSelected: (PaymentMethod) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Select Payment Method",
            style = KptTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )

        Text(
            text = "Choose how you want to make automatic payments",
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            paymentMethods.forEach { method ->
                PaymentMethodCard(
                    method = method,
                    isSelected = selectedMethod?.id == method.id,
                    onSelect = { onMethodSelected(method) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosButton(
            onClick = onNext,
            enabled = selectedMethod != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun PaymentMethodCard(
    method: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) {
                KptTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            },
        ),
        onClick = onSelect,
    ) {
        ListItem(
            headlineContent = {
                Text(text = method.value)
            },
            supportingContent = {
                Text(text = method.description ?: "")
            },
            leadingContent = {
                AvatarBox(
                    icon = when (method.code) {
                        "BANK_ACCOUNT" -> MifosIcons.Bank
                        "CARD" -> MifosIcons.CreditCard
                        "UPI" -> MifosIcons.Payment
                        else -> MifosIcons.Payment
                    },
                    backgroundColor = KptTheme.colorScheme.surfaceContainerHigh,
                )
            },
            trailingContent = {
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun ScheduleConfiguration(
    setupData: AutoPaySetupData,
    frequencyOptions: List<FrequencyOption>,
    onFrequencyChanged: (FrequencyOption) -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Configure Schedule",
            style = KptTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )

        Text(
            text = "Set up when and how often payments should be made",
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        var showFrequencyDropdown by remember { mutableStateOf(false) }

        DropdownBox(
            expanded = showFrequencyDropdown,
            label = "Frequency *",
            value = setupData.frequency?.value ?: "",
            readOnly = true,
            onExpandChange = { showFrequencyDropdown = it },
        ) {
            frequencyOptions.forEach { frequency ->
                DropdownBoxItem(
                    text = frequency.value,
                    onClick = {
                        onFrequencyChanged(frequency)
                        showFrequencyDropdown = false
                    },
                )
            }
        }

        MifosTextField(
            label = "Start Date *",
            value = setupData.startDate?.let { DateHelper.getDateAsStringFromLong(it) } ?: "",
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = onStartDateClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = KptTheme.colorScheme.tertiary,
                        contentColor = KptTheme.colorScheme.tertiaryContainer,
                    ),
                ) {
                    Icon(
                        imageVector = MifosIcons.CalenderMonth,
                        contentDescription = "Choose Start Date",
                    )
                }
            },
        )

        MifosTextField(
            label = "End Date",
            value = setupData.endDate?.let { DateHelper.getDateAsStringFromLong(it) } ?: "",
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = onEndDateClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = KptTheme.colorScheme.tertiary,
                        contentColor = KptTheme.colorScheme.tertiaryContainer,
                    ),
                ) {
                    Icon(
                        imageVector = MifosIcons.CalenderMonth,
                        contentDescription = "Choose End Date",
                    )
                }
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MifosButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
            ) {
                Text("Back")
            }

            MifosButton(
                onClick = onNext,
                enabled = isScheduleValid(setupData),
                modifier = Modifier.weight(1f),
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
private fun AmountConfirmation(
    setupData: AutoPaySetupData,
    onAmountChanged: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Payment Amount",
            style = KptTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )

        Text(
            text = "Confirm the amount for each payment",
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = KptTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Payment Method:")
                    Text(
                        text = setupData.paymentMethod?.value ?: "",
                        fontWeight = FontWeight.Medium,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Frequency:")
                    Text(
                        text = setupData.frequency?.value ?: "",
                        fontWeight = FontWeight.Medium,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Start Date:")
                    Text(
                        text = setupData.startDate?.let { DateHelper.getDateAsStringFromLong(it) }
                            ?: "",
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        MifosTextField(
            label = "Amount *",
            value = setupData.amount,
            onValueChange = onAmountChanged,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MifosButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
            ) {
                Text("Back")
            }

            MifosButton(
                onClick = onNext,
                enabled = setupData.amount.isNotEmpty() && setupData.amount.toDoubleOrNull() != null,
                modifier = Modifier.weight(1f),
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
private fun TermsAcceptance(
    isAccepted: Boolean,
    onAcceptedChanged: (Boolean) -> Unit,
    onActivate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Terms & Conditions",
            style = KptTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )

        Text(
            text = "Please review and accept the terms before activating AutoPay",
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = KptTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "AutoPay Terms & Conditions",
                    style = KptTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )

                Text(
                    text = "By activating AutoPay, you agree to:",
                    style = KptTheme.typography.bodyMedium,
                )

                Text("• Authorize automatic payments according to your schedule")
                Text("• Ensure sufficient funds are available for payments")
                Text("• Notify us of any changes to your payment method")
                Text("• Understand that failed payments may incur fees")
                Text("• Allow us to process payments on scheduled dates")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(
                checked = isAccepted,
                onCheckedChange = onAcceptedChanged,
            )

            Text(
                text = "I accept the terms and conditions",
                style = KptTheme.typography.bodyMedium,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MifosButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
            ) {
                Text("Back")
            }

            MifosButton(
                onClick = onActivate,
                enabled = isAccepted,
                modifier = Modifier.weight(1f),
            ) {
                Text("Activate AutoPay")
            }
        }
    }
}

@Composable
private fun SuccessDialog() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AvatarBox(
                icon = MifosIcons.CheckCircle,
                backgroundColor = KptTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
            )

            Text(
                text = "AutoPay Activated!",
                style = KptTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = "Your automatic payment schedule has been successfully created and activated.",
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun isScheduleValid(setupData: AutoPaySetupData): Boolean {
    return setupData.frequency != null && setupData.startDate != null
}
