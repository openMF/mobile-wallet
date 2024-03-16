package org.mifos.mobilewallet.mifospay.feature.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme

/**
 * Created by naman on 16/6/17.
 */
@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel by viewModels<LoginViewModel>()

    private var usernameContent: String = ""
    private var passwordContent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val pref = PasscodePreferencesHelper(applicationContext)
        if (pref.passCode.isNotEmpty()) {
            startPassCodeActivity()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when(uiState) {
                        is LoginViewModel.LoginUiState.Success -> {
                            loginSuccess()
                        }

                        is LoginViewModel.LoginUiState.Error -> {

                        }

                        is LoginViewModel.LoginUiState.Loading -> {

                        }

                        is LoginViewModel.LoginUiState.None -> {

                        }
                    }
                }
            }
        }
    }

    private fun loginSuccess() {
        //hideProgressDialog()
        //hideSoftKeyboard(this)
        startPassCodeActivity()
    }

    private fun onLoginClicked() {
        // hideSoftKeyboard(this)
        // showProgressDialog(Constants.LOGGING_IN)
        viewModel.loginUser(usernameContent, passwordContent)
    }

    private fun onSignupClicked() {
        /*val signupMethod = SignupMethod()
        signupMethod.show(supportFragmentManager, Constants.CHOOSE_SIGNUP_METHOD)*/
    }

    fun loginFail(message: String?) {
        /*hideSoftKeyboard(this)
        hideProgressDialog()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()*/
    }

    /**
     * Starts [PassCodeActivity] with `Constans.INTIAL_LOGIN` as true
     */
    private fun startPassCodeActivity() {
        /*val intent = Intent(this@LoginActivity, PassCodeActivity::class.java)
        intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
        startActivity(intent)*/
    }

    fun signupUsingGoogleAccount(mifosSavingsProductId: Int) {
        /*showProgressDialog(Constants.PLEASE_WAIT)
        mMifosSavingProductId = mifosSavingsProductId
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient!!.getSignInIntent()
        hideProgressDialog()
        startActivityForResult(signInIntent, 11)*/
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*showProgressDialog(Constants.PLEASE_WAIT)
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
        }*/
    }

    fun signup(mifosSavingsProductId: Int) {
        /*showProgressDialog(Constants.PLEASE_WAIT)
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
        }*/
    }
}