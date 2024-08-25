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

import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utility.AddCardChip
import org.mifospay.savedcards.R

@Composable
fun CardsScreen(
    onEditCard: (Card) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardsScreenViewModel = hiltViewModel(),
) {
    val cardState by viewModel.cardState.collectAsStateWithLifecycle()
    val cardListUiState by viewModel.cardListUiState.collectAsStateWithLifecycle()

    var showCardBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showConfirmDeleteDialog by rememberSaveable { mutableStateOf(false) }

    var deleteCardID by rememberSaveable { mutableStateOf<Int?>(null) }

    if (showCardBottomSheet) {
        AddCardDialogSheet(
            cancelClicked = {
                showCardBottomSheet = false
            },
            addClicked = {
                showCardBottomSheet = false
                viewModel.addCard(it)
            },
            onDismiss = {
                showCardBottomSheet = false
            },
        )
    }

    if (showConfirmDeleteDialog) {
        MifosDialogBox(
            showDialogState = true,
            onDismiss = { showConfirmDeleteDialog = false },
            title = R.string.feature_savedcards_delete_card,
            confirmButtonText = R.string.feature_savedcards_yes,
            onConfirm = {
                deleteCardID?.let { viewModel.deleteCard(it) }
                showConfirmDeleteDialog = false
            },
            dismissButtonText = R.string.feature_savedcards_no,
            message = R.string.feature_savedcards_confirm_delete_card,
        )
    }

    CardsScreen(
        cardState = cardState,
        cardListUiState = cardListUiState,
        onEditCard = onEditCard,
        onDeleteCard = {
            showConfirmDeleteDialog = true
            deleteCardID = it.id
        },
        onAddBtn = { showCardBottomSheet = true },
        updateQuery = viewModel::updateSearchQuery,
        modifier = modifier,
    )
}

enum class CardMenuAction {
    EDIT,
    DELETE,
    CANCEL,
}

