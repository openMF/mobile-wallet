package org.mifos.mobilewallet.mifospay.registration.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract;
import org.mifos.mobilewallet.mifospay.registration.presenter.MobileVerificationPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MobileVerificationActivity extends BaseActivity implements
        RegistrationContract.MobileVerificationView {

    @Inject
    MobileVerificationPresenter mPresenter;

    RegistrationContract.MobileVerificationPresenter mMobileVerificationPresenter;

    @BindView(R.id.ccp_code)
    CountryCodePicker mCcpCode;
    @BindView(R.id.et_mobile_number)
    EditText mEtMobileNumber;
    @BindView(R.id.btn_get_otp)
    TextView mBtnGetOtp;
    @BindView(R.id.et_otp)
    EditText mEtOtp;
    @BindView(R.id.fab_next)
    FloatingActionButton mFabNext;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_verifying_otp)
    TextView mTvVerifyingOtp;
    @BindView(R.id.ccp_country)
    CountryCodePicker mCcpCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);

        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        setToolbarTitle("");
        showBackButton();

        mCcpCode.registerCarrierNumberEditText(mEtMobileNumber);
        mCcpCountry.setCustomMasterCountries("IN,US");
    }

    /**
     * This function sets the presenter.
     * @param presenter This is the presenter that is set.
     */
    @Override
    public void setPresenter(RegistrationContract.MobileVerificationPresenter presenter) {
        mMobileVerificationPresenter = presenter;
    }

    /**
     * This function requests OTP from the server if the mobile number is valid,
     * else asks for a valid mobile number.
     */
    @OnClick(R.id.btn_get_otp)
    public void onGetOTp() {
        Utils.hideSoftKeyboard(this);
        if (mCcpCode.isValidFullNumber()) {
            showProgressDialog(Constants.SENDING_OTP_TO_YOUR_MOBILE_NUMBER);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMobileVerificationPresenter.requestOTPfromServer(mCcpCode.getFullNumber(),
                            mEtMobileNumber.getText().toString());
                }
            }, 1500);

        } else {
            showToast("Enter a valid mobile number");
        }
    }

    /**
     * This function displays the components of the presenter on OTP success.
     */
    @Override
    public void onRequestOtpSuccess() {
        hideProgressDialog();
        mEtMobileNumber.setClickable(false);
        mEtMobileNumber.setFocusableInTouchMode(false);
        mEtMobileNumber.setFocusable(false);
        mCcpCode.setCcpClickable(false);

        mEtOtp.setVisibility(View.VISIBLE);
        mBtnGetOtp.setClickable(false);
        mBtnGetOtp.setBackgroundResource(R.drawable.ic_done);
        mFabNext.setVisibility(View.VISIBLE);
    }

    /**
     * This function show a toast when its fails to request an OTP
     * @param s This is the message that is displayed on the toast.
     */
    @Override
    public void onRequestOtpFailed(String s) {
        hideProgressDialog();
        showToast(s);
    }

    /**
     * This function is implemented when Next is clicked.
     */
    @OnClick(R.id.fab_next)
    public void onNextClicked() {
        Utils.hideSoftKeyboard(this);

        mFabNext.setClickable(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mTvVerifyingOtp.setVisibility(View.VISIBLE);
        mEtOtp.setClickable(false);
        mEtOtp.setFocusableInTouchMode(false);
        mEtOtp.setFocusable(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMobileVerificationPresenter.verifyOTP(mEtOtp.getText().toString());
            }
        }, 1500);
    }

    /**
     * This functions starts a new activity on verification success.
     */
    @Override
    public void onOtpVerificationSuccess() {
        Intent intent = new Intent(MobileVerificationActivity.this, SignupActivity.class);

        intent.putExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID,
                getIntent().getIntExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, 0));
        intent.putExtra(Constants.GOOGLE_PHOTO_URI,
                getIntent().getParcelableExtra(Constants.GOOGLE_PHOTO_URI));
        intent.putExtra(Constants.GOOGLE_DISPLAY_NAME,
                getIntent().getStringExtra(Constants.GOOGLE_DISPLAY_NAME));
        intent.putExtra(Constants.GOOGLE_EMAIL,
                getIntent().getStringExtra(Constants.GOOGLE_EMAIL));
        intent.putExtra(Constants.GOOGLE_FAMILY_NAME,
                getIntent().getStringExtra(Constants.GOOGLE_FAMILY_NAME));
        intent.putExtra(Constants.GOOGLE_GIVEN_NAME,
                getIntent().getStringExtra(Constants.GOOGLE_GIVEN_NAME));

        intent.putExtra(Constants.COUNTRY, mCcpCountry.getSelectedCountryName());
        intent.putExtra(Constants.MOBILE_NUMBER, mCcpCode.getFullNumber());

        startActivity(intent);
        finish();
    }

    /**
     * This function shows a toast if the verification fails.
     * @param s This is the message to be displayed on the toast.
     */
    @Override
    public void onOtpVerificationFailed(String s) {
        mFabNext.setClickable(true);
        mProgressBar.setVisibility(View.GONE);
        mTvVerifyingOtp.setVisibility(View.GONE);
        mEtOtp.setClickable(true);
        mEtOtp.setFocusableInTouchMode(true);
        mEtOtp.setFocusable(true);

        showToast(s);
    }

    /**
     * This function shows a toast message
     * @param message This is the message that is shown on the toast.
     */
    @Override
    public void showToast(String message) {
        Toaster.showToast(this, message);
    }
}
