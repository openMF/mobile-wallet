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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import mobile_wallet.feature.send_interbank.generated.resources.Res
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_amount
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_preview_transfer
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_review_transfer
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_confirm_pay
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_date
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_description
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_edit
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_from_account
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_to_account
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.model.interbank.InterBankPartyInfoResponse
import org.mifospay.core.ui.AvatarBox
import template.core.base.designsystem.theme.KptTheme

@Composable
fun PreviewTransferScreen(
    amount: String,
    transferDate: String,
    transferDescription: String,
    fromAccountName: String,
    fromAccountNo: String,
    recipientInfo: InterBankPartyInfoResponse?,
    isProcessing: Boolean,
    onEditClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBar = {
            MifosTopBar(
                topBarTitle = stringResource(Res.string.feature_send_interbank_preview_transfer),
                backPress = onBackClick,
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                MifosButton(
                    onClick = onConfirmClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing,
                ) {
                    Text(stringResource(Res.string.feature_send_interbank_confirm_pay))
                }

                MifosButton(
                    onClick = onEditClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing,
                ) {
                    Text(stringResource(Res.string.feature_send_interbank_edit))
                }
            }
        },
        containerColor = KptTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(KptTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.feature_send_interbank_review_transfer),
                    style = KptTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // From Account
            item {
                PreviewCard(
                    title = stringResource(Res.string.feature_send_interbank_from_account),
                    name = fromAccountName,
                    accountNo = fromAccountNo,
                    icon = MifosIcons.Bank,
                )
            }

            // To Account
            item {
                PreviewCard(
                    title = stringResource(Res.string.feature_send_interbank_to_account),
                    name = "${recipientInfo?.firstName ?: ""} ${recipientInfo?.lastName ?: ""}".trim(),
                    accountNo = recipientInfo?.partyId ?: "N/A",
                    icon = MifosIcons.Person,
                )
            }

            // Amount
            item {
                PreviewDetailRow(
                    label = stringResource(Res.string.feature_send_interbank_amount),
                    value = "$$amount",
                    isHighlight = true,
                )
            }

            // Date
            item {
                PreviewDetailRow(
                    label = stringResource(Res.string.feature_send_interbank_date),
                    value = transferDate.ifEmpty { "N/A" },
                )
            }

            // Description
            item {
                PreviewDetailRow(
                    label = stringResource(Res.string.feature_send_interbank_description),
                    value = transferDescription.ifEmpty { "N/A" },
                )
            }

            item {
                Box(modifier = Modifier.padding(vertical = KptTheme.spacing.md))
            }
        }
    }
}

@Composable
private fun PreviewCard(
    title: String,
    name: String,
    accountNo: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(KptTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            Text(
                text = title,
                style = KptTheme.typography.labelMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AvatarBox(
                    icon = icon,
                    backgroundColor = KptTheme.colorScheme.primaryContainer,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = name,
                        style = KptTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = accountNo,
                        style = KptTheme.typography.bodySmall,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewDetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isHighlight) KptTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = KptTheme.shapes.medium,
            )
            .padding(KptTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
    ) {
        Text(
            text = label,
            style = KptTheme.typography.labelMedium,
            color = if (isHighlight) KptTheme.colorScheme.onPrimaryContainer else KptTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = if (isHighlight) KptTheme.typography.headlineSmall else KptTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (isHighlight) KptTheme.colorScheme.onPrimaryContainer else KptTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
fun PreviewTransferScreenPreview() {
    MifosTheme {
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

        PreviewTransferScreen(
            amount = "100.00",
            transferDate = "11/09/25",
            transferDescription = "Dinner share",
            fromAccountName = "ALEJANDRO ESCUTIA",
            fromAccountNo = "00000002",
            recipientInfo = mockRecipient,
            isProcessing = false,
            onEditClick = {},
            onConfirmClick = {},
            onBackClick = {},
        )
    }
}

@Preview
@Composable
fun PreviewTransferScreenProcessingPreview() {
    MifosTheme {
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

        PreviewTransferScreen(
            amount = "100.00",
            transferDate = "11/09/25",
            transferDescription = "Dinner share",
            fromAccountName = "ALEJANDRO ESCUTIA",
            fromAccountNo = "00000002",
            recipientInfo = mockRecipient,
            isProcessing = true,
            onEditClick = {},
            onConfirmClick = {},
            onBackClick = {},
        )
    }
}
