package org.mifospay.feature.upi_setup.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mifospay.feature.upi_setup.viewmodel.DebitCardViewModel
import org.mifos.mobilewallet.mifospay.ui.ExpiryDateInput


@Composable
fun DebitCardScreenContents(
    viewModel: DebitCardViewModel = hiltViewModel()
) {
    var cardNumber by rememberSaveable { mutableStateOf("") }
    var expiryDate by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Column {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(
                    text = "Debit Card Number",
                    style = TextStyle(color = Color.Black)
                )
            },
            value = cardNumber,
            onValueChange = {
                cardNumber = it
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            visualTransformation = VisualTransformation {
                val formattedCardNumber = formatCardNumber(it)
                formattedCardNumber
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.DarkGray,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExpiryDateInput(
            date = expiryDate,
            onDateChange = { expiryDate = it },
            onDone = {
                viewModel.verifyDebitCard(cardNumber, "month, year", expiryDate)
            }
        )
    }
}

fun formatCardNumber(text: AnnotatedString): TransformedText {
    val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
    var out = ""
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 4 == 3 && i != 15) {
            out += "-"
        }
    }
    val creditCardOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) {
                return offset
            }
            if (offset <= 7) {
                return offset + 1
            }
            if (offset <= 11) {
                return offset + 2
            }
            if (offset <= 16) {
                return offset + 3
            }
            return 19
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) {
                return offset
            }
            if (offset <= 9) {
                return offset - 1
            }
            if (offset <= 14) {
                return offset - 2
            }
            if (offset <= 19) {
                return offset - 3
            }
            return 16
        }
    }
    return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
}

@Preview
@Composable
fun DebitCardScreenContentsPreview() {
    DebitCardScreenContents()
}