package org.mifospay.home.screens

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.mifospay.bank.ui.AccountsScreen
import org.mifospay.bank.link_bank.LinkBankAccountActivity
import org.mifospay.core.ui.MifosScrollableTabRow
import org.mifospay.core.ui.utility.TabContent
import org.mifospay.kyc.ui.KYCDescriptionScreen
import org.mifospay.merchants.ui.MerchantScreen
import org.mifospay.savedcards.ui.CardsScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinanceRoute(
    onAddBtn: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        pageCount = { FinanceScreenContents.entries.size }
    )

    val tabContents = listOf(
        TabContent(FinanceScreenContents.ACCOUNTS.name) {
            AccountsScreen(onAddAccount = {
                val intent = Intent(context, LinkBankAccountActivity::class.java)
                context.startActivity(intent)
            })
        },
        TabContent(FinanceScreenContents.CARDS.name) {
            CardsScreen(onEditCard = {}, onAddBtn = { onAddBtn.invoke() })
        },
        TabContent(FinanceScreenContents.MERCHANTS.name) {
            MerchantScreen()
        },
        TabContent(FinanceScreenContents.KYC.name) {
            KYCDescriptionScreen(
                onLevel1Clicked = {},
                onLevel2Clicked = {},
                onLevel3Clicked = {}
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
    FinanceRoute({})
}