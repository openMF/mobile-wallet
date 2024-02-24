package com.mifos.mobilewallet.model.entity.client

import android.os.Parcel
import android.os.Parcelable
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount
import kotlinx.parcelize.Parcelize

@Parcelize
data class   ClientAccounts (
    var savingsAccounts: List<SavingAccount> = ArrayList())
    : Parcelable {

    fun withSavingsAccounts(savingsAccounts: List<SavingAccount>): ClientAccounts {
        this.savingsAccounts = savingsAccounts
        return this
    }

    val recurringSavingsAccounts: List<SavingAccount?>
        get() = getSavingsAccounts(true)
    val nonRecurringSavingsAccounts: List<SavingAccount?>
        get() = getSavingsAccounts(false)

    private fun getSavingsAccounts(wantRecurring: Boolean): List<SavingAccount?> {
        val result: MutableList<SavingAccount?> = ArrayList()
        if (savingsAccounts != null) {
            for (account in savingsAccounts!!) {
                if (account!!.isRecurring() == wantRecurring) {
                    result.add(account)
                }
            }
        }
        return result
    }

}
