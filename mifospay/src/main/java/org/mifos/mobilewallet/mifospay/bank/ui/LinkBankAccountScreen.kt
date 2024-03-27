package org.mifos.mobilewallet.mifospay.bank.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.presenter.BankUiState
import org.mifos.mobilewallet.mifospay.bank.presenter.LinkBankAccountViewModel
import org.mifos.mobilewallet.mifospay.designsystem.component.MfOverlayLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosOutlinedTextField
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosTopBar
import org.mifos.mobilewallet.mifospay.designsystem.theme.greyBackgroundColor
import org.mifos.mobilewallet.mifospay.domain.model.Bank
import org.mifos.mobilewallet.mifospay.theme.MifosTheme


@Composable
fun LinkBankAccountScreen(
    activity: LinkBankAccountActivity,
    onBackPress: () -> Unit
) {

    val viewModel: LinkBankAccountViewModel = hiltViewModel()
    var showSimBottomSheet by rememberSaveable { mutableStateOf(false) }
    var bankSelected = ""
    val bankUiState by viewModel.bankState.collectAsStateWithLifecycle()
    val bankListUiState by viewModel.bankListUiState.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        MifosTopBar(
            topBarTitle = R.string.link_bank_account,
            backPress = onBackPress
        )
    }) {
        LinkBankAccountScreenContent(
            padding = it,
            bankUiState = bankUiState,
            bankListUiState = bankListUiState,
            updateSearchQuery = { search ->
                viewModel.updateSearchQuery(search)
            },
            onSelectBank = { bank ->
                bankSelected = bank.name
                showSimBottomSheet = true
            }
        )
        if (showSimBottomSheet) {
            ChooseSimDialogSheet { selectedSim ->
                if (selectedSim != 0) {
                    viewModel.fetchBankAccountDetails(bankName = bankSelected, activity = activity)
                }
                showSimBottomSheet = false
            }
        }
    }
}

@Composable
fun LinkBankAccountScreenContent(
    padding: PaddingValues,
    bankUiState: BankUiState,
    bankListUiState: BankUiState,
    updateSearchQuery: (String) -> Unit,
    onSelectBank: (Bank) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(greyBackgroundColor)
    ) {
        when (bankUiState) {
            BankUiState.Loading -> {
                LoadingContent()
            }

            is BankUiState.LoadedBankState -> {
                LoadedContent(
                    popularBanks = (bankListUiState as BankUiState.LoadedBankState).popularBanks,
                    allBanks = (bankListUiState as BankUiState.LoadedBankState).allBanks,
                    updateSearchQuery,
                    onSelectBank
                )
            }
        }
    }
}

@Composable
fun LoadingContent() {
    MfOverlayLoadingWheel(
        contentDesc = stringResource(R.string.loading),
    )
}

@Composable
fun LoadedContent(
    popularBanks: List<Bank>,
    allBanks: List<Bank>,
    updateSearchQuery: (String) -> Unit,
    onSelectBank: (Bank) -> Unit
) {
    val search = rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 55.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            MifosOutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                value = search.value,
                onValueChange = {
                    search.value = it
                    updateSearchQuery(it)
                },
                label = R.string.search,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search, contentDescription = null
                    )
                })
        }
        if (search.value.isBlank())
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Popular Banks",
                    style = TextStyle(Color.Black, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                BankGrid(banks = popularBanks, onSelectBank)
            }

        if (allBanks.isNotEmpty())
            Column {
                if (search.value.isBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Other Banks",
                        style = TextStyle(Color.Black, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                BankList(
                    otherBanks = allBanks,
                    onSelectBank
                )
            }
        else
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_bank_account),
                    style = MaterialTheme.typography.subtitle1
                )
            }
    }
}

@Composable
fun BankGrid(banks: List<Bank>, onSelectBank: (Bank) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.White)
            .shadow(elevation = 2.dp)
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(banks.size) { index ->
                BankGridItem(bank = banks[index], onSelectBank)
            }
        }
    }
}

@Composable
fun BankGridItem(bank: Bank, onSelectBank: (Bank) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .height(80.dp)
            .clickable {
                onSelectBank(bank)
            }
    ) {
        Image(
            painter = painterResource(id = bank.image),
            contentDescription = bank.name,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = bank.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BankList(otherBanks: List<Bank>, onSelectBank: (Bank) -> Unit) {
    Box(
        modifier = Modifier
            .background(Color.White)
    ) {
        FlowColumn(
        ) {
            otherBanks.forEachIndexed { index, bank ->
                BankListItem(bank = bank, onSelectBank)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun BankListItem(bank: Bank, onSelectBank: (Bank) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .fillMaxWidth()
            .clickable {
                onSelectBank(bank)
            }
    ) {
        Image(
            painter = painterResource(id = bank.image),
            contentDescription = bank.name,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = bank.name, style = TextStyle(fontSize = 14.sp))
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
private fun LinkBankAccountScreenPreview() {
    MifosTheme {
        LinkBankAccountScreen(LinkBankAccountActivity()) {}
    }
}

@Preview
@Composable
private fun BankGridItemPreview() {
    BankGridItem(bank = Bank("RBL Bank", R.drawable.logo_rbl, 0), onSelectBank = {})
}

@Preview
@Composable
private fun BankListItemPreview() {
    BankListItem(bank = Bank("RBL Bank", R.drawable.ic_bank, 0), onSelectBank = {})
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
private fun LoadingContentPreview() {
    LoadingContent()
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
private fun LoadedContentPreview() {

    val popularBanks = ArrayList<Bank>()
    popularBanks.add(Bank("RBL Bank", R.drawable.logo_rbl, 0))
    popularBanks.add(Bank("SBI Bank", R.drawable.logo_sbi, 0))
    popularBanks.add(Bank("PNB Bank", R.drawable.logo_pnb, 0))
    popularBanks.add(Bank("HDFC Bank", R.drawable.logo_hdfc, 0))
    popularBanks.add(Bank("ICICI Bank", R.drawable.logo_icici, 0))
    popularBanks.add(Bank("AXIS Bank", R.drawable.logo_axis, 0))

    val allBanks = ArrayList<Bank>()
    allBanks.add(Bank("HDFC Bank", R.drawable.ic_bank, 0))
    allBanks.add(Bank("ICICI Bank", R.drawable.ic_bank, 0))
    allBanks.add(Bank("AXIS Bank", R.drawable.ic_bank, 0))
    LoadedContent(
        popularBanks = popularBanks,
        allBanks = allBanks,
        updateSearchQuery = {},
        onSelectBank = {},
    )
}



