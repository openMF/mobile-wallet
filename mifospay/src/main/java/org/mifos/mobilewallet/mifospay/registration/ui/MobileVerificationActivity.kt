package org.mifos.mobilewallet.mifospay.registration.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityMobileVerificationBinding
import org.mifos.mobilewallet.mifospay.registration.MobileVerificationScreen
import org.mifos.mobilewallet.mifospay.registration.MobileVerificationViewModel
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.common.Constants

@AndroidEntryPoint
class MobileVerificationActivity : BaseActivity() {

    val viewModel: MobileVerificationViewModel by viewModels()

    //ViewBinding
    private lateinit var binding: ActivityMobileVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMobileVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mobileVerifyCompose.setContent {
            MifosTheme {
                MobileVerificationScreen { fullNumber ->
                    onOtpVerificationSuccess(fullNumber)
                }
            }
        }

        setToolbarTitle("")
        showColoredBackButton(R.drawable.ic_arrow_back_white_24dp)
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