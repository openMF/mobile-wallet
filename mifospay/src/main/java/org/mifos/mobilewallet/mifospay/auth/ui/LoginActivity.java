package org.mifos.mobilewallet.mifospay.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import com.mifos.mobile.passcode.utils.PassCodeConstants;
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.AuthContract;
import org.mifos.mobilewallet.mifospay.auth.presenter.LoginPresenter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 16/6/17.
 */

public class LoginActivity extends BaseActivity implements AuthContract.LoginView {

    @Inject
    LoginPresenter mPresenter;

    AuthContract.LoginPresenter mLoginPresenter;

    @BindView(R.id.et_username)
    EditText etUsername;

    @BindView(R.id.et_password)
    EditText etPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        PasscodePreferencesHelper pref = new PasscodePreferencesHelper(getApplicationContext());
        if (!pref.getPassCode().isEmpty()) {
            startPassCodeActivity();
        }

    }

    @Override
    public void setPresenter(AuthContract.LoginPresenter presenter) {
        mLoginPresenter = presenter;
    }

    @OnClick(R.id.btn_login)
    public void onLoginClicked() {
        Utils.hideSoftKeyboard(this);
        showProgressDialog("Logging in..");
        mLoginPresenter.loginUser(etUsername.getText().toString(),
                etPassword.getText().toString());
    }

    @Override
    public void loginSuccess() {
        hideProgressDialog();
        Utils.hideSoftKeyboard(this);
        startPassCodeActivity();
    }

    @Override
    public void loginFail(String message) {
        Utils.hideSoftKeyboard(this);
        hideProgressDialog();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts {@link PassCodeActivity} with {@code Constans.INTIAL_LOGIN} as true
     */
    private void startPassCodeActivity() {
        Intent intent = new Intent(LoginActivity.this, PassCodeActivity.class);
        intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true);
        startActivity(intent);
        finish();
    }
}
