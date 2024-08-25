/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.mifos.library.countrycodepicker.CountryCodePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosNavigationTopAppBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.core.designsystem.theme.styleNormal18sp

@Composable
fun SendScreenRoute(
    showToolBar: Boolean,
    onBackClick: () -> Unit,
    proceedWithMakeTransferFlow: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SendPaymentViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val selfVpa by viewModel.vpa.collectAsStateWithLifecycle()
    val selfMobile by viewModel.mobile.collectAsStateWithLifecycle()
    val showProgress by viewModel.showProgress.collectAsStateWithLifecycle()

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    SendMoneyScreen(
        showToolBar = showToolBar,
        onBackClick = onBackClick,
        showProgress = showProgress,
        onSubmit = { amount, externalIdOrMobile, sendMethodType ->
            if (!viewModel.checkSelfTransfer(
                    selfVpa = selfVpa,
                    selfMobile = selfMobile,
                    sendMethodType = sendMethodType,
                    externalIdOrMobile = externalIdOrMobile,
                )
            ) {
                viewModel.checkBalanceAvailabilityAndTransfer(
                    externalId = selfVpa,
                    transferAmount = amount.toDouble(),
                    onAnyError = {
                        showToast(context.getString(it))
                    },
                    proceedWithTransferFlow = { externalId, transferAmount ->
                        proceedWithMakeTransferFlow.invoke(
                            externalId,
                            transferAmount.toString(),
                        )
                    },
                )
            } else {
                showToast(context.getString(R.string.feature_send_money_not_allowed))
            }
        },
        modifier = modifier,
    )
}

enum class SendMethodType {
    VPA,
    MOBILE,
}

@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod")
@VisibleForTesting
internal fun SendMoneyScreen(
    showToolBar: Boolean,
    showProgress: Boolean,
    onSubmit: (String, String, SendMethodType) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var amount by rememberSaveable { mutableStateOf("") }
    var vpa by rememberSaveable { mutableStateOf("") }
    var mobileNumber by rememberSaveable { mutableStateOf("") }
    var isValidMobileNumber by rememberSaveable { mutableStateOf(false) }
    var sendMethodType by rememberSaveable { mutableStateOf(SendMethodType.VPA) }
    var isValidInfo by rememberSaveable { mutableStateOf(false) }

    val contactUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    fun validateInfo() {
        isValidInfo =
            when (sendMethodType) {
                SendMethodType.VPA -> amount.isNotEmpty() && vpa.isNotEmpty()
                SendMethodType.MOBILE -> {
                    isValidMobileNumber && mobileNumber.isNotEmpty() && amount.isNotEmpty()
                }
            }
    }

    LaunchedEffect(key1 = contactUri) {
        contactUri?.let {
            mobileNumber = getContactPhoneNumber(it, context)
        }
    }

    val options =
        GmsBarcodeScannerOptions
            .Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
            ).build()

    val scanner = GmsBarcodeScanning.getClient(context, options)

    fun startScan() {
        scanner
            .startScan()
            .addOnSuccessListener { barcode ->
                barcode.rawValue?.let {
                    vpa = it
                }
            }.addOnCanceledListener {
                // Task canceled
            }.addOnFailureListener { e ->
                // Task failed with an exception
                e.localizedMessage?.let { Log.d("SendMoney: Barcode scan failed", it) }
            }
    }

    Box(modifier) {
        Column(Modifier.fillMaxSize()) {
            if (showToolBar) {
                MifosNavigationTopAppBar(
                    titleRes = R.string.feature_send_money_send,
                    onNavigationClick = onBackClick,
                )
            }
            Text(
                modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                text = stringResource(id = R.string.feature_send_money_select_transfer_method),
                style = styleNormal18sp,
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp),
                ) {
                    VpaMobileChip(
                        label = stringResource(id = R.string.feature_send_money_vpa),
                        selected = sendMethodType == SendMethodType.VPA,
                        onClick = { sendMethodType = SendMethodType.VPA },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    VpaMobileChip(
                        label = stringResource(id = R.string.feature_send_money_mobile),
                        selected = sendMethodType == SendMethodType.MOBILE,
                        onClick = { sendMethodType = SendMethodType.MOBILE },
                    )
                }
                MfOutlinedTextField(
                    value = amount,
                    label = stringResource(id = R.string.feature_send_money_amount),
                    onValueChange = {
                        amount = it
                        validateInfo()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )
                when (sendMethodType) {
                    SendMethodType.VPA -> {
                        MfOutlinedTextField(
                            value = vpa,
                            label = stringResource(id = R.string.feature_send_money_virtual_payment_address),
                            onValueChange = {
                                vpa = it
                                validateInfo()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        startScan()
                                    },
                                ) {
                                    Icon(
                                        imageVector = MifosIcons.QrCode2,
                                        contentDescription = "Scan QR",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            },
                        )
                    }

                    SendMethodType.MOBILE -> {
                        EnterPhoneScreen(
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            initialPhoneNumber = mobileNumber,
                            onNumberUpdated = { _, fullPhone, valid ->
                                if (valid) {
                                    mobileNumber = fullPhone
                                }
                                isValidMobileNumber = valid
                                validateInfo()
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                MifosButton(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface,
                    enabled = isValidInfo,
                    onClick = {
                        if (!isValidInfo) return@MifosButton
                        onSubmit(
                            amount,
                            when (sendMethodType) {
                                SendMethodType.VPA -> vpa
                                SendMethodType.MOBILE -> mobileNumber
                            },
                            sendMethodType,
                        )
                        // TODO: Navigate to MakeTransferScreenRoute
                    },
                    contentPadding = PaddingValues(12.dp),
                ) {
                    Text(
                        stringResource(id = R.string.feature_send_money_submit),
                        style = styleMedium16sp.copy(color = MaterialTheme.colorScheme.surface),
                    )
                }
            }
        }

        if (showProgress) {
            MfOverlayLoadingWheel(
                contentDesc = stringResource(id = R.string.feature_send_money_please_wait),
            )
        }
    }
}

@Composable
private fun EnterPhoneScreen(
    onNumberUpdated: (String, String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    initialPhoneNumber: String? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    CountryCodePicker(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        initialPhoneNumber = initialPhoneNumber,
        onValueChange = { (code, phone), isValid ->
            onNumberUpdated(phone, code + phone, isValid)
        },
        label = { Text(stringResource(id = R.string.feature_send_money_phone_number)) },
        keyboardActions = KeyboardActions { keyboardController?.hide() },
    )
}

@Composable
private fun VpaMobileChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosButton(
        onClick = onClick,
        color = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray,
        modifier =
        modifier
            .padding(4.dp)
            .wrapContentSize(),
    ) {
        Text(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            text = label,
        )
    }
}

private suspend fun getContactPhoneNumber(
    uri: Uri,
    context: Context,
): String {
    val contactId: String = uri.lastPathSegment ?: return ""
    return withContext(Dispatchers.IO) {
        val phoneCursor =
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(contactId),
                null,
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
private fun SendMoneyScreenWithToolBarPreview() {
    SendMoneyScreen(
        onSubmit = { _, _, _ -> },
        onBackClick = {},
        showProgress = false,
        showToolBar = true,
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun SendMoneyScreenWithoutToolBarPreview() {
    SendMoneyScreen(
        onSubmit = { _, _, _ -> },
        onBackClick = {},
        showProgress = false,
        showToolBar = false,
    )
}
