package org.mifos.mobilewallet.mifospay.merchants.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosItemCard
import org.mifos.mobilewallet.mifospay.designsystem.theme.mifosText
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleMedium16sp

@Composable
fun MerchantsItem(
    savingsWithAssociations: SavingsWithAssociations,
    onMerchantClicked: () -> Unit
) {
    MifosItemCard(
        onClick = { onMerchantClicked.invoke() },
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
                        text = savingsWithAssociations.clientName,
                        color = mifosText,
                    )
                    Text(
                        text = savingsWithAssociations.externalId,
                        modifier = Modifier.padding(top = 4.dp),
                        style = styleMedium16sp.copy(mifosText)
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountsItemPreview() {
    MerchantsItem(
        savingsWithAssociations = SavingsWithAssociations(),
        onMerchantClicked = {}
    )
}