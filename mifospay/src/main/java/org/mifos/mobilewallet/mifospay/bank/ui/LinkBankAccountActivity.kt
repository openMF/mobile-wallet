package org.mifos.mobilewallet.mifospay.bank.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.theme.MifosTheme


@AndroidEntryPoint
class LinkBankAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                LinkBankAccountScreen(activity = this, onBackPress = {onBackPressed()})
            }
        }
    }

    fun addBankAccount(bankAccountDetails: BankAccountDetails?) {
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent()
            intent.putExtra(Constants.NEW_BANK_ACCOUNT, bankAccountDetails)
            setResult(RESULT_OK, intent)
            finish()
        }, 1500)
    }
}

