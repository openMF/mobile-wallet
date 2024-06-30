package org.mifospay.home.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.rememberPagerState
import org.mifospay.bank.ui.AccountsScreen
import org.mifospay.core.ui.MifosScrollableTabRow
import org.mifospay.core.ui.utility.TabContent
import org.mifospay.feature.savedcards.CardsScreen
import org.mifospay.kyc.ui.KYCScreen
import org.mifospay.merchants.ui.MerchantScreen

@Composable
fun FinanceRoute(
    onAddBtn: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0)

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