package org.mifos.mobilewallet.mifospay.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.settings.SettingsContract
import org.mifos.mobilewallet.mifospay.settings.SettingsContract.SettingsView
import org.mifos.mobilewallet.mifospay.settings.presenter.SettingsPresenter
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DialogBox
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity(), SettingsView {
    var dialogBox = DialogBox()

    @JvmField
    @Inject
    var mPresenter: SettingsPresenter? = null
    var mSettingsPresenter: SettingsContract.SettingsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val builder = AlertDialog.Builder(this, R.style.AppTheme_Dialog)
        builder.setTitle(R.string.log_out_title)
        builder.setCancelable(false)
            .setPositiveButton(R.string.yes) { _, _ ->
                showProgressDialog(Constants.LOGGING_OUT)
                mPresenter?.logout()
            }
            .setNegativeButton(R.string.no, null)
        val alert = builder.create()
        alert.show()
    }

    private fun onDisableAccountClicked() {
        dialogBox.setPositiveListener { _, _ -> mSettingsPresenter?.disableAccount() }
        dialogBox.setNegativeListener { dialog, _ -> dialog.dismiss() }
        dialogBox.show(
            this, R.string.alert_disable_account,
            R.string.alert_disable_account_desc, R.string.ok, R.string.cancel
        )
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