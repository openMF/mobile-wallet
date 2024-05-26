package org.mifospay.savedcards.ui

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utility.AddCardChip
import org.mifospay.savedcards.presenter.CardsScreenViewModel
import org.mifospay.savedcards.presenter.CardsUiState

enum class CardMenuAction {
    EDIT, DELETE, CANCEL
}

@Composable
fun CardsScreen(
    viewModel: CardsScreenViewModel = hiltViewModel(),
    onEditCard: (Card) -> Unit
) {
    val cardState by viewModel.cardState.collectAsStateWithLifecycle()
    val cardListUiState by viewModel.cardListUiState.collectAsStateWithLifecycle()

    var showCardBottomSheet by rememberSaveable { mutableStateOf(false) }
    val showConfirmDeleteDialog = rememberSaveable { mutableStateOf(false) }

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
            }
        )
    }

    MifosDialogBox(
        showDialogState = showConfirmDeleteDialog,
        onDismiss = { showConfirmDeleteDialog.value = false },
        title = R.string.delete_card,
        confirmButtonText = R.string.yes,
        onConfirm = {
            deleteCardID?.let { viewModel.deleteCard(it) }
            showConfirmDeleteDialog.value = false
        },
        dismissButtonText = R.string.no,
        message = R.string.confirm_delete_card
    )

    CardsScreen(
        cardState = cardState,
        cardListUiState = cardListUiState,
        onEditCard = onEditCard,
        onDeleteCard = {
            showConfirmDeleteDialog.value = true
            deleteCardID = it.id
        },
        onAddBtn = { showCardBottomSheet = true },
        updateQuery = {
            viewModel.updateSearchQuery(it)
        }
    )
}

@Composable
fun CardsScreen(
    cardState: CardsUiState,
    cardListUiState: CardsUiState,
    onEditCard: (Card) -> Unit,
    onDeleteCard: (Card) -> Unit,
    onAddBtn: () -> Unit,
    updateQuery: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (cardState) {
            CardsUiState.Loading -> {
                MfLoadingWheel(
                    contentDesc = stringResource(R.string.loading),
                    backgroundColor = Color.White
                )
            }

            is CardsUiState.Empty -> {
                NoCardAddCardsScreen(onAddBtn)
            }

            is CardsUiState.Error -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.error_oops),
                    subTitle = stringResource(id = R.string.unexpected_error_subtitle),
                    iconTint = Color.Black,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            is CardsUiState.CreditCardForm -> {
                CardsScreenContent(
                    cardList = (cardListUiState as CardsUiState.CreditCardForm).cards,
                    onAddBtn = onAddBtn,
                    onDeleteCard = onDeleteCard,
                    onEditCard = onEditCard,
                    updateQuery = updateQuery
                )
            }

            is CardsUiState.Success -> {
                Toast.makeText(LocalContext.current, cardState.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun CardsScreenContent(
    cardList: List<Card>,
    onEditCard: (Card) -> Unit,
    onDeleteCard: (Card) -> Unit,
    onAddBtn: () -> Unit,
    updateQuery: (String) -> Unit
) {
    val query by rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier
    ) {
        Column {
            SearchBarScreen(
                query = query,
                onQueryChange = { q ->
                    updateQuery(q)
                },
                onSearch = {},
                onClearQuery = { updateQuery("") }
            )
            CardsList(
                cards = cardList,
                onMenuItemClick = { card, menuItem ->
                    when (menuItem) {
                        CardMenuAction.EDIT -> onEditCard(card)
                        CardMenuAction.DELETE -> onDeleteCard(card)
                        CardMenuAction.CANCEL -> Unit
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .background(color = Color.White)
        ) {
            AddCardChip(
                modifier = Modifier.align(Alignment.Center),
                onAddBtn = onAddBtn,
                text = R.string.add_cards,
                btnText = R.string.add_cards
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = false,
        onActiveChange = { },
        placeholder = {
            Text(text = stringResource(R.string.search))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onClearQuery
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        }
    ) {}
}

@Composable
fun CardsList(
    cards: List<Card>,
    onMenuItemClick: (Card, CardMenuAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(cards) { card ->
            CardItem(card = card) { clickedCard, menuItem ->
                onMenuItemClick(clickedCard, menuItem)
            }
        }
    }
}

@Composable
fun CardItem(card: Card, onMenuItemClick: (Card, CardMenuAction) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable { expanded = true }
            .background(color = Color.White)
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                Column {
                    Row {
                        Text(text = card.firstName, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = card.lastName, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${stringResource(R.string.card_number)} ${card.cardNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = card.expiryDate, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(38.dp))
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.edit_card)) },
                        onClick = {
                            onMenuItemClick(card, CardMenuAction.EDIT)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete_card)) },
                        onClick = {
                            onMenuItemClick(card, CardMenuAction.DELETE)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.cancel)) },
                        onClick = {
                            onMenuItemClick(card, CardMenuAction.CANCEL)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NoCardAddCardsScreen(onAddBtn: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.add_cards))
            AddCardChip(
                modifier = Modifier,
                onAddBtn = onAddBtn,
                text = R.string.add_cards,
                btnText = R.string.add_cards
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
            onAddBtn = {}
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
            onAddBtn = {}
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
            onAddBtn = {}
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
            onAddBtn = {}
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
        id = index
    )
}
