package org.mifospay.feature.upi_setup.screens

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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.mifospay.ui.VerifyStepHeader
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.upi_setup.R

@Composable
fun UpiPinScreen(
    verificationStatus: Boolean = false,
    contentVisibility: Boolean = false,
    correctlySettingUpi: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 15.dp,
                bottom = 15.dp,
                start = 10.dp,
                end = 10.dp
            ), elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerifyStepHeader(
                stringResource(id = R.string.feature_upi_setup_upi_pin_setup),
                verificationStatus
            )
            if (contentVisibility) UpiPinScreenContent(correctlySettingUpi)
        }
    }
}

@Composable
fun UpiPinScreenContent(
    correctlySettingUpi: (String) -> Unit
) {
    val steps1 = rememberSaveable { mutableStateOf(0) }
    val upiPin1 = rememberSaveable { mutableStateOf("") }
    val upiPin2 = rememberSaveable { mutableStateOf("") }
    val upiPinTobeMatched1 = rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    Text(
        text = if (steps1.value == 0) stringResource(id = R.string.feature_upi_setup_enter_upi_pin)
        else stringResource(id = R.string.feature_upi_setup_reenter_upi),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 18.sp,
        style = MaterialTheme.typography.headlineMedium
    )

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
                    UpiPinCharView(
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
                    correctlySettingUpi(upiPin2.value)
                }
            }
        }, keyboardActions = KeyboardActions(onDone = {
            if (upiPin2.value.length == 4) {
                if (upiPin1.value == upiPin2.value) {
                    correctlySettingUpi(upiPin2.value)
                }
            } else {
                showSnackbar(
                    snackbarHostState,
                    R.string.feature_upi_setup_invalid_upi_pin.toString()
                )
            }
        }), keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
        ), decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(4) { index ->
                    UpiPinCharView(
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

fun showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
    CoroutineScope(Dispatchers.Main).launch {
        snackbarHostState.showSnackbar(message)
    }
}

@Composable
fun UpiPinCharView(
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

@Preview
@Composable
fun UpiScreenPreview() {
    MifosTheme {
        UpiPinScreen(verificationStatus = false, contentVisibility = true, {})
    }
}