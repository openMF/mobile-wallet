/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.invoices

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.invoices.generated.resources.Res
import mobile_wallet.feature.invoices.generated.resources.ic_check
import mobile_wallet.feature.invoices.generated.resources.ic_remove
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.model.datatables.invoice.Invoice
import org.mifospay.core.ui.AvatarBox

@Composable
internal fun InvoiceItem(
    invoice: Invoice,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            onClick(invoice.invoiceId)
        },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        ListItem(
            headlineContent = {
                Text(text = invoice.title)
            },
            supportingContent = {
                Text(text = "${invoice.date} | ${invoice.consumerName}")
            },
            leadingContent = {
                AvatarBox(
                    icon = vectorResource(
                        if (invoice.status == 0L) {
                            Res.drawable.ic_remove
                        } else {
                            Res.drawable.ic_check
                        },
                    ),
                    contentColor = if (invoice.status == 1L) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            },
            trailingContent = {
                Text(
                    text = invoice.amount.toString(),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewInvoiceItem() {
    InvoiceItem(
        invoice = Invoice(
            id = 6597,
            clientId = 3986,
            consumerId = "fusce",
            consumerName = "Lara Shepherd",
            amount = 2.3,
            itemsBought = "quaestio",
            status = 8907,
            transactionId = "sem",
            invoiceId = 5209,
            title = "interdum",
            date = "quem",
            createdAt = listOf(),
            updatedAt = listOf(),
        ),
        onClick = {},
    )
}
