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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import mobile_wallet.feature.send_interbank.generated.resources.Res
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_transfer_successful
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_transfer_completed
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_amount_label
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_download_receipt
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_back_to_home
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_transfer_failed
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_transaction_failed
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_error
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_retry_transfer
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_contact_support
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_success
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_failed
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import template.core.base.designsystem.theme.KptTheme

@Composable
fun TransferSuccessScreen(
    recipientName: String,
    amount: String,
    onDownloadReceipt: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                MifosButton(
                    onClick = onDownloadReceipt,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.feature_send_interbank_download_receipt))
                }

                MifosButton(
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.feature_send_interbank_back_to_home))
                }
            }
        },
        containerColor = KptTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    KptTheme.spacing.md,
                    Alignment.CenterVertically,
                ),
            ) {
                Icon(
                    imageVector = MifosIcons.Check,
                    contentDescription = stringResource(Res.string.feature_send_interbank_success),
                    modifier = Modifier.size(80.dp),
                    tint = KptTheme.colorScheme.primary,
                )

                Text(
                    text = stringResource(Res.string.feature_send_interbank_transfer_successful),
                    style = KptTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = stringResource(Res.string.feature_send_interbank_transfer_completed, recipientName),
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = stringResource(Res.string.feature_send_interbank_amount_label, amount),
                    style = KptTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = KptTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun TransferFailedScreen(
    errorMessage: String,
    onRetry: () -> Unit,
    onContactSupport: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                MifosButton(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.feature_send_interbank_retry_transfer))
                }

                MifosButton(
                    onClick = onContactSupport,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.feature_send_interbank_contact_support))
                }

                MifosButton(
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Res.string.feature_send_interbank_back_to_home))
                }
            }
        },
        containerColor = KptTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    KptTheme.spacing.md,
                    Alignment.CenterVertically,
                ),
            ) {
                Icon(
                    imageVector = MifosIcons.Error,
                    contentDescription = stringResource(Res.string.feature_send_interbank_failed),
                    modifier = Modifier.size(80.dp),
                    tint = KptTheme.colorScheme.error,
                )

                Text(
                    text = stringResource(Res.string.feature_send_interbank_transfer_failed),
                    style = KptTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = KptTheme.colorScheme.error,
                )

                Text(
                    text = stringResource(Res.string.feature_send_interbank_transaction_failed),
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = stringResource(Res.string.feature_send_interbank_error, errorMessage),
                    style = KptTheme.typography.bodySmall,
                    color = KptTheme.colorScheme.error,
                )
            }
        }
    }
}

@Preview
@Composable
fun TransferSuccessScreenPreview() {
    MifosTheme {
        TransferSuccessScreen(
            recipientName = "Pedro Barreto",
            amount = "100.00",
            onDownloadReceipt = {},
            onBackToHome = {},
        )
    }
}

@Preview
@Composable
fun TransferFailedScreenPreview() {
    MifosTheme {
        TransferFailedScreen(
            errorMessage = "T-402: Transaction failed. Please try again.",
            onRetry = {},
            onContactSupport = {},
            onBackToHome = {},
        )
    }
}
