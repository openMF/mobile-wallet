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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.send_money.generated.resources.Res
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_send
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosBottomSheetScaffold
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextUserImage
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.MifosSearchBar
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme
import v2.SendMoneyV2Action
import v2.SendMoneyV2Event
import v2.SendMoneyV2ViewModel

@Composable
fun SendMoneyv2Screen(
    navigateToSelectAccountScreen: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SendMoneyV2ViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            SendMoneyV2Event.NavigateToSearchAccountSelection -> navigateToSelectAccountScreen()

            SendMoneyV2Event.NavigateBack -> navigateBack()
        }
    }

    SendMoneyScreen(
        showTopBar = true,
        modifier = Modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SendMoneyScreen(
    showTopBar: Boolean,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (SendMoneyV2Action) -> Unit,
) {

        MifosBottomSheetScaffold(
            modifier = modifier,
            topBar = {
                AnimatedVisibility(
                    visible = showTopBar,
                ) {
                    MifosTopBar(
                        topBarTitle = stringResource(Res.string.feature_send_money_send),
                        backPress = {
                            onAction(SendMoneyV2Action.NavigateBack)
                        },
                    )
                }
            },
            sheetContent = {
                //TODO : If we can get recent payment details in self with toAccount number and  amount
                // show those list with pay button here and on click it should navigate to that id and price
                RecentBottomSheet()
            },
            sheetPeekHeight = 200.dp,
        ) { paddingValues ->
            Column(
                Modifier.padding(paddingValues).padding(horizontal = KptTheme.spacing.md),
            ) {
                MifosSearchBar(
                    query = "",
                    placeHolder = "",
                    onQueryChange = {},
                    onSearch = {},
                    onClick = {
                        onAction(SendMoneyV2Action.OnSearchBarClicked)
                    },
                    enabled = false,
                )
                Spacer(Modifier.height(KptTheme.spacing.md))
                AddPayeeCard(
                    onClick = {}
                )
            }
        }

}

@Composable
fun AddPayeeCard(
    title: String = "Add Payee",
    subtitle: String = "Add Payee to transfer money quickly",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = KptTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Add Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MifosIcons.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.surface
                )
            }

            Spacer(modifier = Modifier.width(KptTheme.spacing.md))

            // Texts
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

//TODO: remove it once we get data from self api
data class RecentTransaction(
    val name: String,
    val toAccount: String,
    val amount: String,
)

@Composable
private fun RecentBottomSheet() {
    //TODO : when we get api pass data from api.
    val recents = listOf(
        RecentTransaction("Alex Doe", "**** **** 1234", "₹1500"),
        RecentTransaction("Jane Smith", "**** **** 5678", "₹2200"),
        RecentTransaction("Mike Johnson", "**** **** 9012", "₹800"),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.md)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm)
        ) {
            Icon(
                imageVector = MifosIcons.History,
                contentDescription = "Recents",
                tint = KptTheme.colorScheme.primary
            )
            Text(
                text = "Recents",
                style = KptTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm)
        ) {

            MifosTextUserImage(
                text = transaction.name.first().toString(),
                size = 40.dp
            )

            Column {
                Text(
                    text = transaction.name,
                    style = KptTheme.typography.bodyLarge
                )
                Text(
                    text = transaction.toAccount,
                    style = KptTheme.typography.bodyMedium
                )
            }
        }


        MifosOutlinedButton(
            onClick = {
                //TODO pass the id and amount from here
            },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = KptTheme.colorScheme.primary
            ),
        ){
            Text("Pay")
        }
    }
}
