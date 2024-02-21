package org.mifos.mobilewallet.core.data.fineract.entity.mapper;

import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount;
import com.mifos.mobilewallet.model.entity.client.ClientAccounts;
import com.mifos.mobilewallet.model.domain.Account;

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
                account.setProductId(savingAccount.getProductId());

                accountList.add(account);
            }


        }
        return accountList;
    }

}
