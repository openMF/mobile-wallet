package mifos.org.mobilewallet.core.data.fineract.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import mifos.org.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount;
import mifos.org.mobilewallet.core.data.fineract.entity.client.ClientAccounts;
import mifos.org.mobilewallet.core.domain.model.Account;

/**
 * Created by naman on 11/7/17.
 */

@Singleton
public class AccountMapper {

    @Inject
    CurrencyMapper currencyMapper;

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
                account.setId(savingAccount.getId());
                account.setBalance(savingAccount.getAccountBalance());
                account.setCurrency(currencyMapper.transform(savingAccount.getCurrency()));

                accountList.add(account);
            }


        }
        return accountList;
    }

}
