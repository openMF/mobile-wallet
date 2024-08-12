/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import java.util.Calendar

@Composable
internal fun NewSIScreenRoute(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewSIViewModel = hiltViewModel(),
) {
    val uiState by viewModel.newSIUiState.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle()

    var cancelClicked by rememberSaveable { mutableStateOf(false) }

    NewSIScreen(
        uiState = uiState,
        cancelClicked = cancelClicked,
        updateSuccess = updateSuccess,
        onBackPress = onBackPress,
        fetchClient = viewModel::fetchClient,
        setCancelClicked = { cancelClicked = it },
        confirm = viewModel::createNewSI,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun NewSIScreen(
    uiState: NewSIUiState,
    cancelClicked: Boolean,
    updateSuccess: Boolean,
    onBackPress: () -> Unit,
    fetchClient: (String) -> Unit,
    setCancelClicked: (Boolean) -> Unit,
    confirm: (Long, Double, Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        topBarTitle = R.string.feature_standing_instruction_tile_si_activity,
        backPress = onBackPress,
        modifier = modifier,
        scaffoldContent = {
            when (uiState) {
                NewSIUiState.Loading -> NewSIBody(
                    paddingValues = it,
                    fetchClient = fetchClient,
                    cancelClicked = cancelClicked,
                    setCancelClicked = setCancelClicked,
                    confirm = confirm,
                    clientId = 0,
                    updateSuccess = updateSuccess,
                )

                is NewSIUiState.ShowClientDetails -> {
                    NewSIBody(
                        paddingValues = it,
                        fetchClient = fetchClient,
                        cancelClicked = cancelClicked,
                        setCancelClicked = setCancelClicked,
                        confirm = confirm,
                        clientId = uiState.clientId,
                        updateSuccess = updateSuccess,
                    )
                }
            }
        },
    )
}

@Composable
private fun NewSIBody(
    paddingValues: PaddingValues,
    fetchClient: (String) -> Unit,
    cancelClicked: Boolean,
    setCancelClicked: (Boolean) -> Unit,
    confirm: (Long, Double, Int, String) -> Unit,
    clientId: Long,
    updateSuccess: Boolean,
    modifier: Modifier = Modifier,
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var vpa by rememberSaveable { mutableStateOf("") }
    var siInterval by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    if (!cancelClicked) {
        ConfirmTransfer(
            paddingValues = paddingValues,
            clientName = "",
            clientVpa = vpa,
            amount = amount,
            cancel = { setCancelClicked(true) },
            confirm = confirm,
            clientId = clientId,
            recurrenceInterval = siInterval,
            validTill = selectedDate,
            updateSuccess = updateSuccess,
            modifier = modifier,
        )
    } else {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
            )
            .build()
        val scanner = GmsBarcodeScanning.getClient(context, options)

        fun startScan() {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    barcode.rawValue?.let {
                        vpa = it
                    }
                }
                .addOnCanceledListener {
                    // Task canceled
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    e.localizedMessage?.let { Log.d("SendMoney: Barcode scan failed", it) }
                }
        }

        fun showDatePickerDialog() {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
            )
            datePickerDialog.show()
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                value = amount,
                onValueChange = {
                    amount = it
                },
                label = R.string.feature_standing_instruction_amount,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                value = vpa,
                onValueChange = {
                    vpa = it
                },
                label = R.string.feature_standing_instruction_vpa,
                trailingIcon = {
                    IconButton(onClick = { startScan() }) {
                        Icon(
                            imageVector = MifosIcons.QrCode2,
                            contentDescription = "Scan QR",
                            tint = Color.Blue,
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                value = siInterval,
                onValueChange = {
                    siInterval = it
                },
                label = R.string.feature_standing_instruction_interval,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(id = R.string.feature_standing_instruction_valid_till))
            }
            MifosButton(
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = { showDatePickerDialog() },
            ) {
                val text =
                    selectedDate.ifEmpty { stringResource(R.string.feature_standing_instruction_select_date) }
                Text(text = text, color = Color.White)
            }

            MifosButton(
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    fetchClient(vpa)
                    if (updateSuccess) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.feature_standing_instruction_creates),
                            Toast.LENGTH_SHORT,
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            R.string.feature_standing_instruction_failed_to_save_changes,
                            Toast.LENGTH_SHORT,
                        )
                            .show()
                    }
                },
                enabled = selectedDate.isNotEmpty() &&
                    vpa.isNotEmpty() &&
                    amount.isNotEmpty() && siInterval.isNotEmpty(),
            ) {
                Text(
                    text = stringResource(id = R.string.feature_standing_instruction_submit),
                    color = Color.White,
                )
            }
        }
    }
}

