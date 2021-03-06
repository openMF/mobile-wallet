package org.mifos.mobilewallet.mifospay.bank.presenter;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.bank.BankContract;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.Calendar;

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
        int expiryMonth = Integer.parseInt(s1);
        int expiryYear = Integer.parseInt(s2);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if ((s.length() < 12) || (s.length() > 19)) {
            mDebitCardView.verifyDebitCardError(getContext()
                    .getString(R.string.debit_card_error_message), 1);
        } else if (expiryMonth < 01 || expiryMonth > 12) {
            mDebitCardView.verifyDebitCardError(getContext()
                    .getString(R.string.expiry_month_error_message), 2);
        } else if (expiryYear < currentYear || expiryYear > currentYear + 10) {
            mDebitCardView.verifyDebitCardError(getContext()
                    .getString(R.string.expiry_year_error_message), 3);
        } else if (expiryMonth < currentMonth && expiryYear <= currentYear) {
            mDebitCardView.verifyDebitCardError(getContext()
                    .getString(R.string.expiry_month_error_message), 2);
        } else {
            mDebitCardView.verifyDebitCardSuccess(otp);
        }
    }
}
