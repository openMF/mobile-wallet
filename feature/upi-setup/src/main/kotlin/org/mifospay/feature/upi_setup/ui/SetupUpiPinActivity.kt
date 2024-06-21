package org.mifospay.feature.upi_setup.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mifospay.core.model.domain.BankAccountDetails
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.upi_setup.screens.SetupUpiPinScreenRoute

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
}