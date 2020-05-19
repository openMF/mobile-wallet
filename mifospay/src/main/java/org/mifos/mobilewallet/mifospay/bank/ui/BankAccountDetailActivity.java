package org.mifos.mobilewallet.mifospay.bank.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.bank.presenter.BankAccountDetailPresenter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankAccountDetailActivity extends BaseActivity implements
        BankContract.BankAccountDetailView {

    public static final int SETUP_UPI_REQUEST_CODE = 2;

    @Inject
    BankAccountDetailPresenter mPresenter;
    BankContract.BankAccountDetailPresenter mBankAccountDetailPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_bank_name)
    TextView mTvBankName;
    @BindView(R.id.tv_account_holder_name)
    TextView mTvAccountHolderName;
    @BindView(R.id.tv_branch)
    TextView mTvBranch;
    @BindView(R.id.tv_ifsc)
    TextView mTvIfsc;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.btn_setup_upi_pin)
    Button mBtnSetupUpiPin;
    @BindView(R.id.cv_change_upi_pin)
    CardView mCvChangeUpiPin;
    @BindView(R.id.cv_forgot_upi_pin)
    CardView mCvForgotUpiPin;
    @BindView(R.id.cv_delete_bank)
    CardView mCvDeleteBank;

    private BankAccountDetails bankAccountDetails;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_detail);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        setToolbarTitle(Constants.BANK_ACCOUNT_DETAILS);
        mPresenter.attachView(this);

        bankAccountDetails = getIntent().getExtras().getParcelable(Constants.BANK_ACCOUNT_DETAILS);
        index = getIntent().getExtras().getInt(Constants.INDEX);
        if (bankAccountDetails != null) {

            if (bankAccountDetails.isUpiEnabled()) {
                mBtnSetupUpiPin.setVisibility(View.GONE);
            } else {
                mBtnSetupUpiPin.setVisibility(View.VISIBLE);
            }

            mTvBankName.setText(bankAccountDetails.getBankName());
            mTvAccountHolderName.setText(bankAccountDetails.getAccountholderName());
            mTvBranch.setText(bankAccountDetails.getBranch());
            mTvIfsc.setText(bankAccountDetails.getIfsc());
            mTvType.setText(bankAccountDetails.getType());

        } else {
            finish();
        }
    }

    @Override
    public void setPresenter(BankContract.BankAccountDetailPresenter presenter) {
        mBankAccountDetailPresenter = presenter;
    }

    @OnClick(R.id.btn_setup_upi_pin)
    public void onSetupUpiPinClicked() {
        startSetupActivity(Constants.SETUP, index);
    }

    @OnClick(R.id.cv_change_upi_pin)
    public void onChangeUpiPinClicked() {
        if (bankAccountDetails.isUpiEnabled()) {
            startSetupActivity(Constants.CHANGE, index);
        } else {
            showToast(Constants.SETUP_UPI_PIN);
        }
    }

    @OnClick(R.id.cv_forgot_upi_pin)
    public void onForgotUpiPinClicked() {
        if (bankAccountDetails.isUpiEnabled()) {
            startSetupActivity(Constants.FORGOT, index);
        } else {
            showToast(Constants.SETUP_UPI_PIN);
        }
    }

    private void startSetupActivity(String type, int index) {
        Intent intent = new Intent(BankAccountDetailActivity.this, SetupUpiPinActivity.class);
        intent.putExtra(Constants.BANK_ACCOUNT_DETAILS, bankAccountDetails);
        intent.putExtra(Constants.TYPE, type);
        intent.putExtra(Constants.INDEX, index);
        startActivityForResult(intent, SETUP_UPI_REQUEST_CODE);
    }

    public void showToast(String message) {
        Toaster.showToast(this, message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugUtil.log("rescode ", resultCode);
        if (requestCode == SETUP_UPI_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            DebugUtil.log("bundle", bundle);
            if (bundle != null) {
                bankAccountDetails = bundle.getParcelable(Constants.UPDATED_BANK_ACCOUNT);
                index = bundle.getInt(Constants.INDEX);
                if (bankAccountDetails.isUpiEnabled()) {
                    mBtnSetupUpiPin.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.UPDATED_BANK_ACCOUNT, bankAccountDetails);
        intent.putExtra(Constants.INDEX, index);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }
}