/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money.v2

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.send_money.generated.resources.Res
import mobile_wallet.feature.send_money.generated.resources.feature_select_account_placeholder
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_add_icon_desc
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_add_payee_subtitle
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_add_payee_title
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_pay_button
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_recents_title
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_send
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosBottomSheetScaffold
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosTextUserImage
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.SimpleSearchBar
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
fun SendMoneyv2Screen(
    navigateToSelectAccountScreen: () -> Unit,
    navigateBack: () -> Unit,
    navigateToBeneficiary: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SendMoneyV2ViewModel = koinViewModel(),
) {
    EventsEffect(viewModel) { event ->
        when (event) {
            SendMoneyV2Event.NavigateToSearchAccountSelection -> navigateToSelectAccountScreen()

            SendMoneyV2Event.NavigateBack -> navigateBack()

            SendMoneyV2Event.NavigateToBeneficiary -> navigateToBeneficiary()
        }
    }

    SendMoneyScreen(
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SendMoneyScreen(
    modifier: Modifier = Modifier,
    onAction: (SendMoneyV2Action) -> Unit,
) {
    MifosBottomSheetScaffold(
        modifier = modifier,
        topBar = {
            MifosTopBar(
                topBarTitle = stringResource(Res.string.feature_send_money_send),
                backPress = {
                    onAction(SendMoneyV2Action.NavigateBack)
                },
            )
        },
        sheetContent = {
            // TODO : If we can get recent payment details in self with toAccount number and  amount
            // show those list with pay button here and on click it should navigate to that id and price
//            RecentBottomSheet()
        },
        sheetPeekHeight = 0.dp,
    ) { paddingValues ->
        Column(
            Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = KptTheme.spacing.md),
        ) {
            SimpleSearchBar(
                query = "",
                placeHolder = stringResource(Res.string.feature_select_account_placeholder),
                onQueryChange = {},
                onClick = {
                    onAction(SendMoneyV2Action.OnSearchBarClicked)
                },
                enabled = false,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            AddPayeeCard(
                onClick = {
                    onAction(SendMoneyV2Action.OnAddPayeeClicked)
                },
            )
        }
    }
}

@Composable
fun AddPayeeCard(
    title: String = stringResource(Res.string.feature_send_money_add_payee_title),
    subtitle: String = stringResource(Res.string.feature_send_money_add_payee_subtitle),
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = KptTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(0.1f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = MifosIcons.Add,
                    contentDescription = stringResource(Res.string.feature_send_money_add_icon_desc),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.width(KptTheme.spacing.md))

            Column {
                Text(
                    text = title,
                    style = KptTheme.typography.titleSmall,
                )
                Text(
                    text = subtitle,
                    style = KptTheme.typography.bodySmall,
                )
            }
        }
    }
}

// TODO: remove it once we get data from self api
data class RecentTransaction(
    val name: String,
    val toAccount: String,
    val amount: String,
)

@Composable
private fun RecentBottomSheet() {
    // TODO : when we get api pass data from api.
    val recents = listOf(
        RecentTransaction("Alex Doe", "**** **** 1234", "₹1500"),
        RecentTransaction("Jane Smith", "**** **** 5678", "₹2200"),
        RecentTransaction("Mike Johnson", "**** **** 9012", "₹800"),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            Icon(
                imageVector = MifosIcons.History,
                contentDescription = stringResource(Res.string.feature_send_money_recents_title),
                tint = KptTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(Res.string.feature_send_money_recents_title),
                style = KptTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        recents.forEach { transaction ->
            RecentTransactionItem(transaction = transaction)
            Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
        }
    }
}

@Composable
private fun RecentTransactionItem(transaction: RecentTransaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(KptTheme.shapes.medium)
            .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            MifosTextUserImage(
                text = transaction.name.first().toString(),
                size = 40.dp,
            )

            Column {
                Text(
                    text = transaction.name,
                    style = KptTheme.typography.bodyLarge,
                )
                Text(
                    text = transaction.toAccount,
                    style = KptTheme.typography.bodyMedium,
                )
            }
        }

        MifosOutlinedButton(
            onClick = {
                // TODO pass the id and amount from here
            },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = KptTheme.colorScheme.primary,
            ),
        ) {
            Text(stringResource(Res.string.feature_send_money_pay_button))
        }
    }
}
