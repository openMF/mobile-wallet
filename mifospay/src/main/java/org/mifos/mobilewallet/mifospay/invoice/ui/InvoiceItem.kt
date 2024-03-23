package org.mifos.mobilewallet.mifospay.invoice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.theme.grey

@Composable
fun InvoiceItem(
    invoiceTitle: String,
    invoiceAmount: String,
    invoiceStatus: String,
    invoiceDate: String,
    invoiceId: String,
    invoiceStatusIcon: Long
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    id = if (invoiceStatusIcon == 0L) R.drawable.ic_remove_circle_outline_black_24dp
                    else R.drawable.ic_check_round_black_24dp
                ),
                contentDescription = "Invoice Status",
                modifier = Modifier
                    .size(64.dp)
                    .padding(5.dp),
                tint = if (invoiceStatusIcon == 0L) Color.Yellow else Color.Blue
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = invoiceTitle,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = invoiceAmount,
                        color = Color.Black,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = invoiceStatus,
                    color = grey,
                    modifier = Modifier.padding(top = 1.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = invoiceDate,
                        color = grey,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = invoiceId,
                        color = grey,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInvoiceItem() {
    InvoiceItem("Logo for Richard", "$3000", "Pending", "12/3/4", "Invoice id:12345", 0L)
}
