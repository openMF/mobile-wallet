package org.mifos.mobilewallet.mifospay.kyc.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.home.ui.HomeActivity;
import org.mifos.mobilewallet.mifospay.kyc.KYCContract;
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class KYCActivity extends BaseActivity implements KYCContract.KYCView {

    @Inject
    KYCPresenter mPresenter;

    KYCContract.KYCPresenter mKYCPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_kyc);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        setToolbarTitle("KYC");

        replaceFragment(KYCDescriptionFragment.newInstance(), false, R.id.container_kyc);
    }

    @Override
    public void setPresenter(KYCContract.KYCPresenter presenter) {
        mKYCPresenter = presenter;
    }

}
