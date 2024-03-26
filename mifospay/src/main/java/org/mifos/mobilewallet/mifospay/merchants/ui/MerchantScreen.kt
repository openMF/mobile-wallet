package org.mifos.mobilewallet.mifospay.merchants.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.designsystem.component.MfLoadingWheel
import org.mifos.mobilewallet.mifospay.merchants.presenter.MerchantUiState
import org.mifos.mobilewallet.mifospay.merchants.presenter.MerchantViewModel
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.ui.EmptyContentScreen

@Composable
fun MerchantScreen(
    viewModel: MerchantViewModel = hiltViewModel()
) {
    val merchantUiState by viewModel.merchantUiState.collectAsStateWithLifecycle()
    val merchantsListUiState by viewModel.merchantsListUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = viewModel::refreshMerchantsList
        ){
            MerchantScreen(
                merchantUiState = merchantUiState,
                merchantListUiState = merchantsListUiState,
                updateQuery = { viewModel.updateSearchQuery(it) }
            )
        }
}

@Composable
fun MerchantScreen(
    merchantUiState: MerchantUiState,
    merchantListUiState: MerchantUiState,
    updateQuery: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center,
    ) {
        when (merchantUiState) {
            MerchantUiState.Empty -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.empty_no_merchants_title),
                    subTitle = stringResource(id = R.string.empty_no_merchants_subtitle),
                    iconTint = Color.Black,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            is MerchantUiState.Error -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.error_oops),
                    subTitle = stringResource(id = R.string.unexpected_error_subtitle),
                    iconTint = Color.Black,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            MerchantUiState.Loading -> {
                MfLoadingWheel(
                    contentDesc = stringResource(R.string.loading),
                    backgroundColor = Color.White
                )
            }

            is MerchantUiState.ShowMerchants -> {
                MerchantScreenContent(
                    merchantList = (merchantListUiState as MerchantUiState.ShowMerchants).merchants,
                    updateQuery = updateQuery
                )
            }
        }
    }
}

@Composable
fun MerchantScreenContent(
    merchantList: List<SavingsWithAssociations>,
    updateQuery: (String) -> Unit
) {
    val query by rememberSaveable { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            SearchBarScreen(
                query = query,
                onQueryChange = { q ->
                    updateQuery(q)
                },
                onSearch = {},
                onClearQuery = { updateQuery("") }
            )
            MerchantList(merchantList = merchantList)
        }
    }
}

@Composable
fun MerchantList(
    merchantList: List<SavingsWithAssociations>
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(merchantList.size) { index ->
            MerchantsItem(savingsWithAssociations = merchantList[index],
                onMerchantClicked = {
                    val intent = Intent(context, MerchantTransferActivity::class.java)
                    intent.putExtra(Constants.MERCHANT_NAME, merchantList[index].clientName)
                    intent.putExtra(Constants.MERCHANT_VPA, merchantList[index].externalId)
                    intent.putExtra(Constants.MERCHANT_ACCOUNT_NO, merchantList[index].accountNo)
                    context.startActivity(intent)
                }
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

@Preview(showBackground = true)
@Composable
private fun MerchantLoadingPreview() {
    MifosTheme {
        MerchantScreen(merchantUiState = MerchantUiState.Loading,
            merchantListUiState = MerchantUiState.ShowMerchants(sampleMerchantList),
            updateQuery = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MerchantListPreview() {
    MifosTheme {
        MerchantScreen(
            merchantUiState = MerchantUiState.ShowMerchants(sampleMerchantList),
            merchantListUiState = MerchantUiState.ShowMerchants(sampleMerchantList),
            updateQuery = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MerchantErrorPreview() {
    MifosTheme {
        MerchantScreen(
            merchantUiState = MerchantUiState.Error("Error Screen"),
            merchantListUiState = MerchantUiState.ShowMerchants(sampleMerchantList),
            updateQuery = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MerchantEmptyPreview() {
    MifosTheme {
        MerchantScreen(
            merchantUiState = MerchantUiState.Empty,
            merchantListUiState = MerchantUiState.ShowMerchants(sampleMerchantList),
            updateQuery = {}
        )
    }
}


val sampleMerchantList = List(10) {
    SavingsWithAssociations(
        id = 1L,
        accountNo = "123456789",
        depositType = null,
        externalId = "EXT987654",
        clientId = 101,
        clientName = "Alice Bob",
        savingsProductId = 2001,
        savingsProductName = "Premium Savings Account",
        fieldOfficerId = 501,
        status = null,
        timeline = null,
        currency = null,
        nominalAnnualInterestRate = 3.5,
        minRequiredOpeningBalance = 500.0,
        lockinPeriodFrequency = 12.0,
        withdrawalFeeForTransfers = true,
        allowOverdraft = false,
        enforceMinRequiredBalance = false,
        withHoldTax = true,
        lastActiveTransactionDate = listOf(2024, 3, 24),
        dormancyTrackingActive = true,
        summary = null,
        transactions = listOf()
    )
}