package org.mifos.mobilewallet.mifospay.savedcards.ui


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifos.mobilewallet.model.entity.savedcards.Card
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosOverlayLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.savedcards.presenter.CardsScreenViewModel
import org.mifos.mobilewallet.mifospay.savedcards.presenter.CardsUiState

@Composable
fun CardsScreen(
    viewModel: CardsScreenViewModel = hiltViewModel(),
    onAddBtn: () -> Unit
) {
    val context = LocalContext.current
    val cardState by viewModel.cardState.collectAsState()

    CardsScreenContent(
        context = context,
        cardState = cardState,
        onAddBtn = onAddBtn
    )
}

@Composable
fun CardsScreenContent(
    context: Context,
    cardState: CardsUiState,
    onAddBtn:() -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(cardState){
            CardsUiState.Loading->{
                MifosOverlayLoadingWheel(contentDesc = stringResource(R.string.loading))
            }
            is CardsUiState.Empty->{
                EmptyCardsScreen(onAddBtn)
            }
            is CardsUiState.Error -> {
                PlaceholderScreen()
            }
            is CardsUiState.CreditCardForm -> {
                WorkingCardsScreen(context,cardState,onAddBtn)
            }
            else -> {}
        }
    }
}



@Composable
fun EmptyCardsScreen(onAddBtn: () -> Unit){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.add_cards))
            addChip(onAddBtn)
        }
    }
}



@Composable
fun WorkingCardsScreen(
    context:Context,
    cardState: CardsUiState.CreditCardForm,
    onAddBtn: () -> Unit
) {
    val context = context
    val editMessage = stringResource(R.string.add_cards)
    var query by remember { mutableStateOf("") }
    var filteredCards by rememberSaveable {
        mutableStateOf(emptyList<Card>())
    }
    var active by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(362.dp)
            .height(500.dp)
            .padding(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchScreen(
                    query = query,
                    onQueryChange = { q ->
                        val data = filteredCards.filter {
                            it.cardNumber.lowercase().contains(q.lowercase())
                            it.firstName.lowercase().contains(q.lowercase())
                            it.lastName.lowercase().contains(q.lowercase())
                            it.cvv.lowercase().contains(q.lowercase())
                            it.expiryDate.lowercase().contains(q.lowercase())
                        }
                        filteredCards = data
                        query = q
                    },
                    onSearch = {},
                    onActiveChange = {},
                    onClearQuery = { if (query.isNotEmpty()) query = "" else active = false }
                )
            }

            if (filteredCards.isEmpty() || query.isEmpty()) {
                filteredCards = cardState.cards.filterNotNull()
            } else {
                filteredCards.map { it.cardNumber.lowercase() }.contains(query.lowercase())
            }

            CardsList(
                cards = filteredCards,
                onMenuItemClick = { menuItem ->
                    when (menuItem) {
                        CardMenuAction.EDIT.toString() -> {
                            editCardAction(context,editMessage)
                        }
                        CardMenuAction.DELETE.toString() -> {
                            // Todo: Handle delete card action
                        }
                        CardMenuAction.CANCEL.toString() -> {
                            // No action needed
                        }
                    }
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(9.dp))
    addChip(onAddBtn)
}

enum class CardMenuAction {
    EDIT, DELETE, CANCEL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit
) {
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 0.dp),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = false,
        onActiveChange = onActiveChange,
        placeholder = {
            Text(text = stringResource(R.string.search))
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(R.string.search))
        },
        trailingIcon = {
            IconButton(
                onClick = onClearQuery
            ) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = stringResource(R.string.close))
            }
        }
    ) {}
}

@Composable
fun CardsList(
    cards: List<Card>,
    onMenuItemClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(cards) { card ->
            CardItem(card = card) { menuItem ->
                onMenuItemClick(menuItem)
            }
        }
    }
}

@Composable
fun addChip(
    onAddBtn: () -> Unit
){
    AssistChip(
        onClick = { onAddBtn.invoke() },
        label = { Text(
            stringResource(R.string.add_cards),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) },
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

fun editCardAction(context: Context, editMessage: String){
    Toast.makeText(context, editMessage, Toast.LENGTH_SHORT).show()
}


@Composable
fun CardItem(card: Card?, onMenuItemClick: (String) -> Unit) {
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
                        if (card != null) {
                            Text(text = card.firstName, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (card != null) {
                            Text(text = card.lastName, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    if (card != null) {
                        Text(text = "${stringResource(R.string.card_number)} ${card.cardNumber}", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (card != null) {
                        Text(text = card.expiryDate, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.height(38.dp))
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    DropdownMenuItem(onClick = {
                        onMenuItemClick(CardMenuAction.EDIT.toString())
                        expanded = false
                    }) {
                        Text(stringResource(R.string.edit_card))
                    }
                    DropdownMenuItem(onClick = {
                        onMenuItemClick(CardMenuAction.DELETE.toString())
                        expanded = false
                    }) {
                        Text(stringResource(R.string.delete_card))
                    }
                    DropdownMenuItem(onClick = {
                        onMenuItemClick(CardMenuAction.CANCEL.toString())
                        expanded = false
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_error_state),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .scale(1.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.error_oops),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.unexpected_error_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CardsScreenPreview() {
    MifosTheme {
        CardsScreen(
            viewModel = hiltViewModel<CardsScreenViewModel>(),
            onAddBtn = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardsScreenPreviewWithSampleData() {
    val context = LocalContext.current
    MifosTheme {
        WorkingCardsScreen(
            context = context,
            cardState = CardsUiState.CreditCardForm(sampleCards),
            onAddBtn = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    PlaceholderScreen()
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
