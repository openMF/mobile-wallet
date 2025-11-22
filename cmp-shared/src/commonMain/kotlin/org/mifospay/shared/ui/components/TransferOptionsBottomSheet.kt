/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import mobile_wallet.feature.payments.generated.resources.Res
import mobile_wallet.feature.payments.generated.resources.feature_payments_inter_bank_transfer_description
import mobile_wallet.feature.payments.generated.resources.feature_payments_inter_bank_transfer_title
import mobile_wallet.feature.payments.generated.resources.feature_payments_intra_bank_transfer_description
import mobile_wallet.feature.payments.generated.resources.feature_payments_intra_bank_transfer_title
import mobile_wallet.feature.payments.generated.resources.feature_payments_transfer_options_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.theme.MifosTheme
import template.core.base.designsystem.theme.KptTheme

@Composable
fun TransferOptionsBottomSheet(
    onIntraBankTransferClick: () -> Unit,
    onInterBankTransferClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosBottomSheet(
        onDismiss = onDismiss,
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                Text(
                    text = stringResource(Res.string.feature_payments_transfer_options_title),
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                    style = KptTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                // Intra-Bank Transfer Option
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.feature_payments_intra_bank_transfer_title),
                            style = KptTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = KptTheme.colorScheme.onSurface,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(Res.string.feature_payments_intra_bank_transfer_description),
                            style = KptTheme.typography.bodySmall,
                            color = KptTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onIntraBankTransferClick()
                        },
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                    color = KptTheme.colorScheme.outlineVariant,
                )

                // Inter-Bank Transfer Option
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.feature_payments_inter_bank_transfer_title),
                            style = KptTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = KptTheme.colorScheme.onSurface,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(Res.string.feature_payments_inter_bank_transfer_description),
                            style = KptTheme.typography.bodySmall,
                            color = KptTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onInterBankTransferClick()
                        },
                )
            }
        },
    )
}

@Preview
@Composable
fun TransferOptionsBottomSheetPreview() {
    MifosTheme {
        TransferOptionsBottomSheet(
            onIntraBankTransferClick = {},
            onInterBankTransferClick = {},
            onDismiss = {},
        )
    }
}
