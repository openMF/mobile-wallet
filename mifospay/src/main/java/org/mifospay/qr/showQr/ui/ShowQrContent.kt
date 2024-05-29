package org.mifospay.qr.showQr.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosOutlinedButton

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
            Text(text = stringResource(id = R.string.set_amount))
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
                text = stringResource(id = R.string.qr_code_details),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when(amount) {
                    null -> String.format("%s: %s", stringResource(R.string.vpa), qrDataString)
                    else -> String.format("%s: %s\n%s: %s", stringResource(R.string.vpa), qrDataString, stringResource(
                        R.string.amount), amount)
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}