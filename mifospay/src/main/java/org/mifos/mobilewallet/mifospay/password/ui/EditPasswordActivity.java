package org.mifos.mobilewallet.mifospay.password.ui;

import android.os.Bundle;
import android.widget.EditText;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract;
import org.mifos.mobilewallet.mifospay.password.presenter.EditPasswordPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        showProgressDialog(Constants.PLEASE_WAIT);
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
    public void closeActivity() {
        finish();
    }
}