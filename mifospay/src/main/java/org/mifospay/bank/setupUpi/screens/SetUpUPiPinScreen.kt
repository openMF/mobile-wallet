package org.mifospay.bank.setupUpi.screens

import SetUpUpiScreenContent
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.common.Constants
import org.mifospay.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupUpiPinScreen(
    bankAccountDetails: BankAccountDetails,
    type: String,
    index: Int,
    correctlySetingUpi: (String) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = when (type) {
                        Constants.SETUP -> Constants.SETUP_UPI_PIN
                        Constants.CHANGE -> Constants.CHANGE_UPI_PIN
                        Constants.FORGOT -> Constants.FORGOT_UPI_PIN
                        else -> ""
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? Activity)?.onBackPressed()
                    })
                    {
                        Icon(Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id =R.string.back ))
                    }
                })
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SetUpUpiScreenContent(bankAccountDetails=bankAccountDetails,
                    type = type,
                    index = index,
                    correctlySetingUpi = correctlySetingUpi
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewSetupUpipiN() {
    SetupUpiPinScreen(
        getBankAccountDetails(),
        Constants.SETUP_UPI_PIN,
        0,
        {})
}
@Preview
@Composable
fun PreviewChangeUpi() {
    SetupUpiPinScreen(
        getBankAccountDetails(),
        Constants.CHANGE,
        0,
        {})
}
@Preview
@Composable
fun PreviewForgetUpi() {
    SetupUpiPinScreen(
        getBankAccountDetails(),
        Constants.FORGOT,
        0,
        {})
}

fun getBankAccountDetails(): BankAccountDetails {
    return BankAccountDetails(
        "SBI", "Ankur Sharma", "New Delhi",
        "XXXXXXXX9990XXX " + " ", "Savings"
    )
}

