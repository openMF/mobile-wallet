package org.mifos.mobilewallet.mifospay.seup_upi.fragment

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobilewallet.mifospay.ui.VerifyStepHeader
import org.mifospay.R
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.utils.Toaster

@Composable
fun UpiPinScreen(
    verificationStatus: Boolean = false,
    contentVisibility: Boolean = false,
    correctlySetingUpi: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(id = R.dimen.value_15dp),
                bottom = dimensionResource(id = R.dimen.value_15dp),
                start = dimensionResource(id = R.dimen.value_10dp),
                end = dimensionResource(id = R.dimen.value_10dp)
            ), elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerifyStepHeader(stringResource(id = R.string.upi_pin_setup), verificationStatus)
            if (contentVisibility) UpiPinScreenContent(correctlySetingUpi)
        }
    }
}

@Composable
fun UpiPinScreenContent(
    correctlySetingUpi: (String) -> Unit
) {
    var steps1 = rememberSaveable { mutableStateOf(0) }
    var upiPin1 = rememberSaveable { mutableStateOf("") }
    var upiPin2 = rememberSaveable { mutableStateOf("") }
    var upiPinTobeMatched1 = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    Text(
        text = if (steps1.value == 0) stringResource(id = R.string.enter_upi_pin)
        else stringResource(id = R.string.reenter_upi),
        color = colorResource(id = R.color.colorUpiPinScreenTitle),
        fontSize = 18.sp,
        style = MaterialTheme.typography.headlineMedium)
    if (steps1.value == 0) {
        BasicTextField(value = upiPin1.value, onValueChange = {
            upiPin1.value = it

            if (upiPin1.value.length == 4) {
                steps1.value = 1
                upiPinTobeMatched1.value = upiPin1.value
            }
        }, keyboardActions = KeyboardActions(onDone = {
            if (upiPin1.value.length == 4) {
                steps1.value = 1
                upiPinTobeMatched1.value = upiPin1.value
            }
        }), keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
        ), decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(4) { index ->
                    CharView2(
                        index = index, text = upiPin1.value
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        )
    } else {
        BasicTextField(value = upiPin2.value, onValueChange = {
            upiPin2.value = it
            if (upiPin2.value.length == 4) {
                if (upiPin1.value == upiPin2.value) {
                    correctlySetingUpi(upiPin2.value)
                }
            }
        }, keyboardActions = KeyboardActions(onDone = {
            if (upiPin2.value.length == 4) {
                if (upiPin1.value == upiPin2.value) {
                    correctlySetingUpi(upiPin2.value)
                }
            } else {
                showToast(context, "Invalid UPI PIN")
            }
        }), keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
        ), decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(4) { index ->
                    CharView2(
                        index = index, text = upiPin2.value
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        )
    }
}

@Composable
fun CharView2(
    index: Int, text: String
) {
    val isFocused = text.length == index

    val char = when {
        index == text.length -> "_"
        index > text.length -> "_"
        else -> text[index].toString()
    }
    androidx.compose.material3.Text(
        modifier = Modifier
            .width(40.dp)
            .wrapContentHeight(align = Alignment.CenterVertically),
        text = char,
        style = MaterialTheme.typography.headlineSmall,
        color = if (isFocused) {
            Color.DarkGray
        } else {
            Color.LightGray
        },
        textAlign = TextAlign.Center
    )
}

fun showToast(context: Context, message: String?) {
    Toaster.showToast(context, message)
}

@Preview
@Composable
fun UpiScreenPreview() {
    MifosTheme {
        UpiPinScreen(verificationStatus = false, contentVisibility = true, {})
    }
}