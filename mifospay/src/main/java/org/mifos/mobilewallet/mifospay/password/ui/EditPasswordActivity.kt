package org.mifos.mobilewallet.mifospay.password.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import butterknife.*
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract.EditPasswordView
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

class EditPasswordActivity : BaseActivity(), EditPasswordView {
    @JvmField
    @Inject
    var mPresenter: EditPasswordPresenter? = null
    private var mEditPasswordPresenter: EditPasswordContract.EditPasswordPresenter? = null

    @JvmField
    @BindView(R.id.et_edit_password_current)
    var etCurrentPassword: EditText? = null

    @JvmField
    @BindView(R.id.et_edit_password_new)
    var etNewPassword: EditText? = null

    @JvmField
    @BindView(R.id.et_edit_password_new_repeat)
    var etNewPasswordRepeat: EditText? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)
        activityComponent.inject(this)
        ButterKnife.bind(this)
        setupUi()
        mPresenter!!.attachView(this)
        disableSavePasswordButton()
    }

    @OnFocusChange(
        R.id.et_edit_password_current,
        R.id.et_edit_password_new,
        R.id.et_edit_password_new_repeat
    )
    fun onPasswordInputFocusChanged() {
        handlePasswordInputChanged()
    }

    @OnTextChanged(
        R.id.et_edit_password_current,
        R.id.et_edit_password_new,
        R.id.et_edit_password_new_repeat
    )
    fun onPasswordInputTextChanged() {
        handlePasswordInputChanged()
    }

    private fun handlePasswordInputChanged() {
        val currentPassword = etCurrentPassword!!.text.toString()
        val newPassword = etNewPassword!!.text.toString()
        val newPasswordRepeat = etNewPasswordRepeat!!.text.toString()
        mPresenter!!.handleSavePasswordButtonStatus(currentPassword, newPassword, newPasswordRepeat)
    }

    override fun enableSavePasswordButton() {
        findViewById<View>(R.id.btn_save).isEnabled = true
    }

    override fun disableSavePasswordButton() {
        findViewById<View>(R.id.btn_save).isEnabled = false
    }

    private fun setupUi() {
        showCloseButton()
        setToolbarTitle(getString(R.string.change_password))
    }

    @OnClick(R.id.btn_cancel)
    fun onCancelClicked() {
        closeActivity()
    }

    @OnClick(R.id.btn_save)
    fun onSaveClicked() {
        val currentPassword = etCurrentPassword!!.text.toString()
        val newPassword = etCurrentPassword!!.text.toString()
        val newPasswordRepeat = etCurrentPassword!!.text.toString()
        mPresenter!!.updatePassword(currentPassword, newPassword, newPasswordRepeat)
    }

    override fun setPresenter(presenter: EditPasswordContract.EditPasswordPresenter?) {
        mEditPasswordPresenter = presenter
    }

    override fun startProgressBar() {
        showProgressDialog(Constants.PLEASE_WAIT)
    }

    override fun stopProgressBar() {
        hideProgressDialog()
    }

    override fun showError(msg: String?) {
        Toaster.showToast(this, msg)
    }

    override fun closeActivity() {
        finish()
    }
}