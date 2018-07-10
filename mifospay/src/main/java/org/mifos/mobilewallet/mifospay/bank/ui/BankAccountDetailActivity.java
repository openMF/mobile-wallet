package org.mifos.mobilewallet.mifospay.bank.ui;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.bank.presenter.BankAccountDetailPresenter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankAccountDetailActivity extends BaseActivity implements
        BankContract.BankAccountDetailView {

    @Inject
    BankAccountDetailPresenter mPresenter;
    BankContract.BankAccountDetailPresenter mBankAccountDetailPresenter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_detail);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        showBackButton();
        setToolbarTitle(Constants.BANK_ACCOUNT_DETAILS);
        mPresenter.attachView(this);

        bankAccountDetails = getIntent().getExtras().getParcelable(
                Constants.BANK_ACCOUNT_DETAILS);
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
        }
    }

    @Override
    public void setPresenter(BankContract.BankAccountDetailPresenter presenter) {
        mBankAccountDetailPresenter = presenter;
    }

    @OnClick({R.id.cv_change_upi_pin, R.id.cv_forgot_upi_pin, R.id.cv_delete_bank})
    public void onViewClicked(View view) {
        if (bankAccountDetails.isUpiEnabled()) {
            switch (view.getId()) {
                case R.id.cv_change_upi_pin:
                    break;
                case R.id.cv_forgot_upi_pin:
                    break;
                case R.id.cv_delete_bank:
                    break;
            }
        } else {
            Toaster.showToast(this, "Setup UPI PIN");
        }
    }

    @OnClick(R.id.btn_setup_upi_pin)
    public void onsetupUpiClicked() {

    }
}
