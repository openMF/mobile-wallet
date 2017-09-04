package org.mifos.mobilewallet.auth.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.presenter.BusinessDetailsPresenter;
import org.mifos.mobilewallet.base.BaseActivity;
import org.mifos.mobilewallet.user.ui.VerifyPanDialog;
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

    @BindView(R.id.btn_add_pan)
    Button btnAddPan;

    @BindView(R.id.btn_add_aadhar)
    Button btnAadhar;

    @BindView(R.id.tv_pan)
    TextView tvPan;

    @BindView(R.id.tv_aadhar)
    TextView tvAadhar;

    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;

    @BindView(R.id.iv_pan_status)
    ImageView ivPanStatus;

    @BindView(R.id.iv_aadhar_status)
    ImageView ivAadharStatus;

    private ProgressDialog pDialog;
    private String panNumber;
    private String aadharNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_business_details);

        ButterKnife.bind(BusinessDetailsActivity.this);

        setToolbarTitle("Business details");
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

    @OnClick(R.id.btn_add_pan)
    public void addPanClicked() {
        VerifyPanDialog panDialog = new VerifyPanDialog();
        panDialog.show(getSupportFragmentManager(), "Pan dialog");
    }

    @OnClick(R.id.btn_add_aadhar)
    public void addAadharClicked() {
        VerifyAadharDialog aadharDialog = new VerifyAadharDialog();
        aadharDialog.show(getSupportFragmentManager(), "Aadhar dialog");
    }

    @Override
    public void setPresenter(AuthContract.BusinessDetailsPresenter presenter) {
        mBusinessDetailsPresenter = presenter;
    }

    @Override
    public void openAddAccount() {
        startActivity(new Intent(this, AddAccountActivity.class));
    }

    @Override
    public void showPanStatus(boolean status) {
        if (pDialog != null) {
            pDialog.hide();
        }
        Snackbar snackbar = Snackbar
                .make(rootLayout, "", Snackbar.LENGTH_SHORT);

        if (status) {
            snackbar.setText("PAN number successfully verified");
            ivPanStatus.setBackgroundResource(R.drawable.ic_done_green);
            tvPan.setText(panNumber);
        } else {
            snackbar.setText("Invalid PAN number");
            ivPanStatus.setBackgroundResource(R.drawable.ic_close);
            tvPan.setText(panNumber);
        }

        snackbar.show();

    }

    @Override
    public void showAadharOtpSent() {
        if (pDialog != null) {
            pDialog.hide();
        }
        SmsOtpDialog otpDialog = new SmsOtpDialog();
        otpDialog.show(getSupportFragmentManager(), "Otp dialog");

        tvAadhar.setText(aadharNumber);
        ivAadharStatus.setBackgroundResource(R.drawable.ic_done_green);
    }

    @Override
    public void showAadharValid(boolean status) {
        if (status) {
            if (pDialog != null) {
                pDialog.setMessage("Sending OTP to the mobile number linked with this Aadhar");
            }
            mBusinessDetailsPresenter.generateAadharOtp();
        } else {
            if (pDialog != null) {
                pDialog.hide();
            }
            Snackbar snackbar = Snackbar
                    .make(rootLayout, "", Snackbar.LENGTH_SHORT);
            snackbar.setText("Invalid Aadhar number");
            ivAadharStatus.setBackgroundResource(R.drawable.ic_close);
            tvAadhar.setText(aadharNumber);
            snackbar.show();

        }
    }


    @Override
    public void showAadharStatus(boolean status) {
        if (pDialog != null) {
            pDialog.hide();
        }
        Snackbar snackbar = Snackbar
                .make(rootLayout, "", Snackbar.LENGTH_SHORT);

        if (status) {
            snackbar.setText("Aadhar number successfully verified");
            ivAadharStatus.setBackgroundResource(R.drawable.ic_done_green);
            tvAadhar.setText(aadharNumber);
        } else {
            snackbar.setText("Unable to verify Aadhar number.");
            ivAadharStatus.setBackgroundResource(R.drawable.ic_close);
            tvAadhar.setText(aadharNumber);
        }

        snackbar.show();
    }

    public void verifyPan(String number) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
        }
        pDialog.setMessage("Verifying PAN...");
        pDialog.show();

        this.panNumber = number;
        mBusinessDetailsPresenter.verifyPan(number);
    }

    public void verifyAadhar(String number) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
        }

        pDialog.setMessage("Verifying Aadhar number");
        pDialog.show();

        this.aadharNumber = number;
        mBusinessDetailsPresenter.verifyAadhar(number);


    }
}
