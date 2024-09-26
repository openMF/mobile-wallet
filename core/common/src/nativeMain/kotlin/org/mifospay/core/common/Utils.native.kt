package org.mifospay.core.common

import platform.Foundation.NSNumberFormatter

actual class CurrencyFormatter {
    actual fun format(
        balance: Double?,
        currencyCode: String?,
        maximumFractionDigits: Int?,
    ): String {
        val numberFormatter = NSNumberFormatter()
        numberFormatter.numberStyle = NSNumberFormatterCurrencyStyle
        numberFormatter.currencyCode = currencyCode
        numberFormatter.maximumFractionDigits = maximumFractionDigits ?: 0
        return numberFormatter.stringFromNumber(balance ?: 0.0) ?: ""
    }
}