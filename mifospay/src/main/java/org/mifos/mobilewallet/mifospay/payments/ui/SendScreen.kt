package org.mifos.mobilewallet.mifospay.payments.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MfOutlinedTextField
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosOutlinedTextField
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleMedium16sp
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleNormal18sp
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity

@Composable
fun SendScreen(onSubmit: () -> Unit) {

    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var vpa by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var isVpaSelected by remember { mutableStateOf(true) }
    var contactUri by remember { mutableStateOf<Uri?>(null) }

    val contactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let {
            contactUri = uri
        }
    }
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val scanResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
                if (scanResult.contents != null) {
                    vpa = scanResult.contents
                }
            }
        }
    )
    LaunchedEffect(key1 = contactUri) {
        contactUri?.let {
            mobileNumber = getContactPhoneNumber(it, context)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                contactLauncher.launch(null)
            } else {
                // Handle permission denial
            }
        }
    )

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
                VpaMobileChip(
                    selected = isVpaSelected,
                    onClick = { isVpaSelected = true },
                    label = stringResource(id = R.string.vpa)
                )
                Spacer(modifier = Modifier.width(8.dp))
                VpaMobileChip(
                    selected = !isVpaSelected,
                    onClick = { isVpaSelected = false },
                    label = stringResource(id = R.string.mobile)
                )
            }
            MfOutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = stringResource(id = R.string.amount),
                modifier = Modifier.fillMaxWidth()
            )
            if (isVpaSelected) {
                MifosOutlinedTextField(
                    value = vpa,
                    onValueChange = { vpa = it },
                    label = R.string.virtual_payment_address,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            val intent = Intent(context, ReadQrActivity::class.java)
                            scannerLauncher.launch(intent)
                        }) {
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
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = R.string.mobile_number,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                        }) {
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
fun VpaMobileChip(selected: Boolean, onClick: () -> Unit, label: String) {
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

suspend fun getContactPhoneNumber(uri: Uri, context: Context): String {
    val contactId: String = uri.lastPathSegment ?: return ""
    return withContext(Dispatchers.IO) {
        val phoneCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )
        phoneCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val phoneNumberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                cursor.getString(phoneNumberIndex)
            } else {
                ""
            }
        } ?: ""
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SendScreenPreview() {
    SendScreen({})
}