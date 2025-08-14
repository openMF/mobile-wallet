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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.send_money.generated.resources.Res
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_bank_transfer
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_choose_method
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_fineract_payments
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_merchants
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_more
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_pay_anyone
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_people
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_scan_qr_code
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_send
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
fun SendMoneyOptionsScreen(
    onBackClick: () -> Unit,
    onScanQrClick: () -> Unit,
    onPayAnyoneClick: () -> Unit,
    onBankTransferClick: () -> Unit,
    onFineractPaymentsClick: () -> Unit,
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SendMoneyOptionsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            SendMoneyOptionsEvent.NavigateBack -> onBackClick.invoke()
            SendMoneyOptionsEvent.NavigateToPayAnyone -> onPayAnyoneClick.invoke()
            SendMoneyOptionsEvent.NavigateToBankTransfer -> onBankTransferClick.invoke()
            SendMoneyOptionsEvent.NavigateToFineractPayments -> onFineractPaymentsClick.invoke()
            is SendMoneyOptionsEvent.QrCodeScanned -> onQrCodeScanned.invoke(event.data)
        }
    }
    MifosGradientBackground {
        MifosScaffold(
            modifier = modifier,
            topBar = {
                MifosTopBar(
                    topBarTitle = stringResource(Res.string.feature_send_money_send),
                    backPress = {
                        viewModel.trySendAction(SendMoneyOptionsAction.NavigateBack)
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
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
            ) {
                SendMoneyBanner()

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                SendMoneyOptionsRow(
                    onScanQrClick = {
                        viewModel.trySendAction(SendMoneyOptionsAction.ScanQrClicked)
                    },
                    onPayAnyoneClick = {
                        viewModel.trySendAction(SendMoneyOptionsAction.PayAnyoneClicked)
                    },
                    onBankTransferClick = {
                        viewModel.trySendAction(SendMoneyOptionsAction.BankTransferClicked)
                    },
                    onFineractPaymentsClick = {
                        viewModel.trySendAction(SendMoneyOptionsAction.FineractPaymentsClicked)
                    },
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                PeopleSection()

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                MerchantsSection()

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))
            }
        }
    }
}

@Composable
private fun SendMoneyBanner(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(KptTheme.spacing.md),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.xl),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.feature_send_money_choose_method),
                style = KptTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = KptTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun SendMoneyOptionsRow(
    onScanQrClick: () -> Unit,
    onPayAnyoneClick: () -> Unit,
    onBankTransferClick: () -> Unit,
    onFineractPaymentsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            SendMoneyOptionButton(
                icon = MifosIcons.Scan,
                label = stringResource(Res.string.feature_send_money_scan_qr_code),
                onClick = onScanQrClick,
                modifier = Modifier.weight(1f),
            )

            SendMoneyOptionButton(
                icon = MifosIcons.Person,
                label = stringResource(Res.string.feature_send_money_pay_anyone),
                onClick = onPayAnyoneClick,
                modifier = Modifier.weight(1f),
            )

            SendMoneyOptionButton(
                icon = MifosIcons.Bank,
                label = stringResource(Res.string.feature_send_money_bank_transfer),
                onClick = onBankTransferClick,
                modifier = Modifier.weight(1f),
            )

            SendMoneyOptionButton(
                icon = MifosIcons.Payment,
                label = stringResource(Res.string.feature_send_money_fineract_payments),
                onClick = onFineractPaymentsClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SendMoneyOptionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        color = KptTheme.colorScheme.surface,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KptTheme.spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = KptTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(KptTheme.spacing.sm),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp),
                    tint = KptTheme.colorScheme.onPrimaryContainer,
                )
            }

            Text(
                text = label,
                style = KptTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = KptTheme.colorScheme.onSurface,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun PeopleSection(
    modifier: Modifier = Modifier,
) {
    // TODO: This is a placeholder section. People functionality is not implemented yet.
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_send_money_people),
            style = KptTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = KptTheme.colorScheme.onSurface,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
            ) {
                PersonItem(
                    name = "John Doe",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Jane Smith",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Mike Johnson",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Sarah Wilson",
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
            ) {
                PersonItem(
                    name = "David Brown",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Lisa Davis",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Tom Miller",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = stringResource(Res.string.feature_send_money_more),
                    isMoreButton = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MerchantsSection(
    modifier: Modifier = Modifier,
) {
    // TODO: This is a placeholder section. Merchants functionality is not implemented yet.
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_send_money_merchants),
            style = KptTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = KptTheme.colorScheme.onSurface,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
            ) {
                PersonItem(
                    name = "Coffee Shop",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Grocery Store",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Restaurant",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Gas Station",
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
            ) {
                PersonItem(
                    name = "Pharmacy",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Bookstore",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = "Bakery",
                    modifier = Modifier.weight(1f),
                )
                PersonItem(
                    name = stringResource(Res.string.feature_send_money_more),
                    isMoreButton = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PersonItem(
    name: String,
    isMoreButton: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clickable { /* TODO: Handle click */ }
            .clip(RoundedCornerShape(KptTheme.spacing.sm)),
        color = KptTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KptTheme.spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isMoreButton) {
                            KptTheme.colorScheme.secondaryContainer
                        } else {
                            KptTheme.colorScheme.primaryContainer
                        },
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isMoreButton) {
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = name,
                        modifier = Modifier.size(24.dp),
                        tint = KptTheme.colorScheme.onSecondaryContainer,
                    )
                } else {
                    Text(
                        text = name.take(1).uppercase(),
                        style = KptTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = KptTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Text(
                text = name,
                style = KptTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = KptTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
