/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.standing_instruction.generated.resources.Res
import mobile_wallet.feature.standing_instruction.generated.resources.feature_standing_instruction_error_fetching_si_list
import mobile_wallet.feature.standing_instruction.generated.resources.feature_standing_instruction_error_oops
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.standinginstruction.StandingInstruction
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.RevealDirection
import org.mifospay.core.ui.RevealSwipe
import org.mifospay.core.ui.rememberRevealState
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.standing.instruction.components.FrequencyChip
import org.mifospay.feature.standing.instruction.createOrUpdate.SIAddEditType

@Composable
fun StandingInstructionsScreen(
    onAddEditSI: (SIAddEditType) -> Unit,
    onShowSIDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StandingInstructionViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is SIEvent.OnAddEditSI -> onAddEditSI.invoke(event.type)
            is SIEvent.OnNavigateToSIDetails -> onShowSIDetails.invoke(event.siId)
            is SIEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    SIDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(SIAction.DismissDialog) }
        },
    )

    StandingInstructionScreen(
        state = viewState,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = viewModel::trySendAction,
    )
}

@Composable
private fun SIDialogs(
    dialogState: SIUiState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is SIUiState.DialogState.DeleteSI -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                title = stringResource(dialogState.title),
                message = stringResource(dialogState.message),
            ),
            onConfirm = dialogState.onConfirm,
            onDismissRequest = onDismissRequest,
        )

        is SIUiState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is SIUiState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Composable
internal fun StandingInstructionScreen(
    state: SIViewState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (SIAction) -> Unit,
) {
    MifosScaffold(
        snackbarHostState = snackbarHostState,
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = modifier,
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.hasFab,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(
                    onClick = {
                        onAction(SIAction.AddNewSI)
                    },
                ) {
                    Icon(imageVector = MifosIcons.Add, "Add")
                }
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is SIViewState.Loading -> {
                    MifosLoadingWheel(contentDesc = "Loading")
                }

                is SIViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_standing_instruction_error_oops),
                        subTitle = stringResource(Res.string.feature_standing_instruction_error_fetching_si_list),
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                is SIViewState.Empty -> {
                    EmptyContentScreen(
                        title = state.title,
                        subTitle = state.message,
                        btnText = state.btnText,
                        btnIcon = state.btnIcon,
                        onClick = {
                            onAction(SIAction.AddNewSI)
                        },
                        modifier = Modifier,
                    )
                }

                is SIViewState.Content -> {
                    StandingInstructionScreenContent(
                        state = state,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun StandingInstructionScreenContent(
    state: SIViewState.Content,
    modifier: Modifier = Modifier,
    onAction: (SIAction) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = state.list,
            key = { item -> item.id },
        ) { item ->
            SIItem(
                item = item,
                onClick = {
                    onAction(SIAction.ViewSIDetails(it))
                },
                onClickEdit = {
                    onAction(SIAction.EditSIDetails(it))
                },
                onClickDelete = {
                    onAction(SIAction.OnDeleteSI(it))
                },
            )
        }
    }
}

@Composable
private fun SIItem(
    item: StandingInstruction,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit,
    onClickEdit: (Long) -> Unit,
    onClickDelete: (Long) -> Unit,
) {
    val state = rememberRevealState(
        maxRevealDp = 105.dp,
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
                IconButton(
                    onClick = { onClickEdit(item.id) },
                ) {
                    Icon(
                        imageVector = MifosIcons.Edit2,
                        contentDescription = "Edit",
                    )
                }

                IconButton(
                    onClick = { onClickDelete(item.id) },
                ) {
                    Icon(
                        imageVector = MifosIcons.Delete,
                        contentDescription = "Delete",
                    )
                }
            }
        },
        onContentClick = { onClick(item.id) },
    ) {
        val priorityColor = when (item.priority.id) {
            1L -> Color(0xFFFF4444)
            2L -> Color(0xFFFF8800)
            3L -> Color(0xFFFFBB33)
            4L -> Color(0xFF99CC00)
            else -> Color.Gray
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(it),
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(
                        color = priorityColor,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                    )
                    .align(Alignment.CenterStart),
            )

            OutlinedCard(
                modifier = modifier.fillMaxWidth(),
                shape = it,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.Transparent,
                ),
            ) {
                ListItem(
                    headlineContent = {
                        Text(text = item.name)
                    },
                    supportingContent = {
                        Text(text = "${item.toClient.displayName} | ${item.status.value}")
                    },
                    trailingContent = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FrequencyChip(
                                option = item.recurrenceFrequency,
                                interval = item.recurrenceInterval.toString(),
                            )

                            val amount = CurrencyFormatter.format(
                                balance = item.amount,
                                currencyCode = "USD",
                                maximumFractionDigits = null,
                            )

                            Text(
                                text = amount,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
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
}
