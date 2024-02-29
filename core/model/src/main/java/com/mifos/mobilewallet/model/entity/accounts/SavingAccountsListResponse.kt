package com.mifos.mobilewallet.model.entity.accounts

import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount

data class SavingAccountsListResponse(
    var savingsAccounts: List<SavingAccount> = ArrayList()
)
