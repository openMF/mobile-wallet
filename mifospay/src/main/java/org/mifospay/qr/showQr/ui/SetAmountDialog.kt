package org.mifospay.qr.showQr.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.theme.MifosTheme

@Composable
fun SetAmountDialog(
    dismissDialog: () -> Unit,
    resetAmount: () -> Unit,
    prefilledCurrency: String,
    prefilledAmount: String,
    confirmAmount: (String, String) -> Unit,
) {
    val context = LocalContext.current
    var amount by rememberSaveable { mutableStateOf(prefilledAmount) }
    var currency by rememberSaveable { mutableStateOf(prefilledCurrency) }


    Dialog(
        onDismissRequest = { dismissDialog() },
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = stringResource(id = R.string.set_amount),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                MfOutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.set_amount),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                MfOutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.currency),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MifosOutlinedButton(onClick = { resetAmount() }) {
                        Text(text = stringResource(id = R.string.reset))
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    MifosOutlinedButton(onClick = { dismissDialog() }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    MifosButton(
                        onClick = {
                            when {
                                amount.trim().isNullOrEmpty() -> {
                                    Toast.makeText(
                                        context,
                                        R.string.enter_amount,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                amount.trim().toDoubleOrNull() == null -> {
                                    Toast.makeText(
                                        context,
                                        R.string.enter_amount,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                amount.trim().toDouble().compareTo(0.0) <= 0 -> {
                                    Toast.makeText(
                                        context,
                                        R.string.please_enter_valid_amount,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    confirmAmount(amount, currency)
                                }
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun SetAmountDialogPreview() {
    MifosTheme {
        SetAmountDialog(
            dismissDialog = {},
            resetAmount = {},
            prefilledAmount = "",
            confirmAmount = { _, _ -> },
            prefilledCurrency = ""
        )
    }
}