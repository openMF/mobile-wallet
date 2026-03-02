/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.beneficiary.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.beneficiary.generated.resources.Res
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_add
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_add_beneficiary
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_add_beneficiary_hint
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_delete_beneficiary
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_edit_beneficiary
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_error_oops
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_no_beneficiaries
import mobile_wallet.feature.beneficiary.generated.resources.feature_beneficiary_unexpected_error_subtitle
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosProgressIndicator
import org.mifospay.core.ui.MifosProgressIndicatorOverlay
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.beneficiary.addupdatebeneficiary.BeneficiaryAddEditType
import org.mifospay.feature.beneficiary.deletebeneficiary.DeleteBeneficiaryBottomSheet
import org.mifospay.feature.beneficiary.deletebeneficiary.DeleteBeneficiaryState
import org.mifospay.feature.beneficiary.deletebeneficiary.DeleteBeneficiaryViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
fun BeneficiaryListScreen(
    onAddOrEditBeneficiary: (BeneficiaryAddEditType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BeneficiaryListViewModel = koinViewModel(),
    deleteViewModel: DeleteBeneficiaryViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val accountState by viewModel.accountState.collectAsStateWithLifecycle()
    val deleteState by deleteViewModel.state.collectAsStateWithLifecycle()

    // Observe delete success and trigger list refresh
    LaunchedEffect(deleteState.deleteSuccessful) {
        if (deleteState.deleteSuccessful) {
            viewModel.refreshBeneficiaryList()
            deleteViewModel.consumeDeleteSuccess()
        }
    }

    EventsEffect(viewModel) { event ->
        when (event) {
            is BeneficiaryListEvent.OnAddOrEditTPTBeneficiary -> onAddOrEditBeneficiary.invoke(event.type)

            is BeneficiaryListEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(getString(event.message))
                }
            }
        }
    }

    // Delete beneficiary bottom sheet - managed by separate ViewModel
    DeleteBeneficiaryBottomSheet(
        dialogState = deleteState.dialogState,
        onConfirmDelete = remember(deleteViewModel) {
            { beneficiaryId -> deleteViewModel.confirmDelete(beneficiaryId) }
        },
        onDismiss = remember(deleteViewModel) {
            { deleteViewModel.dismissDialog() }
        },
    )

    Box(modifier = modifier.fillMaxSize()) {
        BeneficiaryListScreenContent(
            state = accountState,
            onAction = remember(viewModel) {
                { viewModel.trySendAction(it) }
            },
            onDeleteBeneficiary = remember(deleteViewModel) {
                { id, name -> deleteViewModel.showDeleteConfirmation(id, name) }
            },
            snackbarHostState = snackbarHostState,
        )

        // Show overlay progress indicator over the list when deleting
        if (deleteState.dialogState is DeleteBeneficiaryState.DialogState.Deleting) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@Composable
private fun BeneficiaryListScreenContent(
    state: BeneficiaryListState.ViewState,
    onAction: (BeneficiaryListAction) -> Unit,
    onDeleteBeneficiary: (Long, String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        floatingActionButtonPosition = FabPosition.EndOverlay,
        snackbarHostState = snackbarHostState,
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.hasFab,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        onAction(BeneficiaryListAction.AddTPTBeneficiary)
                    },
                    icon = {
                        Icon(
                            imageVector = MifosIcons.Add,
                            contentDescription = stringResource(Res.string.feature_beneficiary_add),
                        )
                    },
                    text = {
                        Text(text = stringResource(Res.string.feature_beneficiary_add_beneficiary))
                    },
                )
            }
        },
    ) { paddingValues ->
        BeneficiariesList(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onAction = onAction,
            onDeleteBeneficiary = onDeleteBeneficiary,
        )
    }
}

@Composable
fun BeneficiariesList(
    state: BeneficiaryListState.ViewState,
    onAction: (BeneficiaryListAction) -> Unit,
    onDeleteBeneficiary: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is BeneficiaryListState.ViewState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MifosProgressIndicator()
            }
        }

        is BeneficiaryListState.ViewState.Content -> {
            if (state.beneficiaries.isEmpty()) {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_beneficiary_no_beneficiaries),
                    subTitle = stringResource(Res.string.feature_beneficiary_add_beneficiary_hint),
                    modifier = modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(KptTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                ) {
                    items(
                        items = state.beneficiaries,
                        key = { it.id },
                    ) { beneficiary ->
                        BeneficiaryItem(
                            beneficiary = beneficiary,
                            onClickEdit = { onAction(BeneficiaryListAction.EditBeneficiary(it)) },
                            onClickDelete = onDeleteBeneficiary,
                        )
                    }
                }
            }
        }

        is BeneficiaryListState.ViewState.Error -> {
            EmptyContentScreen(
                title = stringResource(Res.string.feature_beneficiary_error_oops),
                subTitle = stringResource(Res.string.feature_beneficiary_unexpected_error_subtitle),
                modifier = modifier.fillMaxSize(),
                iconTint = KptTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun BeneficiaryItem(
    beneficiary: Beneficiary,
    modifier: Modifier = Modifier,
    onClickEdit: (Beneficiary) -> Unit,
    onClickDelete: (Long, String) -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = KptTheme.shapes.small,
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
            contentColor = KptTheme.colorScheme.onSurface,
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
                    backgroundColor = KptTheme.colorScheme.tertiaryContainer,
                    contentColor = KptTheme.colorScheme.tertiary,
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            onClickEdit(beneficiary)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = KptTheme.colorScheme.surfaceContainerHighest,
                            contentColor = KptTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Icon(
                            imageVector = MifosIcons.Edit2,
                            contentDescription = stringResource(Res.string.feature_beneficiary_edit_beneficiary),
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            onClickDelete(beneficiary.id, beneficiary.name)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = KptTheme.colorScheme.errorContainer,
                            contentColor = KptTheme.colorScheme.error,
                        ),
                    ) {
                        Icon(
                            imageVector = MifosIcons.OutlinedDelete,
                            contentDescription = stringResource(Res.string.feature_beneficiary_delete_beneficiary),
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

@Preview
@Composable
fun PreviewBeneficiaryListScreen() {
    MifosTheme {
        BeneficiaryListScreenContent(
            state = BeneficiaryListState.ViewState.Loading,
            onAction = { },
            onDeleteBeneficiary = { _, _ -> },
            snackbarHostState = remember { SnackbarHostState() },
            modifier = Modifier,
        )
    }
}
