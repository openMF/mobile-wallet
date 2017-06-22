package org.mifos.mobilewallet.data.local;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.domain.model.Bank;
import org.mifos.mobilewallet.home.domain.model.UserDetails;
import org.mifos.mobilewallet.invoice.domain.model.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by naman on 17/6/17.
 */

@Singleton
public class LocalRepository {

    private final PreferencesHelper preferencesHelper;

    @Inject
    public LocalRepository(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public UserDetails getUserDetails() {
        UserDetails details = new UserDetails();
        details.setName(preferencesHelper.getFullName());
        details.setEmail(preferencesHelper.getEmail());

        return details;
    }

    public List<Bank> getPopularBanks() {
        List<Bank> banks = new ArrayList<>();
        banks.add(new Bank("RBL Bank", R.drawable.logo_rbl, 0));
        banks.add(new Bank("SBI Bank", R.drawable.logo_sbi, 0));
        banks.add(new Bank("PNB Bank", R.drawable.logo_pnb, 0));
        banks.add(new Bank("HDFC Bank", R.drawable.logo_hdfc, 0));
        banks.add(new Bank("ICICI Bank", R.drawable.logo_icici, 0));
        banks.add(new Bank("AXIS Bank", R.drawable.logo_axis, 0));

        return banks;
    }

    public List<Bank> getOtherBanks() {
        List<Bank> banks = new ArrayList<>();
        banks.add(new Bank("Allahabad Bank", R.drawable.ic_account_balance, 1));
        banks.add(new Bank("Andra Bank", R.drawable.ic_account_balance, 1));
        banks.add(new Bank("Axis Bank", R.drawable.logo_axis, 1));
        banks.add(new Bank("Bank of Baroda", R.drawable.ic_account_balance, 1));
        banks.add(new Bank("HDFC Bank", R.drawable.logo_hdfc, 1));
        banks.add(new Bank("ICICI Bank", R.drawable.logo_icici, 1));
        banks.add(new Bank("PNB Bank", R.drawable.logo_pnb, 1));
        banks.add(new Bank("RBL Bank", R.drawable.logo_rbl, 1));
        banks.add(new Bank("SBI Bank", R.drawable.logo_sbi, 1));
        return banks;
    }

    public List<PaymentMethod> getPaymentMethods() {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(new PaymentMethod("Aadhar Pay", R.drawable.aadharpay, 1));
        paymentMethods.add(new PaymentMethod("UPI", R.drawable.bhim, 1));
        paymentMethods.add(new PaymentMethod("Credit/Debit card", R.drawable.debitcard, 2));
        paymentMethods.add(new PaymentMethod("Net Banking", R.drawable.netbanking, 1));
        paymentMethods.add(new PaymentMethod("Paytm", R.drawable.paytm, 3));
        return paymentMethods;
    }
}
