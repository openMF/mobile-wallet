package org.mifos.mobilewallet.mifospay.auth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mifos.mobile.passcode.utils.PassCodeConstants
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.auth.AuthContract
import org.mifos.mobilewallet.mifospay.auth.AuthContract.LoginView
import org.mifos.mobilewallet.mifospay.auth.presenter.LoginPresenter
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityLoginBinding
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Utils.hideSoftKeyboard
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity(), LoginView {

    @Inject
    lateinit var mPresenter: LoginPresenter
    lateinit var mLoginPresenter: AuthContract.LoginPresenter

    //ViewBinding
    private lateinit var binding: ActivityLoginBinding

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
                        //navigate sign up screen when implement compose navigation
                    })
                }
            }
        }

        val pref = PasscodePreferencesHelper(applicationContext)
        if (pref.passCode.isNotEmpty()) {
            startPassCodeActivity()
        }
    }

    override fun setPresenter(presenter: AuthContract.LoginPresenter?) {
        if (presenter != null) {
            mLoginPresenter = presenter
        }
    }

    fun onLoginClicked() {
        hideSoftKeyboard(this)
        showProgressDialog(Constants.LOGGING_IN)
        mLoginPresenter.loginUser(usernameContent, passwordContent)
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
}