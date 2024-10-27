/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.savedcards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.savedcards.generated.resources.Res
import mobile_wallet.feature.savedcards.generated.resources.feature_savedcards_error_oops
import mobile_wallet.feature.savedcards.generated.resources.feature_savedcards_loading
import mobile_wallet.feature.savedcards.generated.resources.feature_savedcards_subtitle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.savedcards.SavedCard
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.savedcards.createOrUpdate.CardAddEditType
import org.mifospay.feature.savedcards.utils.CreditCardUtils.detectCardType
import org.mifospay.feature.savedcards.utils.CreditCardUtils.maskCreditCardNumber

/**
 * Known Issue, On deleting card, state isn't updating automatically
 * whereas on adding or updating, card state is updating properly,
 * This issue will be fixed soon.
 */
@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    navigateToAddEdit: (CardAddEditType) -> Unit,
    navigateToViewDetail: (Long) -> Unit,
    viewModel: CardsScreenViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val cartState by viewModel.cardState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is CardEvent.OnNavigateToAddEdit -> navigateToAddEdit.invoke(event.type)

            is CardEvent.OnNavigateToCardDetails -> navigateToViewDetail.invoke(event.cardId)

            is CardEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    SavedCardDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(CardAction.DismissDialog) }
        },
    )

    CardsScreen(
        modifier = modifier,
        state = cartState,
        snackbarHostState = snackbarHostState,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun SavedCardDialogs(
    dialogState: CardState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is CardState.DialogState.DeleteCard -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                title = stringResource(dialogState.title),
                message = stringResource(dialogState.message),
            ),
            onConfirm = dialogState.onConfirm,
            onDismissRequest = onDismissRequest,
        )

        is CardState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is CardState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Composable
internal fun CardsScreen(
    state: ViewState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (CardAction) -> Unit,
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
                        onAction(CardAction.AddNewCard)
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
                is ViewState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_savedcards_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is ViewState.Empty -> {
                    EmptyContentScreen(
                        title = state.title,
                        subTitle = state.message,
                        btnText = state.btnText,
                        btnIcon = state.btnIcon,
                        onClick = {
                            onAction(CardAction.AddNewCard)
                        },
                        modifier = Modifier,
                    )
                }

                is ViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_savedcards_error_oops),
                        subTitle = stringResource(Res.string.feature_savedcards_subtitle),
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                is ViewState.Content -> {
                    CardsScreenContent(
                        state = state,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun CardsScreenContent(
    state: ViewState.Content,
    modifier: Modifier = Modifier,
    onAction: (CardAction) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = state.cards,
            key = { it.id },
        ) { savedCard ->
            SavedCardItem(
                savedCard = savedCard,
                onClick = {
                    onAction(CardAction.ViewCardDetails(it))
                },
                onClickEdit = {
                    onAction(CardAction.EditCardDetails(it))
                },
                onClickDelete = {
                    onAction(CardAction.DeleteCardClicked(it))
                },
            )
        }
    }
}

@Composable
private fun SavedCardItem(
    savedCard: SavedCard,
    modifier: Modifier = Modifier,
    onClick: (cardId: Long) -> Unit,
    onClickEdit: (cardId: Long) -> Unit,
    onClickDelete: (cardId: Long) -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
        onClick = {
            onClick(savedCard.id)
        },
    ) {
        ListItem(
            headlineContent = {
                Text(text = savedCard.fullName)
            },
            supportingContent = {
                Text(text = savedCard.cardNumber.maskCreditCardNumber())
            },
            leadingContent = {
                val cardImage = savedCard.cardNumber.detectCardType().cardImage

                Box(
                    modifier = Modifier
                        .size(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        imageVector = vectorResource(cardImage),
                        contentDescription = "Card Image",
                        modifier = Modifier.size(48.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            onClickEdit(savedCard.id)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                    ) {
                        Icon(
                            imageVector = MifosIcons.Edit2,
                            contentDescription = "Edit Card",
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            onClickDelete(savedCard.id)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                    ) {
                        Icon(
                            imageVector = MifosIcons.OutlinedDelete,
                            contentDescription = "Delete Card",
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
