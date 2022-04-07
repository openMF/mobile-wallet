package org.mifos.mobilewallet.mifospay.registration.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.View
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityMobileVerificationBinding
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract.MobileVerificationView
import org.mifos.mobilewallet.mifospay.registration.presenter.MobileVerificationPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils.hideSoftKeyboard
import javax.inject.Inject

class MobileVerificationActivity : BaseActivity(), MobileVerificationView {
    @JvmField
    @Inject
    var mPresenter: MobileVerificationPresenter? = null
    private var mMobileVerificationPresenter: RegistrationContract.MobileVerificationPresenter? =
        null

    private lateinit var binding: ActivityMobileVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMobileVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mPresenter?.attachView(this)
        setToolbarTitle("")
        showColoredBackButton(Constants.WHITE_BACK_BUTTON)
        binding.ccpCode.registerCarrierNumberEditText(binding.etMobileNumber)
        binding.ccpCountry.setCustomMasterCountries(null)

        binding.btnGetOtp.setOnClickListener {
            onGetOTp()
        }

        binding.fabNext.setOnClickListener {
            onNextClicked()
        }
    }

    override fun setPresenter(presenter: RegistrationContract.MobileVerificationPresenter?) {
        mMobileVerificationPresenter = presenter
    }

    private fun onGetOTp() {
        hideSoftKeyboard(this)
        if (binding.ccpCode.isValidFullNumber) {
            showProgressDialog(Constants.SENDING_OTP_TO_YOUR_MOBILE_NUMBER)
            val handler = Handler()
            handler.postDelayed({
                mMobileVerificationPresenter?.requestOTPfromServer(
                    binding.ccpCode.fullNumber,
                    binding.etMobileNumber.text.toString()
                )
            }, 1500)
        } else {
            showToast(getString(R.string.enter_valid_mob_num))
        }
    }

    override fun onRequestOtpSuccess() {
        hideProgressDialog()
        binding.etMobileNumber.isClickable = false
        binding.etMobileNumber.isFocusableInTouchMode = false
        binding.etMobileNumber.isFocusable = false
        binding.ccpCode.setCcpClickable(false)
        binding.etOtp.visibility = View.VISIBLE
        binding.btnGetOtp.isClickable = false
        binding.btnGetOtp.setBackgroundResource(R.drawable.ic_done)
        binding.fabNext.visibility = View.VISIBLE
    }

    override fun onRequestOtpFailed(s: String?) {
        hideProgressDialog()
        showToast(s)
    }

    private fun onNextClicked() {
        hideSoftKeyboard(this)
        binding.fabNext.isClickable = false
        binding.progressBar.visibility = View.VISIBLE
        binding.tvVerifyingOtp.visibility = View.VISIBLE
        binding.etOtp.isClickable = false
        binding.etOtp.isFocusableInTouchMode = false
        binding.etOtp.isFocusable = false
        val handler = Handler()
        handler.postDelayed(
            { mMobileVerificationPresenter?.verifyOTP(binding.etOtp.text.toString()) },
            1500
        )
    }

    override fun onOtpVerificationSuccess() {
        val intent = Intent(this@MobileVerificationActivity, SignupActivity::class.java)
        intent.putExtra(
            Constants.MIFOS_SAVINGS_PRODUCT_ID,
            getIntent().getIntExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, 0)
        )
        intent.putExtra(
            Constants.GOOGLE_PHOTO_URI, getIntent().getParcelableExtra<Parcelable>(
                Constants.GOOGLE_PHOTO_URI
            ).toString()
        )
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
        intent.putExtra(Constants.COUNTRY, binding.ccpCountry.selectedCountryName)
        intent.putExtra(Constants.MOBILE_NUMBER, binding.ccpCode.fullNumber)
        startActivity(intent)
        finish()
    }

    override fun onOtpVerificationFailed(s: String?) {
        binding.fabNext.isClickable = true
        binding.progressBar.visibility = View.GONE
        binding.tvVerifyingOtp.visibility = View.GONE
        binding.etOtp.isClickable = true
        binding.etOtp.isFocusableInTouchMode = true
        binding.etOtp.isFocusable = true
        showToast(s)
    }

    override fun showToast(s: String?) {
        Toaster.showToast(this, s)
    }
}