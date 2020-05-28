package org.mifos.mobilewallet.mifospay.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import java.text.NumberFormat
import java.util.*

object Utils {

    @JvmStatic
    fun hideSoftKeyboard(activity: Activity) {
        val view = activity.currentFocus
        val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    @JvmStatic
    fun Int.dp2px() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    @JvmStatic
    fun String.isBlank() = this.isEmpty() || indices.all { this[it].isWhitespace() }

    @JvmStatic
    fun getFormattedAccountBalance(balance: Double?, currencyCode: String?): String {
        val accountBalanceFormatter = NumberFormat.getCurrencyInstance()
        accountBalanceFormatter.maximumFractionDigits = 0
        accountBalanceFormatter.currency = Currency.getInstance(currencyCode)
        return accountBalanceFormatter.format(balance)
    }

}