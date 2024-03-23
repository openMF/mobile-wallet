package org.mifos.mobilewallet.mifospay.payments.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleMedium16sp
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleNormal18sp
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferViewModel

@Composable
fun RequestScreen(
    viewModel: TransferViewModel = hiltViewModel(),
    showQr: () -> Unit
) {
    val vpa by viewModel.vpa.collectAsState()
    val mobile by viewModel.mobile.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(start = 20.dp, top = 30.dp),
            text = stringResource(id = R.string.receive),
            style = styleNormal18sp.copy(Color.Blue)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .weight(1f)
            ) {
                Column {
                    Text(text = stringResource(id = R.string.virtual_payment_address_vpa))
                    Text(text = vpa, style = styleMedium16sp)
                }

                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(text = stringResource(id = R.string.mobile_number))
                    Text(text = mobile, style = styleMedium16sp)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { showQr.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        tint = Color.Blue,
                        contentDescription = stringResource(id = R.string.show_code),
                    )
                }
                Text(
                    text = stringResource(id = R.string.show_code),
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RequestScreenPreview() {
    RequestScreen(hiltViewModel(),{})
}