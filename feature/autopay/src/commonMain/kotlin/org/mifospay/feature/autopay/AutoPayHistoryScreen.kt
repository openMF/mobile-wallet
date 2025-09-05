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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons

@Composable
fun AutoPayHistoryScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = "AutoPay History",
        backPress = onNavigateBack,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(getDummyHistoryItems()) { historyItem ->
                HistoryItemCard(historyItem = historyItem)
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    historyItem: AutoPayHistoryItem,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (historyItem.status) {
        "Completed" -> Color(0xFF4CAF50) // Green color for success
        "Failed" -> MaterialTheme.colorScheme.error
        null -> when (historyItem.title) {
            "Schedule Created" -> MaterialTheme.colorScheme.primary
            "Schedule Cancelled" -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = historyItem.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = statusColor,
                )

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = historyItem.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = historyItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = historyItem.amount,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = historyItem.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (historyItem.status != null) {
                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Status: ${historyItem.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private data class AutoPayHistoryItem(
    val title: String,
    val description: String,
    val amount: String,
    val date: String,
    val status: String?,
    val icon: ImageVector,
    val statusColor: Color,
)

private fun getDummyHistoryItems(): List<AutoPayHistoryItem> {
    return listOf(
        AutoPayHistoryItem(
            title = "Monthly Rent Payment",
            description = "AutoPay to Landlord Corp",
            amount = "$1,200.00",
            date = "Jan 15, 2024",
            status = "Completed",
            icon = MifosIcons.CheckCircle,
            statusColor = Color.Unspecified,
        ),
        AutoPayHistoryItem(
            title = "Internet Bill",
            description = "AutoPay to Comcast",
            amount = "$89.99",
            date = "Jan 10, 2024",
            status = "Completed",
            icon = MifosIcons.CheckCircle,
            statusColor = Color.Unspecified,
        ),
        AutoPayHistoryItem(
            title = "Electricity Bill",
            description = "AutoPay to Power Company",
            amount = "$156.75",
            date = "Jan 5, 2024",
            status = "Failed",
            icon = MifosIcons.Error,
            statusColor = Color.Unspecified,
        ),
        AutoPayHistoryItem(
            title = "Phone Bill",
            description = "AutoPay to Verizon",
            amount = "$85.50",
            date = "Dec 28, 2023",
            status = "Completed",
            icon = MifosIcons.CheckCircle,
            statusColor = Color.Unspecified,
        ),
        AutoPayHistoryItem(
            title = "Gym Membership",
            description = "AutoPay to Fitness Center",
            amount = "$45.00",
            date = "Dec 20, 2023",
            status = "Completed",
            icon = MifosIcons.CheckCircle,
            statusColor = Color.Unspecified,
        ),
        AutoPayHistoryItem(
            title = "Schedule Created",
            description = "New AutoPay schedule for Netflix",
            amount = "$15.99/month",
            date = "Dec 15, 2023",
            status = null,
            icon = MifosIcons.Add,
            statusColor = Color.Unspecified,
        ),
        AutoPayHistoryItem(
            title = "Schedule Cancelled",
            description = "AutoPay schedule for Spotify cancelled",
            amount = "$9.99/month",
            date = "Dec 10, 2023",
            status = null,
            icon = MifosIcons.Cancel,
            statusColor = Color.Unspecified,
        ),
    )
}
