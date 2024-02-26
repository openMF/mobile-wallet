package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount
import com.mifos.mobilewallet.model.entity.client.ClientAccounts
import com.mifos.mobilewallet.model.domain.Account
import javax.inject.Inject

class AccountMapper @Inject constructor(private val currencyMapper: CurrencyMapper) {

    fun transform(clientAccounts: ClientAccounts?): List<Account> {
        val accountList = mutableListOf<Account>()

        if (clientAccounts != null
            && !clientAccounts.savingsAccounts.isNullOrEmpty()) {

            for (savingAccount in clientAccounts.savingsAccounts) {
                val account = Account().apply {
                    name = savingAccount.productName
                    number = savingAccount.accountNo
                    id = savingAccount.id
                    balance = savingAccount.accountBalance
                    currency = currencyMapper.transform(savingAccount.currency)
                    productId = savingAccount.productId.toLong()
                }

                accountList.add(account)
            }
        }
        return accountList
    }
}
