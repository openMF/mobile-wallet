package org.mifos.mobilewallet.mifospay.savedcards.ui


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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifos.mobilewallet.model.entity.savedcards.Card
import org.mifos.mobilewallet.mifospay.MifosPayApp
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosOverlayLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.savedcards.presenter.CardsUiState
import org.mifos.mobilewallet.mifospay.utils.Constants
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    cardState: CardsUiState,
    onAddBtn:() -> Unit,
    onError:() ->Unit
){
    var query by remember { mutableStateOf("") }
    var filteredCards by rememberSaveable {
        mutableStateOf(emptyList<Card>())
    }
    var active by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(cardState){
            CardsUiState.LoadingState->{
                MifosOverlayLoadingWheel(contentDesc = "LoadingWheel")
            }
            is CardsUiState.EmptyUiState->{
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = Constants.ADD_CARD)
                        addChip(onAddBtn)
                    }
                }
            }
            is CardsUiState.ErrorUiState-> {
                onError.invoke()
            }
            is CardsUiState.WorkingUiState -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 0.dp),
                        query = query,
                        onQueryChange = { q ->
                            val data = filteredCards.filter {
                                it.cardNumber.lowercase().contains(q.lowercase())
                                it.firstName.lowercase().contains(q.lowercase())
                                it.lastName.lowercase().contains(q.lowercase())
                                it.cvv.lowercase().contains(q.lowercase())
                                it.expiryDate.lowercase().contains(q.lowercase())
                            }
                            filteredCards= data
                            query = q
                        },
                        onSearch = {},
                        active = false,
                        onActiveChange = {  },
                        placeholder = {
                            Text(text = Constants.SEARCH)
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = Constants.SEARCH)
                        },
                        trailingIcon = {
                                    IconButton(
                                        onClick = { if (query.isNotEmpty()) query = "" else active = false }
                                    ) {
                                        Icon(imageVector = Icons.Filled.Close, contentDescription = Constants.CLOSE)
                                    }
                        }
                    ) {}
                }

                Box(
                    modifier = Modifier
                        .width(362.dp)
                        .height(500.dp)
                        .padding(0.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if(filteredCards.isEmpty() || query.isEmpty()){
                            filteredCards = sampleCardies.toMutableList()
                        }else{
                            filteredCards.map { it.cardNumber.lowercase() }.contains(query.lowercase())
                        }
                        items(filteredCards) { card ->
                            CardItem(card = card) { menuItem ->
                                // Handle menu item click here
                                when (menuItem) {
                                    "edit_card" -> {
                                        editCardAction()
                                    }
                                    "delete_card" -> {
                                        //Todo:Handle delete card action
                                    }
                                    "cancel" -> {

                                    }
                                }
                            }
                        }

                    }
                }
                Spacer(modifier = Modifier.height(9.dp))
                addChip(onAddBtn)
            }
            else -> {}
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
            Constants.ADD_CARD,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) },
        leadingIcon = {
            Icon(
                Icons.Filled.Add,
                contentDescription = Constants.ADD_CARD,
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
    )
}

//Todo: Navigate to AddCardDialog after migration
fun editCardAction(){
    Toast.makeText(MifosPayApp.context, Constants.EDIT_CLICK, Toast.LENGTH_SHORT).show()
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
                        Text(text = "${Constants.CARD_NU} ${card.cardNumber}", style = MaterialTheme.typography.bodyMedium)
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
                        onMenuItemClick("edit_card")
                        expanded = false
                    }) {
                        Text(Constants.EDIT_C)
                    }
                    DropdownMenuItem(onClick = {
                        onMenuItemClick("delete_card")
                        expanded = false
                    }) {
                        Text(Constants.DELETE_C)
                    }
                    DropdownMenuItem(onClick = {
                        onMenuItemClick("cancel")
                        expanded = false
                    }) {
                        Text(Constants.CANCEL_C)
                    }
                }
            }
        }
    }
}


//Todo:Remove SampleCardies after successful retrivation of cards from repo
val sampleCardies = List(7) { index ->
    Card(cardNumber = "**** **** **** ${index + 1000}", cvv= "${index + 100}", expiryDate = "$index /0$index/202$index",
        firstName="ABC ",lastName=" XYZ",id = index)
}


@Preview(showBackground = true)
@Composable
fun CardsScreenPreview(){
    MifosTheme {
        CardsScreen(hiltViewModel(),{},{})
    }
}
