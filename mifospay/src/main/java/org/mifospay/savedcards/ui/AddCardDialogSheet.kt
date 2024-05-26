package org.mifospay.savedcards.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.R
import org.mifospay.common.CreditCardUtils
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.utils.ExpirationDateMask
import java.util.Calendar

@Composable
fun AddCardDialogSheet(
    cancelClicked: () -> Unit,
    addClicked: (Card) -> Unit,
    onDismiss: () -> Unit
) {

    MifosBottomSheet(
        content = {
            AddCardDialogSheetContent(
                cancelClicked = cancelClicked,
                addClicked = addClicked
            )
        },
        onDismiss = onDismiss
    )
}

@Composable
fun AddCardDialogSheetContent(cancelClicked: () -> Unit, addClicked: (Card) -> Unit) {

    val context = LocalContext.current
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var creditCardNumber by rememberSaveable { mutableStateOf("") }
    var expiration by rememberSaveable { mutableStateOf("") }
    var cvv by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        MifosOutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            modifier = Modifier.fillMaxWidth(),
            label = R.string.first_name
        )
        Spacer(modifier = Modifier.height(8.dp))
        MifosOutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            modifier = Modifier.fillMaxWidth(),
            label = R.string.last_name
        )
        Spacer(modifier = Modifier.height(8.dp))
        MifosOutlinedTextField(
            value = creditCardNumber,
            onValueChange = { if (it.length <= 16) creditCardNumber = it },
            modifier = Modifier.fillMaxWidth(),
            label = R.string.credit_card_number,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.expiry_date))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            MifosOutlinedTextField(
                value = expiration,
                onValueChange = { if (it.length <= 4) expiration = it },
                modifier = Modifier.weight(1f),
                label = R.string.mm_yy,
                visualTransformation = ExpirationDateMask(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword)
            )
            Spacer(modifier = Modifier.width(16.dp))
            MifosOutlinedTextField(
                value = cvv,
                onValueChange = { if (it.length <= 3) cvv = it },
                modifier = Modifier.weight(1f),
                label = R.string.cvv,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MifosButton(onClick = { cancelClicked() }) {
                Text(text = stringResource(id = R.string.cancel))
            }
            Spacer(modifier = Modifier.width(16.dp))
            MifosButton(onClick = {
                val card = Card(
                    cardNumber = creditCardNumber,
                    cvv = cvv,
                    expiryDate = expiration,
                    firstName = firstName,
                    lastName = lastName
                )

                validateCard(card, onError = {
                    Toast.makeText(context, context.getString(it), Toast.LENGTH_SHORT).show()
                }, onSuccess = { addClicked(it) })
            }) {
                Text(text = stringResource(id = R.string.add))
            }
        }
    }
}

fun validateCard(card: Card, onError: (Int) -> Unit, onSuccess: (Card) -> Unit) {

    when {
        card.cardNumber.isEmpty() || card.cvv.isEmpty() || card.expiryDate.isEmpty() || card.firstName.isEmpty() || card.lastName.isEmpty() -> {
            onError(R.string.all_fields_required)
        }

        card.cardNumber.length < 16 || !CreditCardUtils.validateCreditCardNumber(card.cardNumber) -> {
            onError(R.string.invalid_credit_card)
        }

        card.expiryDate.length < 4 -> {
            onError(R.string.expiry_date_length_error)
        }

        (card.expiryDate.substring(2, 4) == Calendar.getInstance()[Calendar.YEAR].toString()
            .substring(2, 4) && card.expiryDate.substring(0, 2)
            .toInt() < Calendar.getInstance()[Calendar.MONTH] + 1) || card.expiryDate.substring(
            0,
            2
        ).toInt() > 12 -> {
            onError(R.string.invalid_expiry_date)
        }

        card.cvv.length < 3 -> {
            onError(R.string.cvv_length_error)
        }

        else -> {
            onSuccess(card)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddCardDialogSheetContentPreview() {
    AddCardDialogSheetContent(cancelClicked = {}, addClicked = {})
}

@Preview(showBackground = true)
@Composable
private fun AddCardDialogSheetPreview() {
    AddCardDialogSheet(cancelClicked = {}, addClicked = {}, onDismiss = {})
}