package org.mifos.mobilewallet.data.fineract.entity.mapper;

import org.mifos.mobilewallet.account.domain.model.Account;
import org.mifos.mobilewallet.data.fineract.entity.accounts.savings.SavingAccount;
import org.mifos.mobilewallet.data.fineract.entity.client.Client;
import org.mifos.mobilewallet.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.home.domain.model.ClientDetails;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by naman on 11/7/17.
 */

@Singleton
public class AccountMapper {

    @Inject
    public AccountMapper() {}

    public List<Account> transform(ClientAccounts clientAccounts) {
        List<Account> accountList = new ArrayList<>();

        if (clientAccounts != null && clientAccounts.getSavingsAccounts() != null
                && clientAccounts.getSavingsAccounts().size() != 0) {

            for (SavingAccount savingAccount : clientAccounts.getSavingsAccounts()) {
                Account account = new Account();
                account.setName(savingAccount.getProductName());
                account.setNumber(savingAccount.getAccountNo());

                accountList.add(account);
            }


        }
        return accountList;
    }

}
