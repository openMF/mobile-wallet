package org.mifos.mobilewallet.mifospay.bank.presenter;

import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import javax.inject.Inject;

/**
 * Created by ankur on 09/July/2018
 */

public class BankAccountDetailPresenter implements BankContract.BankAccountDetailPresenter {

    BankContract.BankAccountDetailView mBankAccountDetailView;

    @Inject
    public BankAccountDetailPresenter() {

    }

    @Override
    public void attachView(BaseView baseView) {
        mBankAccountDetailView = (BankContract.BankAccountDetailView) baseView;
        mBankAccountDetailView.setPresenter(this);
    }
}
