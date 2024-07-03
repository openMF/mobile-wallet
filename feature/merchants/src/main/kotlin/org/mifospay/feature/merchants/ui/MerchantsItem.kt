package org.mifospay.feature.merchants.ui

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.theme.mifosText
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.feature.merchants.R

@Composable
fun MerchantsItem(
    savingsWithAssociations: SavingsWithAssociations,
    onMerchantClicked: () -> Unit,
    onMerchantLongPressed: (String?) -> Unit
) {
    MifosCard(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    onMerchantLongPressed(savingsWithAssociations.externalId)
                }
            )
        },
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
        onMerchantClicked = {},
        onMerchantLongPressed = {}
    )
}