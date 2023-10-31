package org.mifos.mobilewallet.mifospay.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextAlign
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.model.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.theme.border
import org.mifos.mobilewallet.mifospay.theme.lightGrey
import org.mifos.mobilewallet.mifospay.theme.styleMedium16sp

@Composable
fun HomeScreen(
    onRequest: () -> Unit,
    onPay: () -> Unit
) {
    MifosTheme {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .padding(start = 32.dp, end = 32.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 20.dp, bottom = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 36.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Wallet Balance (NGN)",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                            color = lightGrey
                        )
                    )
                    Text(
                        text = "₦ 3000",
                        style = TextStyle(
                            fontSize = 42.sp,
                            fontWeight = FontWeight(600),
                            color = Color.White
                        )
                    )
                    Text(
                        text = "=$3.90 (USD)",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight(500),
                            color = lightGrey
                        )
                    )
                }
            }
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
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = "Recent Transactions",
                style = styleMedium16sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                val transactions = arrayListOf<Transaction>().apply {
                    add(Transaction())
                    add(Transaction())
                    add(Transaction())
                    add(Transaction())
                }
                items(transactions) { transaction ->
                    ItemTransaction(transaction = transaction)
                }
            }
        }
    }
}

@Composable
fun PayCard(modifier: Modifier, title: String, icon: Int, onClickCard: () -> Unit) {
    Card(
        modifier = modifier
            .height(144.dp),
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

@Composable
fun ItemTransaction(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Image(
            modifier = Modifier.size(20.dp).padding(top = 2.dp),
            painter = painterResource(id = when(transaction.transactionType) {
                TransactionType.DEBIT -> R.drawable.money_out
                TransactionType.CREDIT -> R.drawable.money_in
                else -> R.drawable.money_in
            }),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black)
        )
        Column(
            modifier = Modifier.padding(start = 32.dp).weight(.3f)
        ) {
            Text(
                text = "Payment Title",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),

                    )
            )
            Text(
                text = "16th September",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0x66000000))
            )
        }
        Text(
            modifier = Modifier.weight(.3f),
            text = "+ ₦ 150 ",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF008135),
                textAlign = TextAlign.End,
            )
        )
    }
}

@Preview
@Composable
fun ItemTransactionPreview() {
    ItemTransaction(Transaction())
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
fun HomeScreenPreview() {
    HomeScreen({}, {})
}

@Preview
@Composable
fun PayCardPreview() {
    PayCard(Modifier.width(150.dp), "Request", R.drawable.ic_arrow_back_black_24dp) { }
}