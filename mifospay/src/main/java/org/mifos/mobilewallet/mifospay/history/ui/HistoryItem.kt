package org.mifos.mobilewallet.mifospay.history.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.mobilewallet.model.domain.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.theme.creditTextColor
import org.mifos.mobilewallet.mifospay.designsystem.theme.debitTextColor
import org.mifos.mobilewallet.mifospay.designsystem.theme.historyItemTextStyle

@Composable
fun HistoryItem(date: String, amount: String, transactionType: TransactionType) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_transaction),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(id = R.dimen.listImageSize))
        )
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = date,
                style = historyItemTextStyle
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = transactionType.name,
                    color = if (transactionType == TransactionType.CREDIT) creditTextColor else debitTextColor,
                    fontSize = 16.sp,
                )
            }
            Text(
                text = amount,
                style = historyItemTextStyle
            )
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HistoryItemPreview() {
    HistoryItem("date", "amount", TransactionType.CREDIT)
}