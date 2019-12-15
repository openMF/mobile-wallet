package org.mifos.mobilewallet.mifospay.registration.ui;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract;
import org.mifos.mobilewallet.mifospay.registration.presenter.InitiateRegistrationPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InitiateRegistrationActivity extends BaseActivity implements
        RegistrationContract.InitiateRegistrationView {

    @Inject
    InitiateRegistrationPresenter mPresenter;

    RegistrationContract.InitiateRegistrationPresenter mInitiateRegistrationPresenter;

    @BindView(R.id.ccp_countryCode)
    CountryCodePicker mCcpCountryCode;
    @BindView(R.id.et_mobile_number)
    EditText mEtMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_registration);

        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        setToolbarTitle("");
        showBackButton();

        mCcpCountryCode.registerCarrierNumberEditText(mEtMobileNumber);
    }

    @Override
    public void onRequestOtpSuccess() {
        hideProgressDialog();
        Intent intent = new Intent(InitiateRegistrationActivity.this,
                OtpVerificationActivity.class);

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

        intent.putExtra(Constants.COUNTRY, mCcpCountryCode.getSelectedCountryName());
        intent.putExtra(Constants.MOBILE_NUMBER, mCcpCountryCode.getFullNumber());

        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestOtpFailed(String s) {
        hideProgressDialog();
        showToast(s);
    }

    @Override
    public void showToast(String s) {
        Toaster.showToast(this, s);
    }

    @Override
    public void setPresenter(RegistrationContract.InitiateRegistrationPresenter presenter) {
        mInitiateRegistrationPresenter = presenter;
    }

    @OnClick(R.id.fab_next)
    public void onNextClicked() {
        Utils.hideSoftKeyboard(this);
        if (mCcpCountryCode.isValidFullNumber()) {
            showProgressDialog(Constants.SENDING_OTP_TO_YOUR_MOBILE_NUMBER);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInitiateRegistrationPresenter.requestOtpFromServer(
                            mCcpCountryCode.getFullNumber(), mEtMobileNumber.getText().toString());
                }
            }, 1500);

        } else {
            showToast("Enter a valid mobile number");
        }
    }

}
