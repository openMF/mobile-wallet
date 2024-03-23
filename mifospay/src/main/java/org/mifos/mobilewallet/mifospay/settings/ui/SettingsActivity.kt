package org.mifos.mobilewallet.mifospay.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.settings.SettingsContract
import org.mifos.mobilewallet.mifospay.settings.SettingsContract.SettingsView
import org.mifos.mobilewallet.mifospay.settings.presenter.SettingsPresenter
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.feature.auth.login.LoginActivity
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity(), SettingsView {

    @Inject
    lateinit var mPresenter: SettingsPresenter

    private var mSettingsPresenter: SettingsContract.SettingsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter.attachView(this)
        setContent {
            MifosTheme {
                SettingsScreen(
                    backPress = { backToHome() },
                    disable = { onDisableAccountClicked() },
                    logout = { onLogoutClicked() }
                )
            }
        }
    }

    private fun onLogoutClicked() {
        showProgressDialog(Constants.LOGGING_OUT)
        mPresenter.logout()
    }

    private fun onDisableAccountClicked() {
        mSettingsPresenter?.disableAccount()
    }

    private fun backToHome() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun startLoginActivity() {
        hideProgressDialog()
        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        startActivity(intent)
    }

    override fun setPresenter(presenter: SettingsContract.SettingsPresenter?) {
        mSettingsPresenter = presenter
    }
}