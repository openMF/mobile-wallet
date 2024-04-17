package org.mifospay.feature.auth.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.mifos.mobile.passcode.utils.PassCodeConstants
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.common.Constants
import org.mifospay.feature.auth.login.LoginActivity
import org.mifospay.feature.passcode.PassCodeActivity
import org.mifospay.core.designsystem.theme.MifosTheme

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initSignupData(
            savingProductId = intent.getIntExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, 0),
            mobileNumber = intent.getStringExtra(Constants.MOBILE_NUMBER) ?: "",
            countryName = intent.getStringExtra(Constants.COUNTRY) ?: "",
            email = intent.getStringExtra(Constants.GOOGLE_EMAIL) ?: "",
            firstName = intent.getStringExtra(Constants.GOOGLE_GIVEN_NAME) ?: "",
            lastName = intent.getStringExtra(Constants.GOOGLE_FAMILY_NAME) ?: "",
            businessName = intent.getStringExtra(Constants.GOOGLE_DISPLAY_NAME) ?: ""
        )


        setContent {
            MifosTheme {
                SignupScreen {
                    loginSuccess()
                }
            }
        }
    }

    fun onRegisterSuccess(s: String?) {
        // registered but unable to login or user not updated with client
        // TODO :: Consider this case
        // 1. User not updated: when logging in update user
        // 2. User unable to login (must be caused due to server)
        Toast.makeText(this, "Registered successfully.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
        finish()
    }

    private fun loginSuccess() {
        Toast.makeText(this, "Registered successfully.", Toast.LENGTH_SHORT).show()
        PassCodeActivity.startPassCodeActivity(
            context = this,
            bundle = bundleOf(Pair(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)),
        )
        finish()
    }
}