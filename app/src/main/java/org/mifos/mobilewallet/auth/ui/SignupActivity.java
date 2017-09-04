package org.mifos.mobilewallet.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.presenter.SignupPresenter;
import org.mifos.mobilewallet.base.BaseActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 16/6/17.
 */

public class SignupActivity extends BaseActivity implements AuthContract.SignupView {

    @Inject
    SignupPresenter mPresenter;

    AuthContract.SignupPresenter mSignupPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_verify_number);

        ButterKnife.bind(SignupActivity.this);
        mPresenter.attachView(this);
    }

    @OnClick(R.id.btn_send_sms)
    public void sendSms() {
        SmsOtpDialog otpDialog = new SmsOtpDialog();
        otpDialog.show(getSupportFragmentManager(), "Otp dialog");

//        mSignupPresenter.onVerifyNumber();
    }

    @OnClick(R.id.txt_signin)
    public void signinClicked() {
        mSignupPresenter.navigateLogin();
    }

    @Override
    public void setPresenter(AuthContract.SignupPresenter presenter) {
        mSignupPresenter = presenter;
    }

    @Override
    public void openAddDetails() {
        startActivity(new Intent(this, BusinessDetailsActivity.class));
    }

    @Override
    public void openLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
