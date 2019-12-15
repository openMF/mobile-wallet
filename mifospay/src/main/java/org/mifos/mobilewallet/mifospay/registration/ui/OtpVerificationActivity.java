package org.mifos.mobilewallet.mifospay.registration.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.widget.EditText;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract;
import org.mifos.mobilewallet.mifospay.registration.presenter.OtpVerificationPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.OtpDigitsWatcher;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OtpVerificationActivity extends BaseActivity implements
        RegistrationContract.OtpVerificationView {

    @Inject
    OtpVerificationPresenter mPresenter;

    RegistrationContract.OtpVerificationPresenter mOtpVerificationPresenter;

    @BindView(R.id.otpDigit1)
    EditText mOtpDigit1;
    @BindView(R.id.otpDigit2)
    EditText mOtpDigit2;
    @BindView(R.id.otpDigit3)
    EditText mOtpDigit3;
    @BindView(R.id.otpDigit4)
    EditText mOtpDigit4;
    @BindView(R.id.otpDigit5)
    EditText mOtpDigit5;
    @BindView(R.id.otpDigit6)
    EditText mOtpDigit6;
    @BindView(R.id.fab_next)
    FloatingActionButton mFabNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        setToolbarTitle("");
        showBackButton();

        mOtpDigit1.addTextChangedListener(new OtpDigitsWatcher(null, mOtpDigit1, mOtpDigit2));
        mOtpDigit2.addTextChangedListener(new OtpDigitsWatcher(mOtpDigit1, mOtpDigit2, mOtpDigit3));
        mOtpDigit3.addTextChangedListener(new OtpDigitsWatcher(mOtpDigit2, mOtpDigit3, mOtpDigit4));
        mOtpDigit4.addTextChangedListener(new OtpDigitsWatcher(mOtpDigit3, mOtpDigit4, mOtpDigit5));
        mOtpDigit5.addTextChangedListener(new OtpDigitsWatcher(mOtpDigit4, mOtpDigit5, mOtpDigit6));
        mOtpDigit6.addTextChangedListener(new OtpDigitsWatcher(mOtpDigit5, mOtpDigit6, null));
    }


    @Override
    public void setPresenter(RegistrationContract.OtpVerificationPresenter presenter) {
        mOtpVerificationPresenter = presenter;
    }

    public String getOtpFromEditText() {
        String otpDigit1 = mOtpDigit1.getText().toString();
        String otpDigit2 = mOtpDigit2.getText().toString();
        String otpDigit3 = mOtpDigit3.getText().toString();
        String otpDigit4 = mOtpDigit4.getText().toString();
        String otpDigit5 = mOtpDigit5.getText().toString();
        String otpDigit6 = mOtpDigit6.getText().toString();
        return (otpDigit1 + otpDigit2 + otpDigit3 + otpDigit4 + otpDigit5 + otpDigit6);
    }

    @OnClick(R.id.fab_next)
    public void onNextClicked() {
        final String otp = getOtpFromEditText();
        if (otp.length() == 6) {
            showProgressDialog(Constants.VERIFYING_OTP_PLEASE_WAIT);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOtpVerificationPresenter.verifyOTP(otp);
                }
            }, 1500);
        } else {
            showToast("Please enter a valid OTP");
        }
    }

    @Override
    public void onOtpVerificationSuccess() {
        Intent intent = new Intent(OtpVerificationActivity.this, SignupActivity.class);

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
        intent.putExtra(Constants.COUNTRY,
                getIntent().getStringExtra(Constants.COUNTRY));
        intent.putExtra(Constants.MOBILE_NUMBER,
                getIntent().getStringExtra(Constants.MOBILE_NUMBER));

        startActivity(intent);
        finish();
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(this, message);
    }

    @Override
    public void onOtpVerificationFailed(String s) {
        hideProgressDialog();
        showToast("Please enter correct OTP");
    }

}
