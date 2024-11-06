/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.common.DateHelper
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.ui.AvatarBox

@Composable
internal fun TransactionDetail(
    modifier: Modifier = Modifier,
    detail: TransferDetail,
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Transaction ID", style = MaterialTheme.typography.labelLarge)
                Text(text = detail.id.toString())
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = NewUi.onSurface.copy(0.15f),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Transaction Date", style = MaterialTheme.typography.labelLarge)
                val date = DateHelper.getDateAsString(detail.transferDate)
                Text(text = date)
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = NewUi.onSurface.copy(0.15f),
            )

            Text(
                text = "Paid To",
                style = MaterialTheme.typography.labelLarge,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent,
                ),
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = detail.toClient.displayName)
                    },
                    supportingContent = {
                        Text(text = detail.toAccount.accountNo)
                    },
                    leadingContent = {
                        AvatarBox(name = detail.toClient.displayName)
                    },
                    trailingContent = {
                        val amount = CurrencyFormatter.format(
                            detail.transferAmount,
                            currencyCode = detail.currency.code,
                            maximumFractionDigits = null,
                        )
                        Text(
                            text = amount,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    ),
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    Text(
                        text = detail.toOffice.name,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        text = detail.toAccountType.value,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = NewUi.onSurface.copy(0.15f),
            )

            Text(
                text = "Debited From",
                style = MaterialTheme.typography.labelLarge,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent,
                ),
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = detail.fromClient.displayName)
                    },
                    supportingContent = {
                        Text(text = detail.fromAccount.accountNo)
                    },
                    leadingContent = {
                        AvatarBox(name = detail.fromClient.displayName)
                    },
                    trailingContent = {
                        val amount = CurrencyFormatter.format(
                            detail.transferAmount,
                            currencyCode = detail.currency.code,
                            maximumFractionDigits = null,
                        )
                        Text(
                            text = amount,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    Text(
                        text = detail.fromOffice.name,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        text = detail.fromAccountType.value,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}
