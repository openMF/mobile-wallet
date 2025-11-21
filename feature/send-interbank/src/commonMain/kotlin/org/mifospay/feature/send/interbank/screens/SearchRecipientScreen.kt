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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import mobile_wallet.feature.send_interbank.generated.resources.Res
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_search_recipient
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_enter_phone_number
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_no_results
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_no_recipients_found
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_found_recipients
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_enter_phone_to_search
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_account
import mobile_wallet.feature.send_interbank.generated.resources.feature_send_interbank_id
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.feature.send.interbank.RecipientInfo
import template.core.base.designsystem.theme.KptTheme

@Composable
fun SearchRecipientScreen(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    recipients: List<RecipientInfo>,
    onRecipientSelected: (RecipientInfo) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBar = {
            MifosTopBar(
                topBarTitle = stringResource(Res.string.feature_send_interbank_search_recipient),
                backPress = onBackClick,
            )
        },
        containerColor = KptTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(KptTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            MifosTextField(
                label = stringResource(Res.string.feature_send_interbank_enter_phone_number),
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            if (recipients.isEmpty() && searchQuery.isNotEmpty()) {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_send_interbank_no_results),
                    subTitle = stringResource(Res.string.feature_send_interbank_no_recipients_found),
                )
            } else if (recipients.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.feature_send_interbank_found_recipients, recipients.size),
                    style = KptTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                ) {
                    items(recipients) { recipient ->
                        RecipientSelectionCard(
                            recipient = recipient,
                            onClick = { onRecipientSelected(recipient) },
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = KptTheme.spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.feature_send_interbank_enter_phone_to_search),
                        style = KptTheme.typography.bodyMedium,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipientSelectionCard(
    recipient: RecipientInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surfaceContainer,
        ),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = recipient.clientName,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            supportingContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_send_interbank_account) + ": ${recipient.accountNo}",
                        style = KptTheme.typography.bodySmall,
                    )
                    Text(
                        text = stringResource(Res.string.feature_send_interbank_id) + ": ${recipient.clientId}",
                        style = KptTheme.typography.bodySmall,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            leadingContent = {
                AvatarBox(
                    icon = MifosIcons.Person,
                    backgroundColor = KptTheme.colorScheme.secondaryContainer,
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
fun SearchRecipientScreenPreview() {
    MifosTheme {
        val mockRecipients = listOf(
            RecipientInfo(
                clientId = 1L,
                officeId = 1,
                accountId = 1,
                accountType = 2,
                clientName = "Pedro Barreto",
                accountNo = "9880000020",
            ),
            RecipientInfo(
                clientId = 2L,
                officeId = 1,
                accountId = 2,
                accountType = 2,
                clientName = "Maria Garcia",
                accountNo = "9880000021",
            ),
        )

        SearchRecipientScreen(
            searchQuery = "988",
            onSearchQueryChanged = {},
            recipients = mockRecipients,
            onRecipientSelected = {},
            onBackClick = {},
        )
    }
}

@Preview
@Composable
fun SearchRecipientScreenEmptyPreview() {
    MifosTheme {
        SearchRecipientScreen(
            searchQuery = "",
            onSearchQueryChanged = {},
            recipients = emptyList(),
            onRecipientSelected = {},
            onBackClick = {},
        )
    }
}

@Preview
@Composable
fun SearchRecipientScreenNoResultsPreview() {
    MifosTheme {
        SearchRecipientScreen(
            searchQuery = "999999",
            onSearchQueryChanged = {},
            recipients = emptyList(),
            onRecipientSelected = {},
            onBackClick = {},
        )
    }
}
