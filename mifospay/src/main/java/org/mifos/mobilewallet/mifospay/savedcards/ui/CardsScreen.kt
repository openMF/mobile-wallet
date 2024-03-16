package org.mifos.mobilewallet.mifospay.savedcards.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.mobilewallet.model.entity.savedcards.Card
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MfLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.savedcards.presenter.CardsScreenViewModel
import org.mifos.mobilewallet.mifospay.savedcards.presenter.CardsUiState
import org.mifos.mobilewallet.mifospay.ui.EmptyContentScreen

enum class CardMenuAction {
    EDIT, DELETE, CANCEL
}

@Composable
fun CardsScreen(
    viewModel: CardsScreenViewModel = hiltViewModel(),
    onEditCard: (Card) -> Unit,
    onAddBtn: () -> Unit
) {
    val cardState by viewModel.cardState.collectAsStateWithLifecycle()
    val cardListUiState by viewModel.cardListUiState.collectAsStateWithLifecycle()
    CardsScreen(
        cardState = cardState,
        cardListUiState = cardListUiState,
        onEditCard = onEditCard,
        onDeleteCard = {
           // TODO implement Delete card by implementing a delete confirm dialog and call
           // TODO viewModel.deleteCard
        },
        onAddBtn = onAddBtn,
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
                onAddBtn = onAddBtn
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        items(cards) { card ->
            CardItem(card = card) { clickedCard, menuItem ->
                onMenuItemClick(clickedCard, menuItem)
            }
        }
    }
}

@Composable
fun AddCardChip(
    modifier: Modifier,
    onAddBtn: () -> Unit
) {
    AssistChip(
        modifier = modifier,
        onClick = { onAddBtn.invoke() },
        label = {
            Text(
                stringResource(R.string.add_cards),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_cards),
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
    )
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
                onAddBtn = onAddBtn
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