internal class NewSiUiStateProvider : PreviewParameterProvider<NewSIUiState> {
    override val values: Sequence<NewSIUiState>
        get() = sequenceOf(
            NewSIUiState.Loading,
            NewSIUiState.ShowClientDetails(
                0L,
                name = "Pratyush",
                externalId = "External Id",
            ),
        )
}

@Composable
private fun ConfirmTransfer(
    paddingValues: PaddingValues,
    clientName: String,
    clientVpa: String,
    amount: String,
    cancel: () -> Unit,
    confirm: (Long, Double, Int, String) -> Unit,
    clientId: Long,
    recurrenceInterval: String,
    validTill: String,
    updateSuccess: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(12.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_standing_instruction_sending_to),
                    style = TextStyle(color = Color.Blue, fontSize = 15.sp),
                )
                Text(text = clientName, style = TextStyle(color = Color.Black, fontSize = 20.sp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_standing_instruction_vpa),
                    style = TextStyle(color = Color.Black, fontSize = 15.sp),
                )
                Text(text = clientVpa, style = TextStyle(color = Color.Black, fontSize = 20.sp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_standing_instruction_amount),
                    style = TextStyle(color = Color.Blue, fontSize = 15.sp),
                )
                Text(
                    text = amount,
                    style = TextStyle(color = Color.Black, fontSize = 20.sp),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MifosButton(
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp),
                onClick = { cancel.invoke() },
            ) {
                Text(
                    text = stringResource(id = R.string.feature_standing_instruction_cancel),
                    color = Color.White,
                )
            }

            MifosButton(
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp),
                onClick = {
                    if (amount.isNotEmpty()) {
                        confirm.invoke(
                            clientId,
                            amount.toDouble(),
                            recurrenceInterval.toInt(),
                            validTill,
                        )
                    } else {
                        // Handle the case when the amount is empty
                        Toast.makeText(
                            context,
                            R.string.feature_standing_instruction_failed_to_save_changes,
                            Toast.LENGTH_SHORT,
                        )
                            .show()
                    }
                    if (updateSuccess) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.feature_standing_instruction_creates),
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            R.string.feature_standing_instruction_failed_to_save_changes,
                            Toast.LENGTH_SHORT,
                        )
                            .show()
                    }
                },

            ) {
                Text(
                    text = stringResource(id = R.string.feature_standing_instruction_confirm),
                    color = Color.White,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmTransferPreview() {
    ConfirmTransfer(
        PaddingValues(12.dp),
        "Pratyush",
        "999999999@axl",
        "100",
        {}, { _, _, _, _ -> },
        0L, "", "", true,
    )
}

@Preview(showBackground = true)
@Composable
private fun NewSIScreenPreview(
    @PreviewParameter(NewSiUiStateProvider::class) newSIUiState: NewSIUiState,
) {
    MifosTheme {
        NewSIScreen(
            newSIUiState,
            cancelClicked = false,
            updateSuccess = true,
            onBackPress = {},
            fetchClient = {},
            setCancelClicked = {},
            confirm = { _, _, _, _ -> },
        )
    }
}
