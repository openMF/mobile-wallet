package org.mifos.mobilewallet.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.presenter.BankAccountPresenter;
import org.mifos.mobilewallet.auth.presenter.BusinessDetailsPresenter;
import org.mifos.mobilewallet.core.BaseActivity;
import org.mifos.mobilewallet.utils.widgets.DiscreteSlider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 20/6/17.
 */

public class BankAccountActivity extends BaseActivity implements AuthContract.BankAccountView {


    @Inject
    BankAccountPresenter mPresenter;

    AuthContract.BankAccountPresenter mBankAccountPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_bank_account);

        ButterKnife.bind(BankAccountActivity.this);

        setToolbarTitle("Bank Acocunt");
        showBackButton();
        mPresenter.attachView(this);

    }

    @OnClick(R.id.btn_set_up_pin)
    public void setupPINClicked(){
        mBankAccountPresenter.setUPIPin();
    }

    @OnClick(R.id.btn_have_pin)
    public void havePINClicked(){
        mBankAccountPresenter.setUPIPin();
    }

    @OnClick(R.id.btn_later_pin)
    public void setupLaterPINClicked(){
        mBankAccountPresenter.setUPIPin();
    }

    @Override
    public void setPresenter(AuthContract.BankAccountPresenter presenter) {
        this.mBankAccountPresenter = presenter;
    }

    @Override
    public void setupComplete() {
        startActivity(new Intent(this, SetupCompleteActivity.class));
    }
}
