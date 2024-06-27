package org.mifospay.standinginstruction.ui

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.launch
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.standinginstruction.presenter.NewSIUiState
import org.mifospay.standinginstruction.presenter.NewSIViewModel
import org.mifospay.theme.MifosTheme
import java.util.Calendar

@Composable
fun NewSIScreen(viewModel: NewSIViewModel = hiltViewModel(), onBackPress: () -> Unit) {

    val uiState by viewModel.newSIUiState.collectAsStateWithLifecycle()

    NewSIScreen(
        uiState = uiState,
        onBackPress = onBackPress
    )

}

@Composable
fun NewSIScreen(uiState: NewSIUiState, onBackPress: () -> Unit) {

    MifosScaffold(
        topBarTitle = R.string.tile_si_activity,
        backPress = { onBackPress.invoke() },
        scaffoldContent = {
            when (uiState) {
                is NewSIUiState.Error -> ErrorScreenContent()
                NewSIUiState.Loading ->NewSIContent(it)
                is NewSIUiState.ShowClientDetails -> NewSIContent(it)
                NewSIUiState.Success -> {}
            }

        })
}

@Composable
fun NewSIContent(paddingValues: PaddingValues) {
    var amount by rememberSaveable { mutableStateOf("") }
    var vpa by rememberSaveable { mutableStateOf("") }
    var siInterval by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            trailingIcon = {
                IconButton(onClick = { startScan() }) {
                    Icon(
                        imageVector = Icons.Filled.QrCode2,
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
            if (selectedDate.isNotEmpty()) {
                Text(
                    text = selectedDate,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Black
                )
            }
        }
        MifosButton(
            modifier = Modifier
                .width(150.dp)
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = { showDatePickerDialog() }
        ) {
            Text(text = stringResource(R.string.select_date), color = Color.White)
        }

        MifosButton(
            modifier = Modifier
                .width(150.dp)
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {}
        ) {
            Text(text = stringResource(id = R.string.submit), color = Color.White)
        }
    }
}

class NewSiUiStateProvider : PreviewParameterProvider<NewSIUiState> {
    override val values: Sequence<NewSIUiState>
        get() = sequenceOf(
            NewSIUiState.Loading,
            NewSIUiState.Error("Error"),
            NewSIUiState.Success,
            NewSIUiState.ShowClientDetails(0L, "Pratyush", "External Id")
        )

}

@Preview(showBackground = true)
@Composable
private fun NewSIScreenPreview(
    @PreviewParameter(NewSiUiStateProvider::class) newSIUiState: NewSIUiState
) {
    MifosTheme {
        NewSIScreen(newSIUiState, {})
    }
}