@Composable
@VisibleForTesting
internal fun CardsScreen(
    cardState: CardsUiState,
    cardListUiState: CardsUiState,
    onEditCard: (Card) -> Unit,
    onDeleteCard: (Card) -> Unit,
    onAddBtn: () -> Unit,
    updateQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (cardState) {
            CardsUiState.Loading -> {
                MfLoadingWheel(
                    contentDesc = stringResource(R.string.feature_savedcards_loading),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                )
            }

            is CardsUiState.Empty -> {
                NoCardAddCardsScreen(onAddBtn)
            }

            is CardsUiState.Error -> {
                EmptyContentScreen(
                    title = stringResource(id = R.string.feature_savedcards_error_oops),
                    subTitle = stringResource(id = R.string.feature_savedcards_unexpected_error_subtitle),
                    modifier = Modifier,
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconImageVector = MifosIcons.Info,
                )
            }

            is CardsUiState.CreditCardForm -> {
                CardsScreenContent(
                    cardList = (cardListUiState as CardsUiState.CreditCardForm).cards,
                    onAddBtn = onAddBtn,
                    onDeleteCard = onDeleteCard,
                    onEditCard = onEditCard,
                    updateQuery = updateQuery,
                )
            }

            is CardsUiState.Success -> {
                when (cardState.cardsUiEvent) {
                    CardsUiEvent.CARD_ADDED_SUCCESSFULLY -> {
                        Toast.makeText(
                            LocalContext.current,
                            stringResource(id = R.string.feature_savedcards_card_added_successfully),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

                    CardsUiEvent.CARD_UPDATED_SUCCESSFULLY -> {
                        Toast.makeText(
                            LocalContext.current,
                            stringResource(id = R.string.feature_savedcards_card_updated_successfully),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

                    CardsUiEvent.CARD_DELETED_SUCCESSFULLY -> {
                        Toast.makeText(
                            LocalContext.current,
                            stringResource(id = R.string.feature_savedcards_card_deleted_successfully),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }
    }
}

@Composable
private fun CardsScreenContent(
    cardList: List<Card>,
    onEditCard: (Card) -> Unit,
    onDeleteCard: (Card) -> Unit,
    onAddBtn: () -> Unit,
    updateQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val query by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier,
    ) {
        Column {
            SearchBarScreen(
                query = query,
                onQueryChange = { q ->
                    updateQuery(q)
                },
                onSearch = {},
                onClearQuery = { updateQuery("") },
            )
            CardsList(
                cards = cardList,
                onMenuItemClick = { card, menuItem ->
                    when (menuItem) {
                        CardMenuAction.EDIT -> onEditCard(card)
                        CardMenuAction.DELETE -> onDeleteCard(card)
                        CardMenuAction.CANCEL -> Unit
                    }
                },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .background(color = MaterialTheme.colorScheme.surface),
        ) {
            AddCardChip(
                text = R.string.feature_savedcards_add_cards,
                btnText = R.string.feature_savedcards_add_cards,
                onAddBtn = onAddBtn,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        query = query,
        colors = SearchBarDefaults.colors(MaterialTheme.colorScheme.primaryContainer),
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = false,
        onActiveChange = { },
        placeholder = {
            Text(
                text = stringResource(R.string.feature_savedcards_search),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = MifosIcons.Search,
                contentDescription = stringResource(R.string.feature_savedcards_search),
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onClearQuery,
            ) {
                Icon(
                    imageVector = MifosIcons.Close,
                    contentDescription = stringResource(R.string.feature_savedcards_close),
                )
            }
        },
    ) {}
}

@Composable
private fun CardsList(
    cards: List<Card>,
    onMenuItemClick: (Card, CardMenuAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        items(cards) { card ->
            CardItem(
                card = card,
                onMenuItemClick = onMenuItemClick,
            )
        }
    }
}

@Composable
private fun CardItem(
    card: Card,
    onMenuItemClick: (Card, CardMenuAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .clickable { expanded = true }
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row {
                Column {
                    Row {
                        Text(
                            text = card.firstName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = card.lastName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${stringResource(R.string.feature_savedcards_card_number)} ${card.cardNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = card.expiryDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Spacer(modifier = Modifier.height(38.dp))
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.feature_savedcards_edit_card)) },
                        onClick = {
                            onMenuItemClick(card, CardMenuAction.EDIT)
                            expanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.feature_savedcards_delete_card)) },
                        onClick = {
                            onMenuItemClick(card, CardMenuAction.DELETE)
                            expanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.feature_savedcards_cancel)) },
                        onClick = {
                            onMenuItemClick(card, CardMenuAction.CANCEL)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NoCardAddCardsScreen(
    onAddBtn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.feature_savedcards_add_cards),
                color = MaterialTheme.colorScheme.onSurface,
            )
            AddCardChip(
                text = R.string.feature_savedcards_add_cards,
                btnText = R.string.feature_savedcards_add_cards,
                onAddBtn = onAddBtn,
                modifier = Modifier,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardsScreenWithSampleDataPreview() {
    MifosTheme {
        CardsScreen(
            cardState = CardsUiState.CreditCardForm(sampleCards),
            cardListUiState = CardsUiState.CreditCardForm(sampleCards),
            onEditCard = {},
            onDeleteCard = {},
            updateQuery = {},
            onAddBtn = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardsScreenEmptyPreview() {
    MifosTheme {
        CardsScreen(
            cardState = CardsUiState.Empty,
            cardListUiState = CardsUiState.CreditCardForm(sampleCards),
            onEditCard = {},
            onDeleteCard = {},
            updateQuery = {},
            onAddBtn = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardsScreenErrorPreview() {
    MifosTheme {
        CardsScreen(
            cardState = CardsUiState.Error,
            cardListUiState = CardsUiState.CreditCardForm(sampleCards),
            onEditCard = {},
            onDeleteCard = {},
            updateQuery = {},
            onAddBtn = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardsScreenLoadingPreview() {
    MifosTheme {
        CardsScreen(
            cardState = CardsUiState.Loading,
            cardListUiState = CardsUiState.CreditCardForm(sampleCards),
            onEditCard = {},
            onDeleteCard = {},
            updateQuery = {},
            onAddBtn = {},
        )
    }
}

val sampleCards = List(7) { index ->
    Card(
        cardNumber = "**** **** **** ${index + 1000}",
        cvv = "${index + 100}",
        expiryDate = "$index /0$index/202$index",
        firstName = "ABC",
        lastName = " XYZ",
        id = index,
    )
}
