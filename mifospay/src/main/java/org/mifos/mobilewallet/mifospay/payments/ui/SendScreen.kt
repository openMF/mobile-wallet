package org.mifos.mobilewallet.mifospay.payments.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosOutlinedTextField
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleMedium16sp
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleNormal18sp

@Composable
fun SendScreen(openScanner: () -> Unit, openContacts: () -> Unit, onSubmit: () -> Unit) {

    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var vpa by remember { mutableStateOf(TextFieldValue("")) }
    var mobileNumber by remember { mutableStateOf(TextFieldValue("")) }
    var isVpaSelected by remember { mutableStateOf(true) }

    Column(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(start = 20.dp, top = 20.dp),
            text = stringResource(id = R.string.select_transfer_method),
            style = styleNormal18sp
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
            ) {
                Chip(
                    selected = isVpaSelected,
                    onClick = { isVpaSelected = true },
                    label = stringResource(id = R.string.vpa)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Chip(
                    selected = !isVpaSelected,
                    onClick = { isVpaSelected = false },
                    label = stringResource(id = R.string.mobile)
                )
            }

            if (isVpaSelected) {
                MifosOutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = R.string.amount,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                MifosOutlinedTextField(
                    value = vpa,
                    onValueChange = { vpa = it },
                    label = R.string.virtual_payment_address,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { openScanner.invoke() }) {
                            Icon(
                                imageVector = Icons.Filled.QrCode2,
                                contentDescription = "Scan QR",
                                tint = Color.Blue
                            )
                        }
                    }
                )
            } else {
                MifosOutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = R.string.amount,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                MifosOutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = R.string.mobile_number,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { openContacts.invoke() }) {
                            Icon(
                                Icons.Filled.ContactPage,
                                contentDescription = "Open Contacts",
                                tint = Color.Blue
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                onClick = { onSubmit.invoke() },
                contentPadding = PaddingValues(12.dp)
            ) {
                Text(
                    stringResource(id = R.string.submit),
                    style = styleMedium16sp.copy(color = Color.White)
                )
            }
        }
    }
}

@Composable
fun Chip(selected: Boolean, onClick: () -> Unit, label: String) {
    Surface(
        color = if (selected) Color.Black else Color.LightGray,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 32.dp, end = 32.dp),
            color = Color.White
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SendScreenPreview() {
    SendScreen({}, {}, {})
}