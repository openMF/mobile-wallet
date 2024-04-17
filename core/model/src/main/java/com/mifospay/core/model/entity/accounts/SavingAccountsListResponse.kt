package com.mifospay.core.model.entity.accounts

import com.mifospay.core.model.entity.accounts.savings.SavingAccount

data class SavingAccountsListResponse(
    var savingsAccounts: List<SavingAccount> = ArrayList()
)
