package org.mifos.mobilewallet.mifospay.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.mifos.mobilewallet.mifospay.history.ui.HistoryScreen
import org.mifos.mobilewallet.mifospay.invoice.ui.InvoiceScreen
import org.mifos.mobilewallet.mifospay.payments.ui.RequestScreen
import org.mifos.mobilewallet.mifospay.payments.ui.SendScreen
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.StandingInstructionsScreen
import org.mifos.mobilewallet.mifospay.ui.MifosScrollableTabRow
import org.mifos.mobilewallet.mifospay.ui.utility.TabContent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaymentsScreen(
    showQr: () -> Unit,
    onNewSI: () -> Unit
) {

    val pagerState = rememberPagerState(
        pageCount = { PaymentsScreenContents.entries.size }
    )

    val tabContents = listOf(
        TabContent(PaymentsScreenContents.SEND.name) {
            SendScreen(onSubmit = {})
        },
        TabContent(PaymentsScreenContents.REQUEST.name) {
            RequestScreen(showQr = { showQr.invoke() })
        },
        TabContent(PaymentsScreenContents.HISTORY.name) {
            HistoryScreen()
        },
        TabContent(PaymentsScreenContents.SI.name) {
            StandingInstructionsScreen(onNewSI = { onNewSI.invoke() })
        },
        TabContent(PaymentsScreenContents.INVOICES.name) {
            InvoiceScreen()
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        MifosScrollableTabRow(tabContents = tabContents, pagerState = pagerState)
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
    PaymentsScreen({}, {})
}
