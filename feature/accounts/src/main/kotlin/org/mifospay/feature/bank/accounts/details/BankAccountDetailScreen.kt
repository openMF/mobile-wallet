package org.mifospay.feature.bank.accounts.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.theme.mifosText
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.feature.bank.accounts.R

@Composable
fun BankAccountDetailScreen(
    bankAccountDetails: BankAccountDetails,
    onSetupUpiPin: () -> Unit,
    onChangeUpiPin: () -> Unit,
    onForgotUpiPin: () -> Unit,
    navigateBack: () -> Unit
) {
    BankAccountDetailScreen(
        bankName = bankAccountDetails.bankName.toString(),
        accountHolderName = bankAccountDetails.accountholderName.toString(),
        branchName = bankAccountDetails.branch.toString(),
        ifsc = bankAccountDetails.ifsc.toString(),
        type = bankAccountDetails.type.toString(),
        isUpiEnabled = bankAccountDetails.isUpiEnabled,
        onSetupUpiPin = onSetupUpiPin,
        onChangeUpiPin = onChangeUpiPin,
        onForgotUpiPin = onForgotUpiPin,
        navigateBack = navigateBack
    )
}

@Composable
fun BankAccountDetailScreen(
    bankName: String,
    accountHolderName: String,
    branchName: String,
    ifsc: String,
    type: String,
    isUpiEnabled: Boolean,
    onSetupUpiPin: () -> Unit,
    onChangeUpiPin: () -> Unit,
    onForgotUpiPin: () -> Unit,
    navigateBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MifosTopBar(topBarTitle = R.string.bank_account_details) { navigateBack.invoke() }
        Column(
            modifier = Modifier
                .padding(20.dp)
                .border(2.dp, Color.Black)
                .padding(20.dp)
        ) {
            BankAccountDetailRows(
                modifier = Modifier.fillMaxWidth(),
                detail = R.string.bank_name,
                detailValue = bankName
            )
            BankAccountDetailRows(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                detail = R.string.ac_holder_name,
                detailValue = accountHolderName
            )
            BankAccountDetailRows(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                detail = R.string.branch_name,
                detailValue = branchName
            )
            BankAccountDetailRows(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                detail = R.string.ifsc,
                detailValue = ifsc
            )
            BankAccountDetailRows(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                detail = R.string.type,
                detailValue = type
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            BankAccountDetailButton(
                btnText = R.string.setup_upi,
                onClick = { onSetupUpiPin.invoke() },
                isUpiEnabled = !isUpiEnabled,
                hasTrailingIcon = false
            )

            BankAccountDetailButton(
                btnText = R.string.delete_bank,
                onClick = {},
                isUpiEnabled = !isUpiEnabled
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            BankAccountDetailButton(
                btnText = R.string.change_upi_pin,
                onClick = { onChangeUpiPin.invoke() },
                isUpiEnabled = isUpiEnabled,
                modifier = Modifier.fillMaxWidth()
            )
            BankAccountDetailButton(
                btnText = R.string.forgot_upi_pin,
                onClick = { onForgotUpiPin.invoke() },
                isUpiEnabled = isUpiEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BankAccountDetailRows(
    modifier: Modifier, detail: Int, detailValue: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = detail), modifier = Modifier.padding(end = 10.dp)
        )
        Text(text = detailValue, style = styleMedium16sp)
    }
}

@Composable
fun BankAccountDetailButton(
    modifier: Modifier = Modifier,
    btnText: Int,
    onClick: () -> Unit,
    isUpiEnabled: Boolean,
    hasTrailingIcon: Boolean = false
) {
    if (isUpiEnabled) {
        Button(
            onClick = { onClick.invoke() },
            colors = ButtonDefaults.buttonColors(Color.White),
            modifier = modifier
                .padding(start = 20.dp, end = 20.dp),
            contentPadding = PaddingValues(20.dp),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = btnText),
                    style = TextStyle(color = mifosText, fontSize = 18.sp)
                )
                if (hasTrailingIcon) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BankAccountDetailUpiDisabledPreview() {
    BankAccountDetailScreen("Mifos Bank",
        "Mifos Account Holder",
        "Mifos Branch",
        "IFSC",
        "type",
        false,
        {}, {}, {}, {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BankAccountDetailUpiEnabledPreview() {
    BankAccountDetailScreen("Mifos Bank",
        "Mifos Account Holder",
        "Mifos Branch",
        "IFSC",
        "type",
        true,
        {}, {}, {}, {}
    )
}