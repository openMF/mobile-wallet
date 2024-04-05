package org.mifos.mobilewallet.mifospay.home.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.mobilewallet.model.domain.Account
import com.mifos.mobilewallet.model.domain.Transaction
import com.mifos.mobilewallet.model.domain.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.common.compose.ChildLayout
import org.mifos.mobilewallet.mifospay.common.compose.LoadItemAfterSafeCast
import org.mifos.mobilewallet.mifospay.common.compose.VerticalScrollLayout
import org.mifos.mobilewallet.mifospay.home.HomeViewModel
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.theme.border
import org.mifos.mobilewallet.mifospay.theme.green
import org.mifos.mobilewallet.mifospay.theme.lightGrey
import org.mifos.mobilewallet.mifospay.theme.red
import org.mifos.mobilewallet.mifospay.theme.styleMedium16sp
import org.mifos.mobilewallet.mifospay.common.Utils
import org.mifos.mobilewallet.mifospay.common.Utils.getFormattedAccountBalance
import org.mifos.mobilewallet.mifospay.ui.TransactionItemScreen

enum class HomeScreenContents {
    HOME_CARD,
    PAY_CARD,
    TRANSACTIONS_HEADER,
    TRANSACTIONS
}

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onRequest: (String) -> Unit,
    onPay: () -> Unit
) {
    val homeUIState by homeViewModel
        .homeUIState
        .collectAsStateWithLifecycle()

    HomeScreen(
        homeUIState.account,
        homeUIState.transactions,
        onRequest = {
            onRequest.invoke(homeUIState.vpa ?: "")
        },
        onPay = onPay
    )
}

@Composable
fun HomeScreen(
    account: Account?,
    transactions: List<Transaction>,
    onRequest: () -> Unit,
    onPay: () -> Unit
) {
    MifosTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            VerticalScrollLayout(
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(start = 32.dp, end = 32.dp),
                ChildLayout(
                    contentType = HomeScreenContents.HOME_CARD.toString(),
                    content = {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(225.dp)
                                .padding(top = 20.dp, bottom = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(start = 36.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                val walletBalanceLabel =
                                    if (account != null) "(${account.currency?.displayLabel})" else ""
                                Text(
                                    text = "Wallet Balance $walletBalanceLabel",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.W400,
                                        color = lightGrey
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                val accountBalance =
                                    if (account != null) Utils.getFormattedAccountBalance(
                                        account.balance, account.currency?.code
                                    ) else "0"
                                Text(
                                    text = accountBalance,
                                    style = TextStyle(
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight(600),
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                val currencyEqual = if (account != null) {
                                    "${account.currency.code}1 ${account.currency?.displayLabel}"
                                } else ""
                                Text(
                                    text = currencyEqual,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight(500),
                                        color = lightGrey
                                    )
                                )
                            }
                        }
                    }
                ),
                ChildLayout(
                    contentType = HomeScreenContents.PAY_CARD.toString(),
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PayCard(
                                modifier = Modifier.weight(1f),
                                title = "Request",
                                icon = R.drawable.money_in
                            ) {
                                onRequest.invoke()
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            PayCard(
                                modifier = Modifier.weight(1f),
                                title = "Pay",
                                icon = R.drawable.money_out
                            ) {
                                onPay.invoke()
                            }
                        }
                    }
                ),
                ChildLayout(
                    contentType = HomeScreenContents.TRANSACTIONS_HEADER.toString(),
                    content = {
                        if (transactions.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(top = 32.dp),
                                text = "Recent Transactions",
                                style = styleMedium16sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                ),
                ChildLayout(
                    contentType = HomeScreenContents.TRANSACTIONS.toString(),
                    items = transactions,
                    content = { item ->
                        LoadItemAfterSafeCast<Transaction>(item) { safeTransaction ->
                            TransactionItemScreen(transaction = safeTransaction)
                        }
                    }
                )
            )
        }
    }
}

@Composable
fun PayCard(modifier: Modifier, title: String, icon: Int, onClickCard: () -> Unit) {
    Card(
        modifier = modifier
            .height(144.dp)
            .clickable { onClickCard.invoke() },
        border = BorderStroke(1.dp, border),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 20.dp, bottom = 20.dp, start = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black, shape = RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Text(text = title)
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun HomeScreenPreview() {
    HomeScreen(null, listOf(), {}, {})
}

@Preview
@Composable
fun PayCardPreview() {
    PayCard(Modifier.width(150.dp), "Request", R.drawable.ic_arrow_back_black_24dp) { }
}