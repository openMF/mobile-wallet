/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.accounts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.accounts.generated.resources.Res
import mobile_wallet.feature.accounts.generated.resources.baseline_check
import mobile_wallet.feature.accounts.generated.resources.baseline_unchecked
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_error_oops
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_loading
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_unexpected_error_subtitle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.savingsaccount.Status
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosSmallChip
import org.mifospay.core.ui.RevealDirection
import org.mifospay.core.ui.RevealSwipe
import org.mifospay.core.ui.rememberRevealState
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.accounts.beneficiary.BeneficiaryAddEditType
import org.mifospay.feature.accounts.savingsaccount.SavingsAddEditType

@Composable
fun AccountsScreen(
    onViewSavingAccountDetails: (Long) -> Unit,
    onAddEditSavingsAccount: (SavingsAddEditType) -> Unit,
    onAddOrEditBeneficiary: (BeneficiaryAddEditType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val accountState by viewModel.accountState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is AccountEvent.OnAddEditSavingsAccount -> {
                onAddEditSavingsAccount(event.type)
            }

            is AccountEvent.OnNavigateToAccountDetail -> {
                onViewSavingAccountDetails(event.accountId)
            }

            is AccountEvent.OnAddOrEditTPTBeneficiary -> {
                onAddOrEditBeneficiary(event.type)
            }

            is AccountEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    AccountDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(AccountAction.DismissDialog) }
        },
    )

    AccountsScreenContent(
        defaultAccountId = state.defaultAccountId,
        state = accountState,
        onAction = viewModel::trySendAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
internal fun AccountsScreenContent(
    defaultAccountId: Long?,
    state: AccountState.ViewState,
    onAction: (AccountAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        floatingActionButtonPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.hasFab,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(
                    onClick = {
                        onAction(AccountAction.CreateSavingsAccount)
                    },
                ) {
                    Icon(imageVector = MifosIcons.Add, "Add")
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is AccountState.ViewState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_accounts_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is AccountState.ViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_accounts_error_oops),
                        subTitle = stringResource(Res.string.feature_accounts_unexpected_error_subtitle),
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                is AccountState.ViewState.Content -> {
                    AccountsScreenContent(
                        state = state,
                        defaultAccountId = defaultAccountId,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
internal fun AccountsScreenContent(
    defaultAccountId: Long?,
    state: AccountState.ViewState.Content,
    onAction: (AccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountsList(
        modifier = modifier,
        defaultAccountId = defaultAccountId,
        accounts = state.accounts,
        beneficiaryList = state.beneficiaries,
        onAddTPTBeneficiary = {
            onAction(AccountAction.AddTPTBeneficiary)
        },
        onClickEditBeneficiary = {
            onAction(AccountAction.EditBeneficiary(it))
        },
        onClickDeleteBeneficiary = {
            onAction(AccountAction.DeleteBeneficiary(it))
        },
        onAccountClicked = { accId, accNo ->
            onAction(AccountAction.SetDefaultAccount(accId, accNo))
        },
        onClickEditAccount = {
            onAction(AccountAction.EditSavingsAccount(it))
        },
        onClickViewAccount = {
            onAction(AccountAction.ViewAccountDetails(it))
        },
    )
}

@Composable
private fun AccountsList(
    defaultAccountId: Long?,
    accounts: List<Account>,
    beneficiaryList: List<Beneficiary>,
    onAccountClicked: (Long, String) -> Unit,
    onAddTPTBeneficiary: () -> Unit,
    onClickEditBeneficiary: (Beneficiary) -> Unit,
    onClickDeleteBeneficiary: (Long) -> Unit,
    onClickEditAccount: (Long) -> Unit,
    onClickViewAccount: (Long) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Savings Account",
                style = MaterialTheme.typography.labelLarge,
            )
        }

        items(
            items = accounts,
            key = { it.id },
        ) { account ->
            AccountItem(
                account = account,
                isDefault = defaultAccountId == account.id,
                onClick = onAccountClicked,
                onClickEditAccount = onClickEditAccount,
                onClickViewAccount = onClickViewAccount,
            )
        }

        item {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = NewUi.onSurface.copy(alpha = 0.05f),
            )
        }

        item {
            Text(
                text = "Beneficiaries",
                style = MaterialTheme.typography.labelLarge,
            )
        }

        items(
            items = beneficiaryList,
            key = { it.accountNumber },
        ) { beneficiary ->
            BeneficiaryItem(
                beneficiary = beneficiary,
                onClickEdit = onClickEditBeneficiary,
                onClickDelete = onClickDeleteBeneficiary,
            )
        }

        item {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 1.dp,
                color = NewUi.onSurface.copy(alpha = 0.05f),
            )
        }

        item {
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                MifosOutlinedButton(
                    text = {
                        Text(text = "Add Beneficiary")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = MifosIcons.Add,
                            contentDescription = "add",
                        )
                    },
                    onClick = onAddTPTBeneficiary,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun AccountItem(
    account: Account,
    isDefault: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Long, String) -> Unit,
    onClickEditAccount: (Long) -> Unit,
    onClickViewAccount: (Long) -> Unit,
) {
    val state = rememberRevealState(
        maxRevealDp = if (account.status.submittedAndPendingApproval) 105.dp else 75.dp,
        directions = setOf(RevealDirection.EndToStart),
    )
    RevealSwipe(
        modifier = modifier,
        state = state,
        shape = RoundedCornerShape(8.dp),
        backgroundCardStartColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        backgroundCardEndColor = MaterialTheme.colorScheme.primary,
        backgroundStartActionLabel = null,
        backgroundEndActionLabel = "Edit",
        card = { shape, content ->
            Card(
                modifier = Modifier.matchParentSize(),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = Color.Transparent,
                ),
                shape = shape,
                content = content,
            )
        },
        hiddenContentEnd = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedVisibility(
                    visible = account.status.submittedAndPendingApproval,
                ) {
                    IconButton(
                        onClick = { onClickEditAccount(account.id) },
                    ) {
                        Icon(
                            imageVector = MifosIcons.Edit2,
                            contentDescription = "Edit",
                        )
                    }
                }

                IconButton(
                    onClick = { onClickViewAccount(account.id) },
                ) {
                    Icon(
                        imageVector = MifosIcons.Info,
                        contentDescription = "Info",
                    )
                }
            }
        },
        onContentClick = if (account.status.active) {
            { onClick(account.id, account.number) }
        } else {
            null
        },
    ) {
        OutlinedCard(
            modifier = modifier.fillMaxWidth(),
            shape = it,
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent,
            ),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = account.name)
                },
                supportingContent = {
                    Text(text = account.number)
                },
                leadingContent = {
                    AvatarBox(
                        icon = MifosIcons.Bank,
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SavingAccountStatusCard(account.status)

                        AnimatedVisibility(isDefault) {
                            OutlinedCard(
                                onClick = {},
                                shape = RoundedCornerShape(4.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                ),
                            ) {
                                Text(
                                    text = "Default",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isDefault) {
                                vectorResource(Res.drawable.baseline_check)
                            } else {
                                vectorResource(Res.drawable.baseline_unchecked)
                            },
                            contentDescription = "check",
                        )
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                ),
            )
        }
    }
}

