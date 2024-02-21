package org.mifos.mobilewallet.mifospay.bank.presenter

import com.mifos.mobilewallet.model.domain.BankAccountDetails
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.SetupUpiPinView
import org.mifos.mobilewallet.mifospay.base.BaseView
import javax.inject.Inject

/**
 * Created by ankur on 16/July/2018
 */
class SetupUpiPinPresenter @Inject constructor() : BankContract.SetupUpiPinPresenter {
    var mSetupUpiPinView: SetupUpiPinView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mSetupUpiPinView = baseView as SetupUpiPinView?
        mSetupUpiPinView!!.setPresenter(this)
    }

    override fun setupUpiPin(bankAccountDetails: BankAccountDetails?, mSetupUpiPin: String?) {
        // TODO:: Setup UPI PIN Api
        mSetupUpiPinView!!.setupUpiPinSuccess(mSetupUpiPin)

//        String message = "error";
//        mBankAccountDetailView.setupUpiPinError(message);
    }

    override fun requestOtp(bankAccountDetails: BankAccountDetails?) {
        val otp = "0000"
        mSetupUpiPinView!!.debitCardVerified(otp)

//        String message = "error";
    }
}