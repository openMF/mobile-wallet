package org.mifos.mobilewallet.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.presenter.SetupCompletePresenter;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.home.ui.HomeActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 20/6/17.
 */

public class SetupCompleteActivity extends BaseActivity implements AuthContract.SetupCompleteView {

    @Inject
    SetupCompletePresenter mPresenter;

    AuthContract.SetupCompletePresenter mSetupCompletePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_setup_complete);

        ButterKnife.bind(SetupCompleteActivity.this);

        setToolbarTitle("Account details");
        showBackButton();
        mPresenter.attachView(this);

    }

    @OnClick(R.id.btn_start_accepting)
    public void startAcceptingClicked() {
        mSetupCompletePresenter.navigateHome();
    }

    @Override
    public void setPresenter(AuthContract.SetupCompletePresenter presenter) {
        this.mSetupCompletePresenter = presenter;
    }

    @Override
    public void openHome() {
        Intent intent = new Intent(SetupCompleteActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
