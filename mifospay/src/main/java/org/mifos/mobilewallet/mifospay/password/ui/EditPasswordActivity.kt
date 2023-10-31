package org.mifos.mobilewallet.mifospay.password.ui

import android.os.Bundle
import android.view.View
import butterknife.OnFocusChange
import butterknife.OnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityEditPasswordBinding
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract.EditPasswordView
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

@AndroidEntryPoint
class EditPasswordActivity : BaseActivity(), EditPasswordView {
    @JvmField
    @Inject
    var mPresenter: EditPasswordPresenter? = null
    private var mEditPasswordPresenter: EditPasswordContract.EditPasswordPresenter? = null

    private lateinit var binding: ActivityEditPasswordBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUi()
        mPresenter?.attachView(this)
        disableSavePasswordButton()

        binding.btnCancel.setOnClickListener {
            onCancelClicked()
        }

        binding.btnSave.setOnClickListener {
            onSaveClicked()
        }

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
        val currentPassword = binding.etEditPasswordCurrent.text.toString()
        val newPassword = binding.etEditPasswordNew.text.toString()
        val newPasswordRepeat = binding.etEditPasswordNewRepeat.text.toString()
        mPresenter?.handleSavePasswordButtonStatus(currentPassword, newPassword, newPasswordRepeat)
    }

    override fun enableSavePasswordButton() {
        findViewById<View>(R.id.btn_save).isEnabled = true
    }

    override fun disableSavePasswordButton() {
        binding.btnSave.isEnabled = false
    }

    private fun setupUi() {
        showCloseButton()
        setToolbarTitle(getString(R.string.change_password))
    }

    fun onCancelClicked() {
        closeActivity()
    }

    fun onSaveClicked() {
        val currentPassword = binding.etEditPasswordCurrent.text.toString()
        val newPassword = binding.etEditPasswordCurrent.text.toString()
        val newPasswordRepeat = binding.etEditPasswordCurrent.text.toString()
        mPresenter?.updatePassword(currentPassword, newPassword, newPasswordRepeat)
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