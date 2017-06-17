package org.mifos.mobilewallet.auth.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.EditText;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.domain.usecase.AuthenticateUser;
import org.mifos.mobilewallet.auth.presenter.LoginPresenter;
import org.mifos.mobilewallet.core.BaseActivity;

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

        mLoginPresenter.authenticateUser(etUsername.getText().toString(), etPassword.getText().toString());
    }
}
