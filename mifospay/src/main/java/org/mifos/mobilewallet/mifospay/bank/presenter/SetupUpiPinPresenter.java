package org.mifos.mobilewallet.mifospay.bank.presenter;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import javax.inject.Inject;

/**
 * Created by ankur on 16/July/2018
 */

public class SetupUpiPinPresenter implements BankContract.SetupUpiPinPresenter {

    BankContract.SetupUpiPinView mSetupUpiPinView;

    @Inject
    public SetupUpiPinPresenter() {
    }

    @Override
    public void attachView(BaseView baseView) {
        mSetupUpiPinView = (BankContract.SetupUpiPinView) baseView;
        mSetupUpiPinView.setPresenter(this);
    }

    @Override
    public void setupUpiPin(BankAccountDetails bankAccountDetails, String mSetupUpiPin) {
        // TODO:: Setup UPI PIN Api
        mSetupUpiPinView.setupUpiPinSuccess(mSetupUpiPin);

//        String message = "error";
//        mBankAccountDetailView.setupUpiPinError(message);
    }

    @Override
    public void requestOtp(BankAccountDetails bankAccountDetails) {
        String otp = "0000";
        mSetupUpiPinView.debitCardVerified(otp);

//        String message = "error";

    }
}
