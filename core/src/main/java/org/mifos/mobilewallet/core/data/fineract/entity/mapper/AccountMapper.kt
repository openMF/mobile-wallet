package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts
import org.mifos.mobilewallet.core.domain.model.Account
import java.util.*
import javax.inject.Inject

/**
 * Created by naman on 11/7/17.
 */
class AccountMapper @Inject constructor() {

    @Inject
    lateinit var currencyMapper: CurrencyMapper

    fun transform(clientAccounts: ClientAccounts): List<Account> {
        val accountList: MutableList<Account> = ArrayList()
        clientAccounts.savingsAccounts?.let {
            if (it.isNotEmpty()) {
                for (savingAccount in clientAccounts.savingsAccounts!!) {
                    val account = Account()
                    account.name = savingAccount.productName
                    account.number = savingAccount.accountNo
                    account.id = savingAccount.id
                    account.balance = savingAccount.accountBalance!!
                    account.currency = savingAccount.currency?.let {
                        mCurrency -> currencyMapper!!.transform(mCurrency)
                    }
                    account.productId = savingAccount.productId!!.toLong()
                    accountList.add(account)
                }
            }
        }
        return accountList
    }
}