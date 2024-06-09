package org.mifospay.bank.setupUpi.screens

import SetUpUpiScreenContent
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.R
import org.mifospay.bank.setupUpi.viewmodel.SetUpUpiViewModal
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.utils.Toaster

@Composable
fun SetupUpiPinScreenRoute(
    setUpViewModel: SetUpUpiViewModal = hiltViewModel(),
    bankAccountDetails: BankAccountDetails,
    type: String,
    index: Int
) {
    SetupUpiPinScreen(
        bankAccountDetails = bankAccountDetails,
        type = type,
        index = index,
        setupUpiPin = {
            setUpViewModel.setupUpiPin(bankAccountDetails, it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupUpiPinScreen(
    bankAccountDetails: BankAccountDetails,
    type: String,
    index: Int,
    setupUpiPin: (String) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (type) {
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
                        Icon(
                            MifosIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                })
        },
        content = { it ->
            Column(
                modifier = Modifier
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SetUpUpiScreenContent(
                    bankAccountDetails = bankAccountDetails,
                    type = type,
                    correctlySettingUpi = {
                        setupUpiPin(it)
                        bankAccountDetails.isUpiEnabled = true
                        bankAccountDetails.upiPin = it
                        Toaster.showToast(context, Constants.UPI_PIN_SETUP_COMPLETED_SUCCESSFULLY)
                        val intent = Intent().apply {
                            putExtra(Constants.UPDATED_BANK_ACCOUNT, bankAccountDetails)
                            putExtra(Constants.INDEX, index)
                        }
                        (context as? Activity)?.setResult(Activity.RESULT_OK, intent)
                        (context as? Activity)?.finish()
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewSetupUpiPin() {
    SetupUpiPinScreen(
        getBankAccountDetails(),
        Constants.SETUP_UPI_PIN,
        0
    ) {}
}

@Preview
@Composable
fun PreviewChangeUpi() {
    SetupUpiPinScreen(
        getBankAccountDetails(),
        Constants.CHANGE,
        0
    ) {}
}

@Preview
@Composable
fun PreviewForgetUpi() {
    SetupUpiPinScreen(
        getBankAccountDetails(),
        Constants.FORGOT,
        0
    ) {}
}

fun getBankAccountDetails(): BankAccountDetails {
    return BankAccountDetails(
        "SBI", "Ankur Sharma", "New Delhi",
        "XXXXXXXX9990XXX " + " ", "Savings"
    )
}

