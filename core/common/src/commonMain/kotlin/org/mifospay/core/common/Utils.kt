package org.mifospay.core.common

expect class CurrencyFormatter {
    fun format(balance: Double?, currencyCode: String?, maximumFractionDigits: Int?): String
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}