/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.common

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import java.text.NumberFormat
import java.util.Currency

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
    fun getFormattedAccountBalance(balance: Double?, currencyCode: String?, maximumFractionDigits: Int? = 0): String {
        val accountBalanceFormatter = NumberFormat.getCurrencyInstance()
        accountBalanceFormatter.maximumFractionDigits = maximumFractionDigits ?: 0
        accountBalanceFormatter.currency = Currency.getInstance(currencyCode)
        return accountBalanceFormatter.format(balance)
    }

    fun <T> List<T>.toArrayList(): ArrayList<T> {
        val array: ArrayList<T> = ArrayList()
        for (index in this) array.add(index)
        return array
    }
}
