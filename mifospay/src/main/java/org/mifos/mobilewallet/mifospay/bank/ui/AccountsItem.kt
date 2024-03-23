package org.mifos.mobilewallet.mifospay.bank.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosItemCard
import org.mifos.mobilewallet.mifospay.designsystem.theme.mifosText
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleMedium16sp

@Composable
fun AccountsItem(
    bankAccountDetails: BankAccountDetails,
    onAccountClicked: () -> Unit
) {
    MifosItemCard(
        onClick = { onAccountClicked.invoke() },
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bank),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp, end = 16.dp)
                        .size(39.dp)
                )

                Column {
                    Text(
                        text = bankAccountDetails.accountholderName.toString(),
                        color = mifosText,
                    )
                    Text(
                        text = bankAccountDetails.bankName.toString(),
                        modifier = Modifier.padding(top = 4.dp),
                        style = styleMedium16sp.copy(mifosText)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = bankAccountDetails.branch.toString(),
                        modifier = Modifier.padding(16.dp),
                        fontSize = 12.sp,
                        color = mifosText
                    )
                }
            }
        }
        Divider(
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountsItemPreview() {
    AccountsItem(
        bankAccountDetails = BankAccountDetails("A", "B", "C"),
        onAccountClicked = {}
    )
}