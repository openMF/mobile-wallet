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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.send_money.generated.resources.Res
import mobile_wallet.feature.send_money.generated.resources.feature_select_account_placeholder
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_add_icon_desc
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_add_payee_subtitle
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_add_payee_title
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_pay_button
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_recents_title
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_send
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosTextUserImage
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.account.RecentPayee
import org.mifospay.core.ui.MifosProgressIndicatorMini
import org.mifospay.core.ui.SimpleSearchBar
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.KptMaterialTheme
import template.core.base.designsystem.KptTheme
import template.core.base.designsystem.theme.KptTheme

@Composable
fun SendMoneyv2Screen(
    navigateToSelectAccountScreen: () -> Unit,
    navigateBack: () -> Unit,
    navigateToBeneficiary: () -> Unit,
    navigateToMakeTransfer: (
        toOfficeId: Int,
        toClientId: Long,
        toAccountId: Int,
        accountName: String,
        accountNo: String,
    ) -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
    viewModel: SendMoneyV2ViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            SendMoneyV2Event.NavigateToSearchAccountSelection -> navigateToSelectAccountScreen()

            SendMoneyV2Event.NavigateBack -> navigateBack()

            SendMoneyV2Event.NavigateToBeneficiary -> navigateToBeneficiary()

            is SendMoneyV2Event.NavigateToTransfer -> {
                navigateToMakeTransfer(
                    event.toOfficeId,
                    event.toClientId,
                    event.toAccountId,
                    event.accountName,
                    event.accountNo,
                )
            }
        }
    }

    SendMoneyScreen(
        state = state,
        modifier = modifier,
        showTopBar = showTopBar,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun SendMoneyScreen(
    state: SendMoneyV2State,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
    onAction: (SendMoneyV2Action) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        topBar = {
            if (showTopBar) {
                MifosTopBar(
                    topBarTitle = stringResource(Res.string.feature_send_money_send),
                    backPress = {
                        onAction(SendMoneyV2Action.NavigateBack)
                    },
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = KptTheme.spacing.md),
        ) {
            item {
                Spacer(Modifier.height(KptTheme.spacing.md))

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

            // Recent Payees Section
            when (state.recentPayeesState) {
                RecentPayeesState.Loading -> {
                    item {
                        Spacer(Modifier.height(KptTheme.spacing.lg))
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(KptTheme.spacing.lg),
                            contentAlignment = Alignment.Center,
                        ) {
                            MifosProgressIndicatorMini()
                        }
                    }
                }

                RecentPayeesState.Success -> {
                    item {
                        Spacer(Modifier.height(KptTheme.spacing.lg))
                        RecentPayeesHeader()
                        Spacer(Modifier.height(KptTheme.spacing.sm))
                    }

                    items(state.recentPayees, key = { it.clientId }) { payee ->
                        RecentPayeeItem(
                            payee = payee,
                            onPayClick = { onAction(SendMoneyV2Action.OnPayRecentPayee(payee)) },
                        )
                    }
                }

                RecentPayeesState.Empty,
                RecentPayeesState.Error,
                -> {
                    // Don't show anything when empty or error
                }
            }
        }
    }
}

@Composable
private fun RecentPayeesHeader(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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

@Composable
private fun RecentPayeeItem(
    payee: RecentPayee,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(KptTheme.shapes.medium)
            .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            modifier = Modifier.weight(1f),
        ) {
            MifosTextUserImage(
                text = payee.initials,
                size = 40.dp,
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = payee.clientName,
                    style = KptTheme.typography.bodyLarge,
                    maxLines = 1,
                )
                Text(
                    text = "${payee.currency} ${payee.lastAmount.toInt()} • ${payee.accountNo}",
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }

        MifosOutlinedButton(
            onClick = onPayClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = KptTheme.colorScheme.primary,
            ),
        ) {
            Text(stringResource(Res.string.feature_send_money_pay_button))
        }
    }
}

@Preview
@Composable
private fun PreviewSendMoneyScreen() {
    KptMaterialTheme {
        SendMoneyScreen(
            state = SendMoneyV2State(),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSendMoneyScreenWithRecents() {
    val mockPayees = listOf(
        RecentPayee(
            clientId = 1,
            clientName = "John Doe",
            accountId = 101,
            accountNo = "ACC001",
            officeId = 1,
            officeName = "HQ",
            lastTransferDate = "2024-01-15",
            lastAmount = 50.0,
            currency = "USD",
        ),
        RecentPayee(
            clientId = 2,
            clientName = "Alice Smith",
            accountId = 102,
            accountNo = "ACC002",
            officeId = 1,
            officeName = "HQ",
            lastTransferDate = "2024-01-14",
            lastAmount = 120.0,
            currency = "USD",
        ),
    )

    KptMaterialTheme {
        SendMoneyScreen(
            state = SendMoneyV2State(
                recentPayees = mockPayees,
                recentPayeesState = RecentPayeesState.Success,
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun PreviewAddPayeeCard() {
    KptMaterialTheme {
        AddPayeeCard(
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewRecentPayeeItem() {
    KptMaterialTheme {
        RecentPayeeItem(
            payee = RecentPayee(
                clientId = 1,
                clientName = "Alex Doe",
                accountId = 101,
                accountNo = "ACC1234",
                officeId = 1,
                officeName = "HQ",
                lastTransferDate = "2024-01-15",
                lastAmount = 1500.0,
                currency = "USD",
            ),
            onPayClick = {},
        )
    }
}
