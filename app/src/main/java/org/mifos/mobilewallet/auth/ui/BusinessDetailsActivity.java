package org.mifos.mobilewallet.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.presenter.BusinessDetailsPresenter;
import org.mifos.mobilewallet.auth.presenter.LandingPresenter;
import org.mifos.mobilewallet.core.BaseActivity;
import org.mifos.mobilewallet.utils.widgets.DiscreteSlider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by naman on 20/6/17.
 */

public class BusinessDetailsActivity extends BaseActivity
        implements AuthContract.BusinessDetailsView {


    @Inject
    BusinessDetailsPresenter mPresenter;

    AuthContract.BusinessDetailsPresenter mBusinessDetailsPresenter;

    @BindView((R.id.discrete_slider))
    DiscreteSlider slider;

    @BindView(R.id.btn_owner)
    Button btnOwner;

    @BindView(R.id.btn_agent)
    Button btnAgent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_business_details);

        ButterKnife.bind(BusinessDetailsActivity.this);

        setToolbarTitle("Business/Shop details");
        showBackButton();
        mPresenter.attachView(this);

        slider.getSeekBar().setEnabled(false);


    }

    @OnClick(R.id.btn_owner)
    public void ownerClicked() {
        btnOwner.setBackgroundResource(R.drawable.button_round_primary);
        btnAgent.setBackgroundResource(R.drawable.button_round_stroke);
        btnOwner.setTextColor(getResources().getColor(android.R.color.white));
        btnAgent.setTextColor(getResources().getColor(android.R.color.black));

    }

    @OnClick(R.id.btn_agent)
    public void agentClicked() {
        btnOwner.setBackgroundResource(R.drawable.button_round_stroke);
        btnAgent.setBackgroundResource(R.drawable.button_round_primary);
        btnOwner.setTextColor(getResources().getColor(android.R.color.black));
        btnAgent.setTextColor(getResources().getColor(android.R.color.white));

    }

    @OnClick(R.id.btn_next)
    public void nextClicked() {
        mBusinessDetailsPresenter.registerDetails();
    }

    @Override
    public void setPresenter(AuthContract.BusinessDetailsPresenter presenter) {
        mBusinessDetailsPresenter = presenter;
    }

    @Override
    public void openAddAccount() {
        startActivity(new Intent(this, AddAccountActivity.class));
    }
}