@Composable
private fun BeneficiaryItem(
    beneficiary: Beneficiary,
    modifier: Modifier = Modifier,
    onClickEdit: (Beneficiary) -> Unit,
    onClickDelete: (Long) -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        ListItem(
            headlineContent = {
                Text(text = beneficiary.name)
            },
            supportingContent = {
                Text(text = beneficiary.accountNumber)
            },
            leadingContent = {
                AvatarBox(
                    icon = MifosIcons.AccountCircle,
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            onClickEdit(beneficiary)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                    ) {
                        Icon(
                            imageVector = MifosIcons.Edit2,
                            contentDescription = "Edit Beneficiary",
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            onClickDelete(beneficiary.id)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                    ) {
                        Icon(
                            imageVector = MifosIcons.OutlinedDelete,
                            contentDescription = "Delete Beneficiary",
                        )
                    }
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun AccountDialogs(
    dialogState: AccountState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is AccountState.DialogState.DeleteBeneficiary -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                title = stringResource(dialogState.title),
                message = stringResource(dialogState.message),
            ),
            onConfirm = dialogState.onConfirm,
            onDismissRequest = onDismissRequest,
        )

        is AccountState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is AccountState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SavingAccountStatusCard(
    status: Status,
    modifier: Modifier = Modifier,
) {
    val statusChips = listOf(
        "Pending Approval" to status.submittedAndPendingApproval,
        "Approved" to status.approved,
        "Rejected" to status.rejected,
        "Withdrawn" to status.withdrawnByApplicant,
        "Closed" to status.closed,
        "Prematurely Closed" to status.prematureClosed,
        "Transfer in Progress" to status.transferInProgress,
        "Transfer on Hold" to status.transferOnHold,
        "Matured" to status.matured,
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        statusChips.forEach { (label, isActive) ->
            if (isActive) {
                StatusChip(label)
            }
        }
    }
}

@Composable
private fun StatusChip(label: String) {
    val color = when (label) {
        "Pending Approval" -> Color(0xFFFFF9C4)
        "Approved" -> Color(0xFFC8E6C9)
        "Rejected" -> Color(0xFFFFCDD2)
        "Withdrawn" -> Color(0xFFE1BEE7)
        "Active" -> Color(0xFFBBDEFB)
        "Closed" -> Color(0xFFCFD8DC)
        "Prematurely Closed" -> Color(0xFFD7CCC8)
        "Transfer in Progress" -> Color(0xFFFFE0B2)
        "Transfer on Hold" -> Color(0xFFF0F4C3)
        "Matured" -> Color(0xFFB2DFDB)
        else -> Color(0xFFEFEFEF)
    }

    MifosSmallChip(
        label = label,
        containerColor = color,
    )
}
