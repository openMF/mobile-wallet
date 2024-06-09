package org.mifospay.bank.setupUpi.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mifospay.core.model.domain.BankAccountDetails
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.bank.setupUpi.screens.SetupUpiPinScreenRoute
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.theme.MifosTheme

@AndroidEntryPoint
class SetupUpiPinActivity : ComponentActivity() {

    private var bankAccountDetails: BankAccountDetails? = null
    private var index = 0
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = intent.extras
        bankAccountDetails = b!!.getParcelable(Constants.BANK_ACCOUNT_DETAILS)
        index = b.getInt(Constants.INDEX)
        type = b.getString(Constants.TYPE)
        setContent {
            MifosTheme {
                SetupUpiPinScreenRoute(
                    bankAccountDetails = bankAccountDetails!!,
                    type = type!!,
                    index = index
                )
            }
        }
    }

//    private fun setupUpiPinSuccess(mSetupUpiPin: String?) {
//        setUpViewModel.setupUpiPin(bankAccountDetails, mSetupUpiPin)
//        bankAccountDetails!!.isUpiEnabled = true
//        bankAccountDetails!!.upiPin = mSetupUpiPin
//        Toaster.showToast(this, Constants.UPI_PIN_SETUP_COMPLETED_SUCCESSFULLY)
//        val intent = Intent()
//        intent.putExtra(Constants.UPDATED_BANK_ACCOUNT, bankAccountDetails)
//        intent.putExtra(Constants.INDEX, index)
//        setResult(RESULT_OK, intent)
//        finish()
//    }
}