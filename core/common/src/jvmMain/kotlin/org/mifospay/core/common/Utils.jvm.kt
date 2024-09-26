package org.mifospay.core.common

import java.text.NumberFormat
import java.util.Currency

actual class CurrencyFormatter {
    actual fun format(
        balance: Double?,
        currencyCode: String?,
        maximumFractionDigits: Int?,
    ): String {
        val numberFormat = NumberFormat.getCurrencyInstance()
        numberFormat.maximumFractionDigits = maximumFractionDigits ?: 0
        numberFormat.currency = Currency.getInstance(currencyCode)
        return numberFormat.format(balance)
    }
}