package org.mifos.mobilewallet.core.data.fineract.entity.mapper;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount;
import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import org.mifos.mobilewallet.core.domain.model.Account;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by naman on 11/7/17.
 */

public class AccountMapper {

    @Inject
    CurrencyMapper currencyMapper;

    @Inject
    public AccountMapper() {
    }

    public List<Account> transform(ClientAccounts clientAccounts) {
        List<Account> accountList = new ArrayList<>();

        if (clientAccounts != null && clientAccounts.getSavingsAccounts() != null
                && clientAccounts.getSavingsAccounts().size() != 0) {

            for (SavingAccount savingAccount : clientAccounts.getSavingsAccounts()) {
                Account account = new Account();
                account.setName(savingAccount.getProductName());
                account.setNumber(savingAccount.getAccountNo());
                account.setId(savingAccount.getId());
                account.setBalance(savingAccount.getAccountBalance());
                account.setCurrency(currencyMapper.transform(savingAccount.getCurrency()));
                account.setProductId((long)savingAccount.getProductId());

                accountList.add(account);
            }


        }
        return accountList;
    }

}
