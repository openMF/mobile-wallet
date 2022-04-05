package org.mifos.mobilewallet.mifospay.bank.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.bank.fragment.DebitCardFragment;
import org.mifos.mobilewallet.mifospay.bank.fragment.OtpFragment;
import org.mifos.mobilewallet.mifospay.bank.fragment.UpiPinFragment;
import org.mifos.mobilewallet.mifospay.bank.presenter.SetupUpiPinPresenter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.utils.AnimationUtil;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetupUpiPinActivity extends BaseActivity implements BankContract.SetupUpiPinView {

    @Inject
    SetupUpiPinPresenter mPresenter;
    BankContract.SetupUpiPinPresenter mSetupUpiPinPresenter;
    @BindView(R.id.fl_debit_card)
    FrameLayout mFlDebitCard;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fl_otp)
    FrameLayout mFlOtp;
    @BindView(R.id.fl_upi_pin)
    FrameLayout mFlUpiPin;
    @BindView(R.id.cv_debit_card)
    CardView mCvDebitCard;
    @BindView(R.id.tv_debit_card)
    TextView mTvDebitCard;
    @BindView(R.id.tv_otp)
    TextView mTvOtp;
    @BindView(R.id.tv_upi)
    TextView mTvUpi;

    private BankAccountDetails bankAccountDetails;
    private int index;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_upi_pin);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        setToolbarTitle(Constants.SETUP_UPI_PIN);
        mPresenter.attachView(this);

        Bundle b = getIntent().getExtras();

        bankAccountDetails = b.getParcelable(Constants.BANK_ACCOUNT_DETAILS);
        index = b.getInt(Constants.INDEX);
        type = b.getString(Constants.TYPE);

        if (type.equals(Constants.CHANGE)) {
            mSetupUpiPinPresenter.requestOtp(bankAccountDetails);
            mFlDebitCard.setVisibility(View.GONE);
            mCvDebitCard.setVisibility(View.GONE);
            setToolbarTitle(Constants.CHANGE_UPI_PIN);
        } else if (type.equals(Constants.FORGOT)) {
            mFlDebitCard.setVisibility(View.VISIBLE);
            mCvDebitCard.setVisibility(View.VISIBLE);
            setToolbarTitle(Constants.FORGOT_UPI_PIN);
        } else {
            mFlDebitCard.setVisibility(View.VISIBLE);
            mCvDebitCard.setVisibility(View.VISIBLE);
            setToolbarTitle(Constants.SETUP_UPI_PIN);
        }

        addFragment(new DebitCardFragment(), R.id.fl_debit_card);
    }

    @Override
    public void setPresenter(BankContract.SetupUpiPinPresenter presenter) {
        mSetupUpiPinPresenter = presenter;
    }

    @Override
    public void debitCardVerified(String otp) {
        mTvDebitCard.setVisibility(View.VISIBLE);
        addFragment(OtpFragment.newInstance(otp), R.id.fl_otp);
        AnimationUtil.collapse(mFlDebitCard);
        AnimationUtil.expand(mFlOtp);
    }

    public void otpVerified() {
        mTvOtp.setVisibility(View.VISIBLE);
        addFragment(new UpiPinFragment(), R.id.fl_upi_pin);
        AnimationUtil.expand(mFlUpiPin);
        AnimationUtil.collapse(mFlOtp);
    }

    public void upiPinEntered(String upiPin) {
        replaceFragment(UpiPinFragment.newInstance(1, upiPin), false, R.id.fl_upi_pin);
    }

    public void upiPinConfirmed(String upiPin) {
        mTvUpi.setVisibility(View.VISIBLE);
        showProgressDialog(Constants.SETTING_UP_UPI_PIN);
        mSetupUpiPinPresenter.setupUpiPin(bankAccountDetails, upiPin);
    }

    @Override
    public void setupUpiPinSuccess(String mSetupUpiPin) {
        bankAccountDetails.setUpiEnabled(true);
        bankAccountDetails.setUpiPin(mSetupUpiPin);
        hideProgressDialog();
        showToast(Constants.UPI_PIN_SETUP_COMPLETED_SUCCESSFULLY);
        Intent intent = new Intent();
        intent.putExtra(Constants.UPDATED_BANK_ACCOUNT, bankAccountDetails);
        intent.putExtra(Constants.INDEX, index);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void setupUpiPinError(String message) {
        hideProgressDialog();
        showToast(Constants.ERROR_WHILE_SETTING_UP_UPI_PIN);
    }

    public void showToast(String message) {
        Toaster.showToast(this, message);
    }

}
