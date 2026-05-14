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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons

@Composable
fun AutoPayRulesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = "AutoPay Rules",
        backPress = onNavigateBack,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = MifosIcons.Rule,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AutoPay Rules & Policies",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                )

                Text(
                    text = "Understand how AutoPay works and the rules that govern automatic payments.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RulesSection(
                title = "General Rules",
                rules = listOf(
                    "AutoPay schedules can be set up for recurring payments",
                    "Maximum payment amount is limited to $10,000 per transaction",
                    "Payments are processed on the scheduled date",
                    "Failed payments will be retried up to 3 times",
                    "You can pause or cancel schedules at any time",
                ),
            )

            RulesSection(
                title = "Frequency Limits",
                rules = listOf(
                    "Daily: Maximum 1 payment per day",
                    "Weekly: Maximum 2 payments per week",
                    "Monthly: Maximum 4 payments per month",
                    "Yearly: Maximum 12 payments per year",
                ),
            )

            RulesSection(
                title = "Security & Privacy",
                rules = listOf(
                    "All payment data is encrypted and secure",
                    "You will receive notifications for all AutoPay activities",
                    "You can view payment history and status anytime",
                    "AutoPay can be disabled temporarily or permanently",
                ),
            )

            RulesSection(
                title = "Cancellation Policy",
                rules = listOf(
                    "Schedules can be cancelled before the next payment date",
                    "Cancelled schedules cannot be reactivated",
                    "You must create a new schedule after cancellation",
                    "No fees are charged for cancelling AutoPay schedules",
                ),
            )
        }
    }
}

@Composable
private fun RulesSection(
    title: String,
    rules: List<String>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rules.forEach { rule ->
                    RuleItem(rule = rule)
                }
            }
        }
    }
}

@Composable
private fun RuleItem(
    rule: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp),
        )

        Text(
            text = rule,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}
