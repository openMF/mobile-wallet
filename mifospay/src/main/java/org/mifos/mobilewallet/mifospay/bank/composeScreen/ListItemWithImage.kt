package org.mifos.mobilewallet.mifospay.bank.composeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.theme.MifosTheme

@Composable
fun ListItemWithImage(
    bankAccount: BankAccountDetails,
    onAccountClicked: (BankAccountDetails) -> Unit
) {
    var margin = dimensionResource(id = R.dimen.marginItemsInSectionSmall)
    var title = bankAccount.bankName ?: ""
    var subtitle = bankAccount.accountholderName ?: ""
    var optionalCaption = bankAccount.branch ?: ""
    Column(
        modifier = Modifier.clickable {
            onAccountClicked(bankAccount)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = margin
                ),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bank),
                contentDescription = "bank`s icon",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(
                        start = margin,
                        end = margin
                    )
                    .height(dimensionResource(id = R.dimen.listImageSize))
                    .width(dimensionResource(id = R.dimen.listImageSize))
            )

            Column {
                Text(
                    text = subtitle,
                    color = colorResource(id = R.color.colorTextSecondary),
                )
                Text(
                    text = title,
                    color = colorResource(id = R.color.colorTextPrimary),
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 16.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = optionalCaption,
                    modifier = Modifier.padding(
                        top = margin,
                        bottom = margin,
                        end = margin,
                        start = margin
                    ),
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.colorTextSecondary)
                )
            }
        }
        Divider(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp))
    }
}

@Preview
@Composable
fun ListItemPreview() {
    MifosTheme {
        ListItemWithImage(
            bankAccount = BankAccountDetails(
                "SBI", "Ankur Sharma", "New Delhi",
                "1234567890", "Savings"
            ),
            {}
        )
    }
}
