/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.bank.accounts.link

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.Bank
import com.mifospay.core.model.domain.BankType
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosTopAppBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.DevicePreviews
import org.mifospay.feature.bank.accounts.R
import org.mifospay.feature.bank.accounts.choose.sim.ChooseSimDialogSheet

@Composable
internal fun LinkBankAccountRoute(
    viewModel: LinkBankAccountViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val bankUiState by viewModel.bankListUiState.collectAsStateWithLifecycle()
    var showSimBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showOverlyProgressBar by rememberSaveable { mutableStateOf(false) }

    if (showSimBottomSheet) {
        ChooseSimDialogSheet(
            onSimSelected = { selectedSim ->
                showSimBottomSheet = false
                if (selectedSim != -1) {
                    showOverlyProgressBar = true
                    viewModel.fetchBankAccountDetails {
                        showOverlyProgressBar = false
                        onBackClick()
                    }
                }
            },
        )
    }

    LinkBankAccountScreen(
        bankUiState = bankUiState,
        showOverlyProgressBar = showOverlyProgressBar,
        onBankSearch = { query ->
            viewModel.updateSearchQuery(query)
        },
        onBankSelected = {
            viewModel.updateSelectedBank(it)
            showSimBottomSheet = true
        },
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LinkBankAccountScreen(
    bankUiState: BankUiState,
    showOverlyProgressBar: Boolean,
    onBankSearch: (String) -> Unit,
    onBankSelected: (Bank) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface),
        topBar = {
            MifosTopAppBar(
                titleRes = R.string.feature_accounts_link_bank_account,
                navigationIcon = MifosIcons.Back,
                navigationIconContentDescription = "Back icon",
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                onNavigationClick = onBackClick,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues),
        ) {
            when (bankUiState) {
                is BankUiState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(R.string.feature_accounts_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is BankUiState.Success -> {
                    BankListScreenContent(
                        banks = bankUiState.banks,
                        onBankSearch = onBankSearch,
                        onBankSelected = onBankSelected,
                    )
                }
            }

            if (showOverlyProgressBar) {
                MfOverlayLoadingWheel()
            }
        }
    }
}

@Composable
private fun BankListScreenContent(
    banks: List<Bank>,
    onBankSearch: (String) -> Unit,
    onBankSelected: (Bank) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState()),
    ) {
        MifosOutlinedTextField(
            label = R.string.feature_accounts_search,
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onBankSearch(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            trailingIcon = {
                Icon(imageVector = MifosIcons.Search, contentDescription = null)
            },
        )

        if (searchQuery.isBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.feature_accounts_popular_banks),
                style = TextStyle(
                    MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.padding(start = 16.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            PopularBankGridBody(
                banks = banks.filter { it.bankType == BankType.POPULAR },
                onBankSelected = onBankSelected,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.feature_accounts_other_banks),
                style = TextStyle(
                    MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.padding(start = 16.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        BankListBody(
            banks = if (searchQuery.isBlank()) {
                banks.filter { it.bankType == BankType.OTHER }
            } else {
                banks
            },
            onBankSelected = onBankSelected,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PopularBankGridBody(
    banks: List<Bank>,
    onBankSelected: (Bank) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosCard(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        elevation = 2.dp,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
    ) {
        FlowRow(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 3,
        ) {
            banks.forEach {
                PopularBankItemBody(
                    modifier = Modifier.weight(1f),
                    bank = it,
                    onBankSelected = onBankSelected,
                )
            }
        }
    }
}

@Composable
private fun PopularBankItemBody(
    bank: Bank,
    onBankSelected: (Bank) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onBankSelected(bank)
            },
    ) {
        Image(
            modifier = Modifier
                .size(58.dp)
                .padding(bottom = 4.dp, top = 16.dp),
            painter = painterResource(id = bank.image),
            contentDescription = bank.name,
        )
        Text(
            text = bank.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BankListBody(
    banks: List<Bank>,
    onBankSelected: (Bank) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowColumn(modifier) {
        banks.forEach { bank ->
            BankListItemBody(bank = bank, onBankSelected = onBankSelected)
        }
    }
}

@Composable
private fun BankListItemBody(
    bank: Bank,
    onBankSelected: (Bank) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable { onBankSelected(bank) },
    ) {
        HorizontalDivider()
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
        ) {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = bank.image),
                contentDescription = bank.name,
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                text = bank.name,
                style = TextStyle(fontSize = 14.sp),
            )
        }
    }
}

@DevicePreviews
@Composable
private fun LinkBankAccountScreenPreview(
    @PreviewParameter(LinkBankUiStatePreviewParameterProvider::class)
    bankUiState: BankUiState,
) {
    MifosTheme {
        LinkBankAccountScreen(
            bankUiState = bankUiState,
            showOverlyProgressBar = false,
            onBankSelected = { },
            onBankSearch = { },
            onBackClick = { },
        )
    }
}
