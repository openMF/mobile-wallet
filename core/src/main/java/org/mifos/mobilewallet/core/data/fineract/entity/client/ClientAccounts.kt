package org.mifos.mobilewallet.core.data.fineract.entity.client

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingAccount
import java.util.*

@Parcelize
class ClientAccounts : Parcelable {
    var savingsAccounts: List<SavingAccount>? = ArrayList()

    val recurringSavingsAccounts: List<SavingAccount>
        get() = getSavingsAccounts(true)

    val nonRecurringSavingsAccounts: List<SavingAccount>
        get() = getSavingsAccounts(false)

    fun withSavingsAccounts(savingsAccounts: List<SavingAccount>): ClientAccounts {
        this.savingsAccounts = savingsAccounts
        return this
    }

    private fun getSavingsAccounts(wantRecurring: Boolean): List<SavingAccount> {
        val result: MutableList<SavingAccount> = ArrayList()
        savingsAccounts?.let {
            for(account in it) {
                if (account.isRecurring() == wantRecurring) {
                    result.add(account)
                }
            }
        }
        return result
    }

}