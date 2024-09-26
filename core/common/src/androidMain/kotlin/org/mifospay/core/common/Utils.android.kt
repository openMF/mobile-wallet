package org.mifospay.core.common

import java.text.NumberFormat
import java.util.Currency

actual class CurrencyFormatter {
    actual fun format(
        balance: Double?,
        currencyCode: String?,
        maximumFractionDigits: Int?,
    ): String {
        val balanceFormatter = NumberFormat.getCurrencyInstance()
        balanceFormatter.maximumFractionDigits = maximumFractionDigits ?: 0
        balanceFormatter.currency = Currency.getInstance(currencyCode)
        return balanceFormatter.format(balance)
    }
}