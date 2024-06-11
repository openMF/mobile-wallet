package org.mifospay.bank.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.mifospay.core.model.domain.BankAccountDetails
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.bank.setupUpi.ui.SetupUpiPinActivity
import org.mifospay.base.BaseActivity
import org.mifospay.common.Constants
import org.mifospay.theme.MifosTheme
import org.mifospay.utils.Toaster

@AndroidEntryPoint
class BankAccountDetailActivity : BaseActivity() {

    private lateinit var bankAccountDetails: BankAccountDetails
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bankAccountDetails = intent.extras?.getParcelable(Constants.BANK_ACCOUNT_DETAILS)!!
        index = intent.extras!!.getInt(Constants.INDEX)

        setContent {
            MifosTheme {
                BankAccountDetailScreen(
                    bankAccountDetails = bankAccountDetails,
                    onSetupUpiPin = { onSetupUpiPinClicked() },
                    onChangeUpiPin = { onChangeUpiPinClicked() },
                    onForgotUpiPin = { onForgotUpiPinClicked() },
                    navigateBack = { onBackPressed() }
                )
            }
        }
    }

    private fun onSetupUpiPinClicked() {
        startSetupActivity(Constants.SETUP, index)
    }

    private fun onChangeUpiPinClicked() {
        if (bankAccountDetails.isUpiEnabled) {
            startSetupActivity(Constants.CHANGE, index)
        } else {
            showToast(Constants.SETUP_UPI_PIN)
        }
    }

    private fun onForgotUpiPinClicked() {
        if (bankAccountDetails.isUpiEnabled) {
            startSetupActivity(Constants.FORGOT, index)
        } else {
            showToast(Constants.SETUP_UPI_PIN)
        }
    }

    private fun startSetupActivity(type: String, index: Int) {
        val intent = Intent(this@BankAccountDetailActivity, SetupUpiPinActivity::class.java)
        intent.putExtra(Constants.BANK_ACCOUNT_DETAILS, bankAccountDetails)
        intent.putExtra(Constants.TYPE, type)
        intent.putExtra(Constants.INDEX, index)
        startActivityForResult(intent, SETUP_UPI_REQUEST_CODE)
    }

    fun showToast(message: String?) {
        Toaster.showToast(this, message)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constants.UPDATED_BANK_ACCOUNT, bankAccountDetails)
        intent.putExtra(Constants.INDEX, index)
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }

    companion object {
        const val SETUP_UPI_REQUEST_CODE = 2
    }
}