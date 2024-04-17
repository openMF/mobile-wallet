package org.mifospay.core.data.fineract.entity.mapper

import com.mifospay.core.model.entity.client.ClientAccounts
import com.mifospay.core.model.domain.Account
import javax.inject.Inject

class AccountMapper @Inject constructor(private val currencyMapper: CurrencyMapper) {

    fun transform(clientAccounts: ClientAccounts?): List<Account> {
        val accountList = mutableListOf<Account>()

        clientAccounts?.savingsAccounts?.forEach { savingAccount ->
            val account = Account(
                name = savingAccount.productName,
                number = savingAccount.accountNo,
                id = savingAccount.id,
                balance = savingAccount.accountBalance,
                currency = currencyMapper.transform(savingAccount.currency),
                productId = savingAccount.productId.toLong(),
            )
            accountList.add(account)
        }
        return accountList
    }
}
