package org.mifos.mobilewallet.mifospay.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
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
    fun getFormattedAccountBalance(balance: Double?, currencySymbol: String?): String {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.CEILING
        val roundedBalance = df.format(balance)
        return "$currencySymbol $roundedBalance"
    }

    @JvmStatic
    fun getFormattedDate(unformattedDate: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val output = SimpleDateFormat("dd MMMM yyyy")
        val date = sdf.parse(unformattedDate)
        return output.format(date)
    }

}