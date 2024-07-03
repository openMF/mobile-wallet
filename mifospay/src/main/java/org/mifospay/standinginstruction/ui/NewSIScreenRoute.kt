package org.mifospay.standinginstruction.ui

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
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
import org.mifospay.R
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.standinginstruction.presenter.NewSIUiState
import org.mifospay.standinginstruction.presenter.NewSIViewModel
import org.mifospay.theme.MifosTheme
import java.util.Calendar

@Composable
fun NewSIScreenRoute(
    viewModel: NewSIViewModel = hiltViewModel(),
    onBackPress: () -> Unit
) {

    val uiState by viewModel.newSIUiState.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle()

    var cancelClicked by rememberSaveable { mutableStateOf(false) }

    NewSIScreen(
        uiState = uiState,
        onBackPress = onBackPress,
        fetchClient = { viewModel.fetchClient(it) },
        cancelClicked = cancelClicked,
        setCancelClicked = { cancelClicked = it },
        confirm = { clientId, amount, recurrenceInterval, validTill ->
            viewModel.createNewSI(clientId, amount, recurrenceInterval, validTill)
        },
        updateSuccess = updateSuccess
    )

}

@Composable
fun NewSIScreen(
    uiState: NewSIUiState,
    onBackPress: () -> Unit,
    fetchClient: (String) -> Unit,
    cancelClicked: Boolean,
    setCancelClicked: (Boolean) -> Unit,
    confirm: (Long, Double, Int, String) -> Unit,
    updateSuccess: Boolean
) {

    MifosScaffold(
        topBarTitle = R.string.tile_si_activity,
        backPress = { onBackPress.invoke() },
        scaffoldContent = {
            when (uiState) {

                NewSIUiState.Loading -> NewSIBody(
                    it,
                    fetchClient,
                    cancelClicked,
                    setCancelClicked,
                    confirm,
                    clientId = 0,
                    updateSuccess = updateSuccess
                )

                is NewSIUiState.ShowClientDetails -> {
                    NewSIBody(
                        it,
                        fetchClient,
                        cancelClicked,
                        setCancelClicked,
                        confirm,
                        clientId = uiState.clientId,
                        updateSuccess = updateSuccess
                    )
                }
            }
        })
}

@Composable
fun NewSIBody(
    paddingValues: PaddingValues,
    fetchClient: (String) -> Unit,
    cancelClicked: Boolean,
    setCancelClicked: (Boolean) -> Unit,
    confirm: (Long, Double, Int, String) -> Unit,
    clientId: Long,
    updateSuccess: Boolean

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
            updateSuccess = updateSuccess
        )
    } else {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
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
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MifosOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                value = amount,
                onValueChange = {
                    amount = it
                },
                label = R.string.amount,
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
                label = R.string.vpa,
                trailingIcon = {
                    IconButton(onClick = { startScan() }) {
                        Icon(
                            imageVector = MifosIcons.QrCode2,
                            contentDescription = "Scan QR",
                            tint = Color.Blue
                        )
                    }
                }
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
                label = R.string.recurrence_interval_in_months
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.valid_till))
            }
            MifosButton(
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = { showDatePickerDialog() }
            ) {
                val text = selectedDate.ifEmpty { stringResource(R.string.select_date) }
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
                            context.getString(R.string.successfully_creates_new_standing_instruction),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            org.mifospay.feature.profile.R.string.failed_to_save_changes,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                enabled = selectedDate.isNotEmpty() && vpa.isNotEmpty() && amount.isNotEmpty() && siInterval.isNotEmpty()
            ) {
                Text(text = stringResource(id = R.string.submit), color = Color.White)
            }
        }
    }

}

class NewSiUiStateProvider : PreviewParameterProvider<NewSIUiState> {
    override val values: Sequence<NewSIUiState>
        get() = sequenceOf(
            NewSIUiState.Loading,
            NewSIUiState.ShowClientDetails(0L, "Pratyush", "External Id")
        )

}

@Composable
fun ConfirmTransfer(
    paddingValues: PaddingValues,
    clientName: String,
    clientVpa: String,
    amount: String,
    cancel: () -> Unit,
    confirm: (Long, Double, Int, String) -> Unit,
    clientId: Long,
    recurrenceInterval: String,
    validTill: String,
    updateSuccess: Boolean
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(12.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.sending_to),
                    style = TextStyle(color = Color.Blue, fontSize = 15.sp)
                )
                Text(text = clientName, style = TextStyle(color = Color.Black, fontSize = 20.sp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.vpa),
                    style = TextStyle(color = Color.Black, fontSize = 15.sp)
                )
                Text(text = clientVpa, style = TextStyle(color = Color.Black, fontSize = 20.sp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.amount),
                    style = TextStyle(color = Color.Blue, fontSize = 15.sp)
                )
                Text(
                    text = amount, style = TextStyle(color = Color.Black, fontSize = 20.sp)
                )
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MifosButton(
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp),
                onClick = { cancel.invoke() }
            ) {
                Text(text = stringResource(id = R.string.cancel), color = Color.White)
            }

            MifosButton(
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp),
                onClick = {
                    confirm.invoke(
                        clientId,
                        amount.toDouble(),
                        recurrenceInterval.toInt(),
                        validTill
                    )
                    if (updateSuccess) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.successfully_creates_new_standing_instruction),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            org.mifospay.feature.profile.R.string.failed_to_save_changes,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

            ) {
                Text(text = stringResource(id = R.string.confirm), color = Color.White)
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
        0L, "", "", true
    )
}

@Preview(showBackground = true)
@Composable
private fun NewSIScreenPreview(
    @PreviewParameter(NewSiUiStateProvider::class) newSIUiState: NewSIUiState
) {
    MifosTheme {
        NewSIScreen(newSIUiState, {}, {}, false, {}, { _, _, _, _ -> }, true)
    }
}
