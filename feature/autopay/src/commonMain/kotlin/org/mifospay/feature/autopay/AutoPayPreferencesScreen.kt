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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons

@Composable
fun AutoPayPreferencesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = "AutoPay Preferences",
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
                    imageVector = MifosIcons.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AutoPay Preferences",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                )

                Text(
                    text = "Customize your AutoPay experience and notification settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PreferencesSection(
                title = "Notifications",
                preferences = listOf(
                    PreferenceItem(
                        title = "Payment Confirmations",
                        description = "Receive notifications when payments are processed",
                        icon = MifosIcons.OutlinedNotifications,
                    ),
                    PreferenceItem(
                        title = "Failed Payment Alerts",
                        description = "Get notified when payments fail",
                        icon = MifosIcons.Warning,
                    ),
                    PreferenceItem(
                        title = "Schedule Reminders",
                        description = "Receive reminders before scheduled payments",
                        icon = MifosIcons.Schedule,
                    ),
                ),
            )

            PreferencesSection(
                title = "Security",
                preferences = listOf(
                    PreferenceItem(
                        title = "Two-Factor Authentication",
                        description = "Require 2FA for AutoPay changes",
                        icon = MifosIcons.Security,
                    ),
                    PreferenceItem(
                        title = "Payment Limits",
                        description = "Set maximum payment amounts",
                        icon = MifosIcons.AttachMoney,
                    ),
                ),
            )

            PreferencesSection(
                title = "General",
                preferences = listOf(
                    PreferenceItem(
                        title = "AutoPay Enabled",
                        description = "Enable or disable AutoPay functionality",
                        icon = MifosIcons.Power,
                    ),
                    PreferenceItem(
                        title = "Default Payment Method",
                        description = "Set your preferred payment method",
                        icon = MifosIcons.CreditCard,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun PreferencesSection(
    title: String,
    preferences: List<PreferenceItem>,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                preferences.forEach { preference ->
                    PreferenceRow(preference = preference)
                }
            }
        }
    }
}

@Composable
private fun PreferenceRow(
    preference: PreferenceItem,
    modifier: Modifier = Modifier,
) {
    var isEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = preference.icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = preference.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = preference.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it },
        )
    }
}

private data class PreferenceItem(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)
