package org.mifos.mobilewallet.mifospay.settings.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import butterknife.ButterKnife
import butterknife.OnClick
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.settings.SettingsContract
import org.mifos.mobilewallet.mifospay.settings.SettingsContract.SettingsView
import org.mifos.mobilewallet.mifospay.settings.presenter.SettingsPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DialogBox
import javax.inject.Inject

class SettingsActivity : BaseActivity(), SettingsView {
    private var dialogBox = DialogBox()

    @JvmField
    @Inject
    var mPresenter: SettingsPresenter? = null
    private var mSettingsPresenter: SettingsContract.SettingsPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        activityComponent.inject(this)
        ButterKnife.bind(this)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        setToolbarTitle(Constants.SETTINGS)
        mPresenter?.attachView(this)
    }

    override fun setPresenter(presenter: SettingsContract.SettingsPresenter?) {
        mSettingsPresenter = presenter
    }

    @OnClick(R.id.btn_logout)
    fun onLogoutClicked() {
        val builder = AlertDialog.Builder(this, R.style.AppTheme_Dialog)
        builder.setTitle(R.string.log_out_title)
        builder.setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, id ->
                showProgressDialog(Constants.LOGGING_OUT)
                mPresenter?.logout()
            }
            .setNegativeButton(R.string.no, null)
        val alert = builder.create()
        alert.show()
    }

    @OnClick(R.id.btn_disable_account)
    fun onDisableAccountClicked() {
        dialogBox.setOnPositiveListener { dialog, which -> mSettingsPresenter?.disableAccount() }
        dialogBox.setOnNegativeListener { dialog, which -> dialog.dismiss() }
        dialogBox.show(
            this, R.string.alert_disable_account,
            R.string.alert_disable_account_desc, R.string.ok, R.string.cancel
        )
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
}