package org.mifospay.home.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.mifospay.bank.ui.AccountsScreen
import org.mifospay.core.ui.MifosScrollableTabRow
import org.mifospay.core.ui.utility.TabContent
import org.mifospay.kyc.ui.KYCScreen
import org.mifospay.merchants.ui.MerchantScreen
import org.mifospay.savedcards.ui.CardsScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinanceRoute(
    onAddBtn: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { FinanceScreenContents.entries.size }
    )

    val tabContents = listOf(
        TabContent(FinanceScreenContents.ACCOUNTS.name) {
            AccountsScreen()
        },
        TabContent(FinanceScreenContents.CARDS.name) {
            CardsScreen(onEditCard = {})
        },
        TabContent(FinanceScreenContents.MERCHANTS.name) {
            MerchantScreen()
        },
        TabContent(FinanceScreenContents.KYC.name) {
            KYCScreen()
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        MifosScrollableTabRow(tabContents = tabContents, pagerState = pagerState)
    }
}

enum class FinanceScreenContents {
    ACCOUNTS,
    CARDS,
    MERCHANTS,
    KYC
}

@Preview(showBackground = true)
@Composable
private fun FinanceScreenPreview() {
    FinanceRoute({})
}