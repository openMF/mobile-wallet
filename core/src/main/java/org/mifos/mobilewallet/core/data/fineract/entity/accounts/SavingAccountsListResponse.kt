package org.mifos.mobilewallet.core.data.fineract.entity.accounts

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount
import kotlin.collections.ArrayList

data class SavingAccountsListResponse(
        var savingsAccounts: List<SavingAccount>? = ArrayList())