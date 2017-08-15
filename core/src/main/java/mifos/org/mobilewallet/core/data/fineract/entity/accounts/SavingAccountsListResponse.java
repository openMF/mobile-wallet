package mifos.org.mobilewallet.core.data.fineract.entity.accounts;

import java.util.ArrayList;
import java.util.List;

import mifos.org.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount;

public class SavingAccountsListResponse {

    private List<SavingAccount> savingsAccounts = new ArrayList<>();

    public List<SavingAccount> getSavingsAccounts() {
        return savingsAccounts;
    }

    public void setAccounts(List<SavingAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }
}
