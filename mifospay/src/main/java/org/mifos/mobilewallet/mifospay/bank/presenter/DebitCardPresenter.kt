package org.mifos.mobilewallet.mifospay.bank.presenter;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import javax.inject.Inject;

import static org.mifos.mobilewallet.mifospay.MifosPayApp.getContext;

/**
 * Created by ankur on 13/July/2018
 */

public class DebitCardPresenter implements BankContract.DebitCardPresenter {

    BankContract.DebitCardView mDebitCardView;

    @Inject
    public DebitCardPresenter() {

    }

    @Override
    public void attachView(BaseView baseView) {
        mDebitCardView = (BankContract.DebitCardView) baseView;
        mDebitCardView.setPresenter(this);
    }

    @Override
    public void verifyDebitCard(String s, String s1, String s2) {
        String otp = "0000";
        if ((s.length() < 12) || (s.length() > 19)) {
            mDebitCardView.verifyDebitCardError(getContext()
                    .getString(R.string.debit_card_error_message));
        } else {
            mDebitCardView.verifyDebitCardSuccess(otp);
        }
    }
}
