package org.mifos.mobilewallet.mifospay.registration.ui

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.transition.TransitionManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mobile.passcode.utils.PassCodeConstants
import org.json.JSONObject
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract.SignupView
import org.mifos.mobilewallet.mifospay.registration.presenter.SignupPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.FileUtils
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.ValidateUtil.isValidEmail
import javax.inject.Inject

class SignupActivity : BaseActivity(), SignupView {
    @JvmField
    @Inject
    var mPresenter: SignupPresenter? = null
    var mSignupPresenter: RegistrationContract.SignupPresenter? = null

    @JvmField
    @BindView(R.id.et_first_name)
    var mEtFirstName: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_last_name)
    var mEtLastName: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_email)
    var mEtEmail: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_business_shop_name)
    var mEtBusinessShopName: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_business_shop_layout)
    var mEtBusinessShopLayout: TextInputLayout? = null

    @JvmField
    @BindView(R.id.et_address_line_1)
    var mEtAddressLine1: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_address_line_2)
    var mEtAddressLine2: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_pin_code)
    var mEtPinCode: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_state)
    var mEtCity: TextInputEditText? = null

    @JvmField
    @BindView(R.id.fab_next)
    var mFabNext: FloatingActionButton? = null
    var spinnerDialog: SpinnerDialog? = null

    @JvmField
    @BindView(R.id.et_user_name)
    var mEtUserName: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_password)
    var mEtPassword: TextInputEditText? = null

    @JvmField
    @BindView(R.id.et_confirm_password)
    var mEtConfirmPassword: TextInputEditText? = null

    @JvmField
    @BindView(R.id.rr_container)
    var container: ViewGroup? = null

    @JvmField
    @BindView(R.id.pb_password_strength)
    var passwordStrengthProgress: ProgressBar? = null

    @JvmField
    @BindView(R.id.tv_password_strength)
    var passwordStrengthText: TextView? = null
    private var countryName: String? = null
    private var mobileNumber: String? = null
    private var countryId: String? = null
    private var stateId: String? = null
    private var mifosSavingProductId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        activityComponent.inject(this)
        ButterKnife.bind(this)
        mPresenter?.attachView(this)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        setToolbarTitle("Registration")
        mifosSavingProductId = intent.getIntExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, 0)
        if (mifosSavingProductId
            == org.mifos.mobilewallet.core.utils.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
        ) {
            mEtBusinessShopLayout?.visibility = View.VISIBLE
        } else {
            mEtBusinessShopLayout?.visibility = View.GONE
        }
        mobileNumber = intent.getStringExtra(Constants.MOBILE_NUMBER)
        countryName = intent.getStringExtra(Constants.COUNTRY)
        val email = intent.getStringExtra(Constants.GOOGLE_EMAIL)
        val displayName = intent.getStringExtra(Constants.GOOGLE_DISPLAY_NAME)
        val firstName = intent.getStringExtra(Constants.GOOGLE_GIVEN_NAME)
        val lastName = intent.getStringExtra(Constants.GOOGLE_FAMILY_NAME)
        val photoUri = intent.getParcelableExtra<Uri>(Constants.GOOGLE_PHOTO_URI)
        if (displayName != null) {
            mEtBusinessShopName?.setText(displayName)
        }
        if (email != null) {
            mEtEmail?.setText(email)
            mEtUserName?.setText(email.substring(0, email.indexOf('@')))
        }
        if (firstName != null) {
            mEtFirstName?.setText(firstName)
        }
        if (lastName != null) {
            mEtLastName?.setText(lastName)
        }
        mEtPassword?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mPresenter?.checkPasswordStrength(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        DebugUtil.log(mobileNumber, countryName, email, displayName, firstName, lastName, photoUri)
        showProgressDialog(Constants.PLEASE_WAIT)
        initSearchableStateSpinner()
    }

    private fun initSearchableStateSpinner() {
        var jsonObject: JSONObject?
        try {
            countryId = ""
            jsonObject = FileUtils.readJson(this, "countries.json")
            val countriesArray = jsonObject.getJSONArray("countries")
            for (i in 0 until countriesArray.length()) {
                if (countriesArray.getJSONObject(i).getString("name") == countryName) {
                    countryId = countriesArray.getJSONObject(i).getString("id")
                    break
                }
            }
            jsonObject = FileUtils.readJson(this, "states.json")
            val statesJson = jsonObject.getJSONArray("states")
            val statesList = ArrayList<String>()
            for (i in 0 until statesJson.length()) {
                val statesJsonObject = statesJson.getJSONObject(i)
                if (statesJsonObject.getString("country_id") == countryId) {
                    statesList.add(statesJsonObject.getString("name"))
                    stateId = statesJsonObject.getString("id")
                }
            }
            spinnerDialog = SpinnerDialog(
                this@SignupActivity, statesList,
                "Select or Search State", R.style.DialogAnimations_SmileWindow, "Close"
            )
            spinnerDialog?.bindOnSpinerListener { item, position -> mEtCity?.setText(item) }
            mEtCity?.setOnClickListener { spinnerDialog?.showSpinerDialog() }
            hideProgressDialog()
        } catch (e: Exception) {
            Log.d("qxz", e.toString() + " " + e.message)
        }
    }

    override fun setPresenter(presenter: RegistrationContract.SignupPresenter?) {
        mSignupPresenter = presenter
    }

    @OnClick(R.id.fab_next)
    fun onNextClicked() {
        showProgressDialog(Constants.PLEASE_WAIT)
        if (mifosSavingProductId
            == org.mifos.mobilewallet.core.utils.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
            && isEmpty(mEtBusinessShopName)
        ) {
            Toaster.showToast(this, "All fields are mandatory")
            hideProgressDialog()
            return
        }
        if (isEmpty(mEtFirstName) || isEmpty(mEtLastName) || isEmpty(mEtEmail)
            || isEmpty(mEtAddressLine1) || isEmpty(mEtAddressLine2)
            || isEmpty(mEtPinCode) || isEmpty(mEtCity) || isEmpty(mEtUserName) || isEmpty(
                mEtPassword
            ) || isEmpty(mEtConfirmPassword)
        ) {
            Toaster.showToast(this, "All fields are mandatory")
            hideProgressDialog()
            return
        }
        if (mEtPassword?.text.toString().length < 6) {
            showToast("Password should contain more than 6 characters")
            return
        }
        val firstName = mEtFirstName?.text.toString()
        val lastName = mEtLastName?.text.toString()
        val email = mEtEmail?.text.toString()
        val businessName = mEtBusinessShopName?.text.toString()
        val addressline1 = mEtAddressLine1?.text.toString()
        val addressline2 = mEtAddressLine2?.text.toString()
        val pincode = mEtPinCode?.text.toString()
        val city = mEtCity?.text.toString()
        val username = mEtUserName?.text.toString()
        val password = mEtPassword?.text.toString()
        val confirmPassword = mEtConfirmPassword?.text.toString()
        if (!email.isValidEmail()) {
            container?.let {
                Snackbar.make(it, R.string.validate_email, Snackbar.LENGTH_SHORT).show()
            }
            hideProgressDialog()
            return
        }
        if (password != confirmPassword) {
            Toaster.showToast(this, "Password is not same as Confirm Password")
            hideProgressDialog()
            return
        }
        mSignupPresenter?.registerUser(
            firstName, lastName, mobileNumber, email, businessName,
            addressline1, addressline2, pincode, city, countryName, username, password, stateId,
            countryId, mifosSavingProductId
        )
    }

    override fun onRegisterSuccess(s: String?) {
        // registered but unable to login or user not updated with client
        // TODO :: Consider this case
        // 1. User not updated: when logging in update user
        // 2. User unable to login (must be caused due to server)
        hideProgressDialog()
        showToast("Registered successfully.")
        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
        finish()
    }

    override fun updatePasswordStrength(stringRes: Int, colorRes: Int, value: Int) {
        container?.let { TransitionManager.beginDelayedTransition(it) }
        passwordStrengthText?.visibility = View.VISIBLE
        if (value == 0) {
            passwordStrengthText?.text = "Password should contain more than 6 characters"
            return
        }
        passwordStrengthProgress?.visibility = View.VISIBLE
        passwordStrengthProgress?.progressDrawable?.setColorFilter(
            colorRes, PorterDuff.Mode.SRC_IN
        )
        passwordStrengthProgress?.progress = value
        passwordStrengthText?.setText(stringRes)
    }

    override fun loginSuccess() {
        hideProgressDialog()
        showToast("Registered successfully")
        val intent = Intent(this@SignupActivity, PassCodeActivity::class.java)
        intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
        startActivity(intent)
        finish()
    }

    override fun onRegisterFailed(message: String?) {
        hideProgressDialog()
        showToast(message)
    }

    private fun isEmpty(etText: EditText?): Boolean {
        return etText?.text.toString().trim { it <= ' ' }.isEmpty()
    }

    override fun showToast(s: String?) {
        Toaster.showToast(this, s)
    }
}