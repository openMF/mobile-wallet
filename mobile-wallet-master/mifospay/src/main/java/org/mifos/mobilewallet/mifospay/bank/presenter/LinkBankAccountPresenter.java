package org.mifos.mobilewallet.mifospay.bank.presenter;

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.Random;

import javax.inject.Inject;

/**
 * Created by ankur on 09/July/2018
 */

public class LinkBankAccountPresenter implements BankContract.LinkBankAccountPresenter {

    BankContract.LinkBankAccountView mLinkBankAccountView;

    @Inject
    public LinkBankAccountPresenter() {

    }

    @Override
    public void attachView(BaseView baseView) {
        mLinkBankAccountView = (BankContract.LinkBankAccountView) baseView;
        mLinkBankAccountView.setPresenter(this);
    }

    @Override
    public void fetchBankAccountDetails(final String bankName) {
        // TODO:: UPI API implement
        mLinkBankAccountView.addBankAccount(
                new BankAccountDetails(bankName, "Ankur Sharma", "New Delhi",
                        new Random().nextInt() + " ", "Savings"));
    }
}
