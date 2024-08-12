/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.merchants.ui

import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.feature.merchants.MerchantUiState
import org.mifospay.feature.merchants.MerchantViewModel
import org.mifospay.feature.merchants.R
import org.mifospay.feature.merchants.navigation.navigateToMerchantTransferScreen

@Composable
fun MerchantScreen(
    modifier: Modifier = Modifier,
    viewModel: MerchantViewModel = hiltViewModel(),
) {
    val merchantUiState by viewModel.merchantUiState.collectAsStateWithLifecycle()
    val merchantsListUiState by viewModel.merchantsListUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    MerchantScreen(
        merchantUiState = merchantUiState,
        merchantListUiState = merchantsListUiState,
        isRefreshing = isRefreshing,
        updateQuery = viewModel::updateSearchQuery,
        onRefresh = viewModel::refresh,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@VisibleForTesting
internal fun MerchantScreen(
    merchantUiState: MerchantUiState,
    merchantListUiState: MerchantUiState,
    isRefreshing: Boolean,
    updateQuery: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)

    Box(
        modifier = modifier
            .pullRefresh(pullRefreshState),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (merchantUiState) {
                MerchantUiState.Empty -> {
                    EmptyContentScreen(
                        modifier = Modifier,
                        title = stringResource(id = R.string.feature_merchants_empty_no_merchants_title),
                        subTitle = stringResource(id = R.string.feature_merchants_empty_no_merchants_subtitle),
                        iconTint = MaterialTheme.colorScheme.primary,
                        iconImageVector = Icons.Rounded.Info,
                    )
                }

                is MerchantUiState.Error -> {
                    EmptyContentScreen(
                        modifier = Modifier,
                        title = stringResource(id = R.string.feature_merchants_error_oops),
                        subTitle = stringResource(id = R.string.feature_merchants_unexpected_error_subtitle),
                        iconTint = MaterialTheme.colorScheme.primary,
                        iconImageVector = Icons.Rounded.Info,
                    )
                }

                MerchantUiState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(R.string.feature_merchants_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is MerchantUiState.ShowMerchants -> {
                    MerchantScreenContent(
                        merchantList = (merchantListUiState as MerchantUiState.ShowMerchants).merchants,
                        updateQuery = updateQuery,
                    )
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun MerchantScreenContent(
    merchantList: List<SavingsWithAssociations>,
    updateQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val query by rememberSaveable { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            SearchBarScreen(
                query = query,
                onQueryChange = { q ->
                    updateQuery(q)
                },
                onSearch = {},
                onClearQuery = { updateQuery("") },
            )
            MerchantList(merchantList = merchantList)
        }
    }
}

@Composable
private fun MerchantList(
    merchantList: List<SavingsWithAssociations>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val navController = rememberNavController()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        items(merchantList.size) { index ->
            MerchantsItem(
                savingsWithAssociations = merchantList[index],
                onMerchantClicked = {
                    navController.navigateToMerchantTransferScreen(
                        merchantVPA = merchantList[index].externalId,
                        merchantName = merchantList[index].clientName,
                        merchantAccountNumber = merchantList[index].accountNo.toString(),
                    )
//                    val intent = Intent(context, MerchantTransferActivity::class.java)
//                    intent.putExtra(Constants.MERCHANT_NAME, merchantList[index].clientName)
//                    intent.putExtra(Constants.MERCHANT_VPA, merchantList[index].externalId)
//                    intent.putExtra(Constants.MERCHANT_ACCOUNT_NO, merchantList[index].accountNo)
//                    context.startActivity(intent)
                },
                onMerchantLongPressed = {
                    clipboardManager.setText(AnnotatedString(it ?: ""))
                    Toast.makeText(
                        context,
                        R.string.feature_merchants_vpa_copy_success,
                        Toast.LENGTH_LONG,
                    ).show()
                },
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
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = false,
        onActiveChange = { },
        placeholder = {
            Text(text = stringResource(R.string.feature_merchants_search))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.feature_merchants_search),
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onClearQuery,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.feature_merchants_close),
                )
            }
        },
    ) {}
}

@Preview(showBackground = true)
@Composable
private fun MerchantLoadingPreview() {
    MifosTheme {
        MerchantScreen(
            merchantUiState = MerchantUiState.Loading,
            merchantListUiState = MerchantUiState.ShowMerchants(sampleMerchantList),
            isRefreshing = false,
            updateQuery = {},
            onRefresh = {},
            modifier = Modifier,
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
            isRefreshing = false,
            updateQuery = {},
            onRefresh = {},
            modifier = Modifier,
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
            isRefreshing = true,
            updateQuery = {},
            onRefresh = {},
            modifier = Modifier,
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
            isRefreshing = false,
            updateQuery = {},
            onRefresh = {},
            modifier = Modifier,
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
        transactions = listOf(),
    )
}
