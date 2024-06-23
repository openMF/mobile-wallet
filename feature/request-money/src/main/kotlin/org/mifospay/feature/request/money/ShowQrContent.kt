package org.mifospay.feature.request.money

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.mifospay.core.designsystem.component.MifosButton

@Composable
fun ShowQrContent(
    qrDataBitmap: Bitmap,
    amount: String?,
    qrDataString: String,
    showAmountDialog: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = qrDataBitmap,
            contentDescription = null,
            modifier = Modifier
                .padding(20.dp)
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        MifosButton(
            modifier = Modifier
                .width(150.dp),
            onClick = { showAmountDialog() }
        ) {
            Text(text = stringResource(id = R.string.feature_request_money_set_amount))
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.feature_request_money_qr_code_details),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (amount) {
                    null -> String.format(
                        "%s: %s",
                        stringResource(R.string.feature_request_money_vpa),
                        qrDataString
                    )

                    else -> String.format(
                        "%s: %s\n%s: %s",
                        stringResource(R.string.feature_request_money_vpa),
                        qrDataString,
                        stringResource(
                            R.string.feature_request_money_amount
                        ),
                        amount
                    )
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}