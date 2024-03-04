package org.mifos.mobilewallet.mifospay.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.mifospay.history.ui.HistoryScreen
import org.mifos.mobilewallet.mifospay.invoice.ui.InvoiceScreen
import org.mifos.mobilewallet.mifospay.payments.ui.RequestScreen
import org.mifos.mobilewallet.mifospay.payments.ui.SendScreen
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.SIScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaymentsScreen(showQr: () -> Unit, searchContact: () -> Unit, scanQr: () -> Unit) {

    val pagerState = rememberPagerState(
        pageCount = { PaymentsScreenContents.entries.size }
    )
    val scope = rememberCoroutineScope()
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            containerColor = Color.White,
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 8.dp,
            indicator = {},
            divider = {},
        ) {
            PaymentsScreenContents.entries.forEachIndexed { index, currentTab ->
                Tab(
                    text = { Text(currentTab.name) },
                    selected = selectedTabIndex == index,
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(currentTab.ordinal)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> SendScreen({ scanQr.invoke() }, { searchContact.invoke() }, {})
                1 -> RequestScreen(showQr = { showQr.invoke() })
                2 -> HistoryScreen()
                3 -> SIScreen()
                4 -> InvoiceScreen()
                else -> Text("Page $page")
            }
        }
    }
}

enum class PaymentsScreenContents {
    SEND,
    REQUEST,
    HISTORY,
    SI,
    INVOICES
}

@Preview(showBackground = true)
@Composable
fun PaymentsScreenPreview() {
    PaymentsScreen({}, {},{})
}
