package org.mifospay.qr.showQr.ui

import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import org.mifospay.core.data.util.Constants
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosCustomDialog
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.feature.auth.utils.ValidateUtil.isValidEmail
import org.mifospay.theme.MifosTheme
import java.time.temporal.TemporalAmount

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
    var amountValidator by rememberSaveable { mutableStateOf<String?>(null) }
    var currencyValidator by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = amount) {
        amountValidator = when {
            amount.trim().isEmpty() -> context.getString(R.string.enter_amount)
            amount.trim().toDoubleOrNull() == null -> context.getString(R.string.enter_valid_amount)
            amount.trim().toDouble().compareTo(0.0) <= 0 -> context.getString(R.string.enter_valid_amount)
            else -> null
        }
    }

    LaunchedEffect(key1 = currency) {
        currencyValidator = when {
            currency.trim().isEmpty() -> context.getString(R.string.enter_currency)
            else -> null
        }
    }

    fun validateAllFields(): Boolean {
        if (amountValidator != null) {
            Toast.makeText(context, amountValidator, Toast.LENGTH_SHORT).show()
            return false
        }

        if (currencyValidator != null) {
            Toast.makeText(context, currencyValidator, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    MifosCustomDialog(
        onDismiss = { dismissDialog() },
        content = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = stringResource(id = R.string.set_amount),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        MifosOutlinedButton(onClick = { resetAmount() }) {
                            Text(text = stringResource(id = R.string.reset))
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        MifosOutlinedButton(onClick = { dismissDialog() }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        MifosButton(
                            onClick = {
                                if (validateAllFields()) {
                                    confirmAmount(amount, currency)
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    }
                }
            }
        }
    )
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