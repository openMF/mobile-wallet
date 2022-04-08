package org.mifos.mobilewallet.mifospay.registration.ui

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.transition.TransitionManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.mifos.mobile.passcode.utils.PassCodeConstants
import org.json.JSONObject
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivitySignupBinding
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract.SignupView
import org.mifos.mobilewallet.mifospay.registration.presenter.SignupPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.FileUtils
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.ValidateUtil.isValidEmail

class SignupActivity : BaseActivity(), SignupView {
    private lateinit var mPresenter: SignupPresenter
    private lateinit var mSignupPresenter: RegistrationContract.SignupPresenter
    private lateinit var spinnerDialog: SpinnerDialog
    private lateinit var countryName: String
    private lateinit var mobileNumber: String
    private lateinit var countryId: String
    private lateinit var stateId: String
    private var mifosSavingProductId = 0

    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mPresenter.attachView(this)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        setToolbarTitle("Registration")
        mifosSavingProductId = intent.getIntExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, 0)
        if (mifosSavingProductId
            == org.mifos.mobilewallet.core.utils.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
        ) {
            binding.etBusinessShopLayout.visibility = View.VISIBLE
        } else {
            binding.etBusinessShopLayout.visibility = View.GONE
        }
        mobileNumber = intent.getStringExtra(Constants.MOBILE_NUMBER).toString()
        countryName = intent.getStringExtra(Constants.COUNTRY).toString()
        val email = intent.getStringExtra(Constants.GOOGLE_EMAIL)
        val displayName = intent.getStringExtra(Constants.GOOGLE_DISPLAY_NAME)
        val firstName = intent.getStringExtra(Constants.GOOGLE_GIVEN_NAME)
        val lastName = intent.getStringExtra(Constants.GOOGLE_FAMILY_NAME)
        val photoUri = intent.getParcelableExtra<Uri>(Constants.GOOGLE_PHOTO_URI)
        if (displayName != null) {
            binding.etBusinessShopName.setText(displayName)
        }
        if (email != null) {
            binding.etEmail.setText(email)
            binding.etUserName.setText(email.substring(0, email.indexOf('@')))
        }
        binding.etFirstName.setText(firstName)
        binding.etLastName.setText(lastName)
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mPresenter.checkPasswordStrength(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        DebugUtil.log(mobileNumber, countryName, email, displayName, firstName, lastName, photoUri)
        showProgressDialog(Constants.PLEASE_WAIT)
        initSearchableStateSpinner()

        binding.fabNext.setOnClickListener {
            onNextClicked()
        }
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
            spinnerDialog.bindOnSpinerListener { item, position -> binding.etState.setText(item) }
            binding.etState.setOnClickListener { spinnerDialog.showSpinerDialog() }
            hideProgressDialog()
        } catch (e: Exception) {
            Log.d("qxz", e.toString() + " " + e.message)
        }
    }

    override fun setPresenter(presenter: RegistrationContract.SignupPresenter?) {
        if (presenter != null) {
            mSignupPresenter = presenter
        }
    }

    fun onNextClicked() {
        showProgressDialog(Constants.PLEASE_WAIT)
        if (mifosSavingProductId
            == org.mifos.mobilewallet.core.utils.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
            && isEmpty(binding.etBusinessShopName)
        ) {
            Toaster.showToast(this, "All fields are mandatory")
            hideProgressDialog()
            return
        }
        if (isEmpty(binding.etFirstName) || isEmpty(binding.etLastName) || isEmpty(binding.etEmail)
            || isEmpty(binding.etAddressLine1) || isEmpty(binding.etAddressLine2)
            || isEmpty(binding.etPinCode) || isEmpty(binding.etState) || isEmpty(binding.etUserName)
            || isEmpty(binding.etPassword) || isEmpty(binding.etConfirmPassword)
        ) {
            Toaster.showToast(this, "All fields are mandatory")
            hideProgressDialog()
            return
        }
        if (binding.etPassword.text.toString().length < 6) {
            showToast("Password should contain more than 6 characters")
            return
        }
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val email = binding.etEmail.text.toString()
        val businessName = binding.etBusinessShopName.text.toString()
        val addressline1 = binding.etAddressLine1.text.toString()
        val addressline2 = binding.etAddressLine2.text.toString()
        val pincode = binding.etPinCode.text.toString()
        val city = binding.etState.text.toString()
        val username = binding.etUserName.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        if (!email.isValidEmail()) {
            binding.rrContainer.let {
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
        mSignupPresenter.registerUser(
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
        binding.rrContainer.let { TransitionManager.beginDelayedTransition(it) }
        binding.tvPasswordStrength.visibility = View.VISIBLE
        if (value == 0) {
            binding.tvPasswordStrength.text = "Password should contain more than 6 characters"
            return
        }
        binding.pbPasswordStrength.visibility = View.VISIBLE
        binding.pbPasswordStrength.progressDrawable?.setColorFilter(
            colorRes, PorterDuff.Mode.SRC_IN
        )
        binding.pbPasswordStrength.progress = value
        binding.tvPasswordStrength.setText(stringRes)
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