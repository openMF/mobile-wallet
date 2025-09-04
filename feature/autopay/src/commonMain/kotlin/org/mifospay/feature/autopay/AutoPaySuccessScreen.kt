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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.AvatarBox
import template.core.base.designsystem.theme.KptTheme

@Composable
fun AutoPaySuccessScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBar = {
            MifosTopBar(
                topBarTitle = "AutoPay Success",
                backPress = onNavigateBack,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                MifosButton(
                    onClick = onNavigateToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text("Back to Home")
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = KptTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    AvatarBox(
                        icon = MifosIcons.CheckCircle,
                        backgroundColor = KptTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp),
                    )

                    Text(
                        text = "AutoPay Setup Complete!",
                        style = KptTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = "Your automatic payment schedule has been successfully created and activated. You will receive notifications before each payment is processed.",
                        style = KptTheme.typography.bodyMedium,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = KptTheme.colorScheme.surfaceContainerHigh,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "What's Next?",
                        style = KptTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SuccessItem(
                            icon = MifosIcons.OutlinedNotifications,
                            title = "Payment Notifications",
                            description = "You'll receive notifications before each scheduled payment",
                        )

                        SuccessItem(
                            icon = MifosIcons.History,
                            title = "Payment History",
                            description = "Track all your automatic payments in the history section",
                        )

                        SuccessItem(
                            icon = MifosIcons.Settings,
                            title = "Manage Schedule",
                            description = "Modify or cancel your AutoPay schedule anytime",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        AvatarBox(
            icon = icon,
            backgroundColor = KptTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp),
        )

        Column {
            Text(
                text = title,
                style = KptTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = description,
                style = KptTheme.typography.bodySmall,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
