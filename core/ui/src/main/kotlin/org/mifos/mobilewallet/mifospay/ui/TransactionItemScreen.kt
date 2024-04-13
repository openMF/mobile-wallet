package org.mifos.mobilewallet.mifospay.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mifos.mobilewallet.model.domain.Transaction
import com.mifos.mobilewallet.model.domain.TransactionType
import org.mifos.mobilewallet.mifospay.common.Utils.getFormattedAccountBalance
import org.mifos.mobilewallet.mifospay.designsystem.theme.green
import org.mifos.mobilewallet.mifospay.designsystem.theme.red

@Composable
fun TransactionItemScreen(
    modifier: Modifier = Modifier,
    transaction: Transaction
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp),
            painter = painterResource(
                id = when (transaction.transactionType) {
                    TransactionType.DEBIT -> R.drawable.money_out
                    TransactionType.CREDIT -> R.drawable.money_in
                    else -> R.drawable.money_in
                }
            ),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black)
        )
        Column(
            modifier = Modifier
                .padding(start = 32.dp)
                .weight(.3f)
        ) {
            Text(
                text = transaction.transactionType.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),

                    )
            )
            Text(
                text = transaction.date.toString(),
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0x66000000)
                )
            )
        }
        val formattedAmount =
            getFormattedAccountBalance(transaction.amount, transaction.currency.code, 2)
        val amount = when (transaction.transactionType) {
            TransactionType.DEBIT -> "- $formattedAmount"
            TransactionType.CREDIT -> "+ $formattedAmount"
            else -> formattedAmount
        }
        Text(
            modifier = Modifier.weight(.3f),
            text = amount,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = when (transaction.transactionType) {
                    TransactionType.DEBIT -> red
                    TransactionType.CREDIT -> green
                    else -> Color.Black
                },
                textAlign = TextAlign.End,
            )
        )
    }
}

@Preview
@Composable
fun ItemTransactionPreview() {
    TransactionItemScreen(modifier = Modifier, Transaction())
}
