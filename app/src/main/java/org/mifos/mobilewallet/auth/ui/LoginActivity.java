package org.mifos.mobilewallet.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.presenter.LoginPresenter;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.home.ui.HomeActivity;
import org.mifos.mobilewallet.utils.Utils;

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
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFail(String message) {
        Utils.hideSoftKeyboard(this);
        hideProgressDialog();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
