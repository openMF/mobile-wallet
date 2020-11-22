package org.mifos.mobilewallet.mifospay.password.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract;
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordPresenter;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class EditPasswordActivity extends BaseActivity implements
        EditPasswordContract.EditPasswordView {

    @Inject
    EditPasswordPresenter mPresenter;
    EditPasswordContract.EditPasswordPresenter mEditPasswordPresenter;

    @BindView(R.id.et_edit_password_current)
    EditText etCurrentPassword;

    @BindView(R.id.et_edit_password_new)
    EditText etNewPassword;

    @BindView(R.id.et_edit_password_new_repeat)
    EditText etNewPasswordRepeat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        setupUi();
        mPresenter.attachView(this);

        disableSavePasswordButton();
    }

    @OnFocusChange({R.id.et_edit_password_current, R.id.et_edit_password_new,
            R.id.et_edit_password_new_repeat})
    public void onPasswordInputFocusChanged() {
        handlePasswordInputChanged();
    }

    @OnTextChanged({R.id.et_edit_password_current, R.id.et_edit_password_new,
            R.id.et_edit_password_new_repeat})
    public void onPasswordInputTextChanged() {
        handlePasswordInputChanged();
    }

    public void handlePasswordInputChanged() {
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String newPasswordRepeat = etNewPasswordRepeat.getText().toString();
        mPresenter.handleSavePasswordButtonStatus(currentPassword, newPassword, newPasswordRepeat);
    }

    @Override
    public void enableSavePasswordButton() {
        findViewById(R.id.btn_save).setEnabled(true);
    }

    @Override
    public void disableSavePasswordButton() {
        findViewById(R.id.btn_save).setEnabled(false);
    }

    private void setupUi() {
        showCloseButton();
        setToolbarTitle(getString(R.string.change_password));
    }

    @OnClick(R.id.btn_cancel)
    public void onCancelClicked() {
        closeActivity();
    }

    @OnClick(R.id.btn_save)
    public void onSaveClicked() {
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etCurrentPassword.getText().toString();
        String newPasswordRepeat = etCurrentPassword.getText().toString();
        mPresenter.updatePassword(currentPassword, newPassword, newPasswordRepeat);
    }

    @Override
    public void setPresenter(EditPasswordContract.EditPasswordPresenter presenter) {
        mEditPasswordPresenter = presenter;
    }

    @Override
    public void startProgressBar() {
        showProgressDialog(getString(R.string.please_wait));
    }

    @Override
    public void stopProgressBar() {
        hideProgressDialog();
    }

    @Override
    public void showError(String msg) {
        Toaster.showToast(this, msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void closeActivity() {
        finish();
    }
}