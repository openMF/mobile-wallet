package org.mifos.mobilewallet.mifospay.auth.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.mifos.mobile.passcode.utils.PassCodeConstants
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.auth.AuthContract
import org.mifos.mobilewallet.mifospay.auth.AuthContract.LoginView
import org.mifos.mobilewallet.mifospay.auth.presenter.LoginPresenter
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityLoginBinding
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity
import org.mifos.mobilewallet.mifospay.registration.ui.MobileVerificationActivity
import org.mifos.mobilewallet.mifospay.registration.ui.SignupMethod
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils.hideSoftKeyboard
import javax.inject.Inject

/**
 * Created by naman on 16/6/17.
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity(), LoginView {
    @Inject
    lateinit var mPresenter: LoginPresenter
    lateinit var mLoginPresenter: AuthContract.LoginPresenter

    //ViewBinding
    private lateinit var binding: ActivityLoginBinding

    private var googleSignInClient: GoogleSignInClient? = null
    private var account: GoogleSignInAccount? = null
    private var mMifosSavingProductId = 0

    private var usernameContent: String = ""
    private var passwordContent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mPresenter.attachView(this)

        binding.loginCompose.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    LoginScreen({ username, password ->
                        usernameContent = username
                        passwordContent = password
                        onLoginClicked()
                    }, {
                        onSignupClicked()
                    })
                }
            }
        }

        val pref = PasscodePreferencesHelper(applicationContext)
        if (pref.passCode.isNotEmpty()) {
            startPassCodeActivity()
        }
        disableLoginButton()

        val edittextlist = listOf(binding.etPassword, binding.etUsername)
        //add focus changedListener and textChangedListener on username and password input field
        edittextlist.forEach {
            it.setOnFocusChangeListener { _, _ -> handleLoginInputChanged() }
        }
        edittextlist.forEach {
            it.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    handleLoginInputChanged()
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
        }

        binding.btnLogin.setOnClickListener{onLoginClicked()}
        binding.llSignup.setOnClickListener{onSignupClicked()}
        binding.bgScreen.setOnClickListener{backgroundScreenClicked()}
    }

    override fun setPresenter(presenter: AuthContract.LoginPresenter?) {
        if (presenter != null) {
            mLoginPresenter = presenter
        }
    }

    private fun handleLoginInputChanged() {
        mPresenter.handleLoginButtonStatus(usernameContent, passwordContent)
    }

    fun onLoginClicked() {
        hideSoftKeyboard(this)
        showProgressDialog(Constants.LOGGING_IN)
        mLoginPresenter.loginUser(usernameContent, passwordContent)
    }

    fun onSignupClicked() {
        val signupMethod = SignupMethod()
        signupMethod.show(supportFragmentManager, Constants.CHOOSE_SIGNUP_METHOD)
    }

    fun backgroundScreenClicked() {
        if (this.currentFocus != null) {
            hideSoftKeyboard(this)
        }
    }

    override fun disableLoginButton() {
        binding.btnLogin.isEnabled = false
    }

    override fun enableLoginButton() {
        binding.btnLogin.isEnabled = true
    }

    override fun loginSuccess() {
        hideProgressDialog()
        hideSoftKeyboard(this)
        startPassCodeActivity()
    }

    override fun loginFail(message: String?) {
        hideSoftKeyboard(this)
        hideProgressDialog()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Starts [PassCodeActivity] with `Constans.INTIAL_LOGIN` as true
     */
    private fun startPassCodeActivity() {
        val intent = Intent(this@LoginActivity, PassCodeActivity::class.java)
        intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
        startActivity(intent)
    }

    fun signupUsingGoogleAccount(mifosSavingsProductId: Int) {
        showProgressDialog(Constants.PLEASE_WAIT)
        mMifosSavingProductId = mifosSavingsProductId
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient!!.getSignInIntent()
        hideProgressDialog()
        startActivityForResult(signInIntent, 11)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        showProgressDialog(Constants.PLEASE_WAIT)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 11) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException::class.java)
                hideProgressDialog()
                signup(mMifosSavingProductId)
            } catch (e: Exception) {
                // Google Sign In failed, update UI appropriately
                DebugUtil.log(Constants.GOOGLE_SIGN_IN_FAILED, e.message)
                Toaster.showToast(this, Constants.GOOGLE_SIGN_IN_FAILED)
                hideProgressDialog()
            }
        }
    }

    fun signup(mifosSavingsProductId: Int) {
        showProgressDialog(Constants.PLEASE_WAIT)
        val intent = Intent(this@LoginActivity, MobileVerificationActivity::class.java)
        mMifosSavingProductId = mifosSavingsProductId
        intent.putExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, mMifosSavingProductId)
        if (account != null) {
            intent.putExtra(Constants.GOOGLE_PHOTO_URI, account!!.photoUrl)
            intent.putExtra(Constants.GOOGLE_DISPLAY_NAME, account!!.displayName)
            intent.putExtra(Constants.GOOGLE_EMAIL, account!!.email)
            intent.putExtra(Constants.GOOGLE_FAMILY_NAME, account!!.familyName)
            intent.putExtra(Constants.GOOGLE_GIVEN_NAME, account!!.givenName)
        }
        hideProgressDialog()
        startActivity(intent)
        if (googleSignInClient != null) {
            googleSignInClient!!.signOut()
                .addOnCompleteListener(this, OnCompleteListener { account = null })
        }
    }
}