package com.mifos.mobilewallet.model.entity.accounts;

import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount;

import java.util.ArrayList;
import java.util.List;

public class SavingAccountsListResponse {

    private List<SavingAccount> savingsAccounts = new ArrayList<>();

    public List<SavingAccount> getSavingsAccounts() {
        return savingsAccounts;
    }

    public void setAccounts(List<SavingAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }
}
