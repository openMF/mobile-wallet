package org.mifospay.feature.finance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.mifospay.bank.ui.AccountsScreen
import org.mifospay.core.ui.MifosScrollableTabRow
import org.mifospay.core.ui.utility.TabContent
import org.mifospay.feature.kyc.KYCScreen
import org.mifospay.feature.savedcards.CardsScreen
import org.mifospay.merchants.ui.MerchantScreen

@Composable
fun FinanceRoute(
    onAddBtn: () -> Unit,
    onLevel1Clicked: () -> Unit,
    onLevel2Clicked: () -> Unit,
    onLevel3Clicked: () -> Unit
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
            KYCScreen(
                onLevel1Clicked = onLevel1Clicked,
                onLevel2Clicked = onLevel2Clicked,
                onLevel3Clicked = onLevel3Clicked
            )
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
    FinanceRoute(
        onAddBtn = {},
        onLevel1Clicked = {},
        onLevel2Clicked = {},
        onLevel3Clicked = {}
    )
}