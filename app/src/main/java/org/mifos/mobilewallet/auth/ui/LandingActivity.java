package org.mifos.mobilewallet.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.presenter.LandingPresenter;
import org.mifos.mobilewallet.base.BaseActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 16/6/17.
 */

public class LandingActivity extends BaseActivity implements AuthContract.LandingView {

    @Inject
    LandingPresenter mPresenter;

    AuthContract.LandingPresenter mLandingPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_landing);

        ButterKnife.bind(LandingActivity.this);
        mPresenter.attachView(this);

    }

    @Override
    public void setPresenter(AuthContract.LandingPresenter presenter) {
        mLandingPresenter = presenter;
    }

    @Override
    public void openLoginScreen() {
        Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void openSignupScreen() {
        Intent intent = new Intent(LandingActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_login)
    public void onLoginClicked() {
        mLandingPresenter.navigateLogin();
    }

    @OnClick(R.id.btn_signup)
    public void onSignupClicked() {
        mLandingPresenter.navigateSignup();
    }
}
