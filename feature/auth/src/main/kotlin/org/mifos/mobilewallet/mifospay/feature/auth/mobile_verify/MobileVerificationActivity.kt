package org.mifos.mobilewallet.mifospay.feature.auth.mobile_verify

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.feature.auth.signup.SignupActivity

@AndroidEntryPoint
class MobileVerificationActivity : AppCompatActivity() {

    val viewModel: MobileVerificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MifosTheme {
                MobileVerificationScreen { fullNumber ->
                    onOtpVerificationSuccess(fullNumber)
                }
            }
        }
    }

    private fun onOtpVerificationSuccess(fullNumber: String) {
        val intent = Intent(this@MobileVerificationActivity, SignupActivity::class.java)
        intent.putExtra(
            Constants.MIFOS_SAVINGS_PRODUCT_ID,
            getIntent().getIntExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, 0)
        )
       /* intent.putExtra(
            Constants.GOOGLE_PHOTO_URI, getIntent().getParcelableExtra<Parcelable>(
                Constants.GOOGLE_PHOTO_URI
            ).toString()
        )*/
        intent.putExtra(
            Constants.GOOGLE_DISPLAY_NAME,
            getIntent().getStringExtra(Constants.GOOGLE_DISPLAY_NAME)
        )
        intent.putExtra(
            Constants.GOOGLE_EMAIL,
            getIntent().getStringExtra(Constants.GOOGLE_EMAIL)
        )
        intent.putExtra(
            Constants.GOOGLE_FAMILY_NAME,
            getIntent().getStringExtra(Constants.GOOGLE_FAMILY_NAME)
        )
        intent.putExtra(
            Constants.GOOGLE_GIVEN_NAME,
            getIntent().getStringExtra(Constants.GOOGLE_GIVEN_NAME)
        )
        intent.putExtra(Constants.COUNTRY, "Canada")
        intent.putExtra(Constants.MOBILE_NUMBER, fullNumber)
        startActivity(intent)
        finish()
    }
}