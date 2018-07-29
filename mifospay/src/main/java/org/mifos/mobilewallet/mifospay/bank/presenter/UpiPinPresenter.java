package org.mifos.mobilewallet.mifospay.bank.presenter;

import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import javax.inject.Inject;

/**
 * Created by ankur on 13/July/2018
 */

public class UpiPinPresenter implements BankContract.UpiPinPresenter {

    BankContract.UpiPinView mUpiPinView;

    @Inject
    public UpiPinPresenter() {
    }

    @Override
    public void attachView(BaseView baseView) {
        mUpiPinView = (BankContract.UpiPinView) baseView;
    }
}
