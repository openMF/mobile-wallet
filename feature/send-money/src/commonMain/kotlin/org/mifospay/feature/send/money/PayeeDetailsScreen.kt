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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
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

                Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

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
                if (state.payeeName.isNotEmpty() && state.payeeName != "UNKNOWN") {
                    val firstLetter = state.payeeName
                        .replace("%20", " ")
                        .trim()
                        .firstOrNull()
                        ?.uppercase()

                    if (firstLetter != null) {
                        Text(
                            text = firstLetter,
                            style = KptTheme.typography.headlineLarge.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = KptTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        Icon(
                            imageVector = MifosIcons.Person,
                            contentDescription = "Payee Profile",
                            modifier = Modifier.size(40.dp),
                            tint = KptTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                } else {
                    Icon(
                        imageVector = MifosIcons.Person,
                        contentDescription = "Payee Profile",
                        modifier = Modifier.size(40.dp),
                        tint = KptTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            if (state.payeeName.isNotEmpty() && state.payeeName != "UNKNOWN") {
                val decodedName = state.payeeName
                    .replace("%20", " ")
                    .trim()

                Text(
                    text = "Paying ${decodedName.uppercase()}",
                    style = KptTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = KptTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }

            val contactInfo = if (state.isUpiCode) {
                "UPI ID: ${state.upiId}"
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
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.lg),
    ) {
        ExpandableAmountInput(
            value = state.formattedAmount,
            onValueChange = onAmountChange,
            enabled = state.isAmountEditable,
            modifier = Modifier.wrapContentWidth(),
        )

        AnimatedVisibility(
            visible = state.showMaxAmountMessage,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
        ) {
            val vibrationOffset by animateFloatAsState(
                targetValue = if (state.showMaxAmountMessage) 1f else 0f,
                animationSpec = repeatable(
                    iterations = 3,
                    animation = tween(100, delayMillis = 0),
                ),
                label = "vibration",
            )

            Text(
                text = "Amount cannot be more than ₹ 5,00,000",
                style = KptTheme.typography.bodySmall,
                color = KptTheme.colorScheme.error,
                modifier = Modifier
                    .padding(top = KptTheme.spacing.xs)
                    .graphicsLayer {
                        translationX = if (state.showMaxAmountMessage) {
                            (vibrationOffset * 10f * (if (vibrationOffset % 2 == 0f) 1f else -1f))
                        } else {
                            0f
                        }
                    },
            )
        }

        ExpandableNoteInput(
            value = state.note,
            onValueChange = onNoteChange,
            modifier = Modifier.wrapContentWidth(),
        )
    }
}

@Composable
private fun ExpandableAmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val displayValue = value.ifEmpty { "0" }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(KptTheme.spacing.sm))
                .background(
                    color = KptTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(KptTheme.spacing.sm),
                )
                .padding(
                    horizontal = KptTheme.spacing.md,
                    vertical = KptTheme.spacing.sm,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "₹",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = KptTheme.colorScheme.onSurface,
                ),
            )

            Spacer(modifier = Modifier.width(KptTheme.spacing.sm))

            BasicTextField(
                value = displayValue,
                onValueChange = { newValue ->
                    val cleanValue = newValue.replace(",", "").replace(".", "")
                    if (cleanValue.isEmpty() || cleanValue.toLongOrNull() != null) {
                        val amount = cleanValue.toLongOrNull() ?: 0L
                        if (amount <= 500000) {
                            onValueChange(cleanValue)
                        }
                    }
                },
                enabled = enabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = KptTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .width(
                        when {
                            displayValue.length <= 1 -> 24.dp
                            displayValue.length <= 3 -> displayValue.length * 16.dp
                            displayValue.length <= 6 -> displayValue.length * 14.dp
                            else -> displayValue.length * 12.dp
                        },
                    )
                    .focusRequester(focusRequester),
                singleLine = true,
            )
        }
    }
}

@Composable
private fun ExpandableNoteInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(KptTheme.spacing.sm))
                .background(
                    color = KptTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(KptTheme.spacing.sm),
                )
                .padding(
                    horizontal = KptTheme.spacing.md,
                    vertical = KptTheme.spacing.sm,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= 50) {
                        onValueChange(newValue)
                    }
                },
                enabled = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (value.isEmpty()) KptTheme.colorScheme.onSurfaceVariant else KptTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .width(
                        when {
                            value.length <= 7 -> 7 * 12.dp
                            value.length <= 28 -> (value.length + 1) * 12.dp
                            else -> 28 * 12.dp
                        },
                    )
                    .focusRequester(focusRequester),
                singleLine = value.length <= 28,
                maxLines = if (value.length > 28) 2 else 1,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = "Add note",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = KptTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }
                    innerTextField()
                },
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
    val isAmountValid = state.amount.isNotEmpty() &&
        state.amount.toLongOrNull() != null &&
        state.amount.toLong() > 0 &&
        !state.isAmountExceedingMax
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
