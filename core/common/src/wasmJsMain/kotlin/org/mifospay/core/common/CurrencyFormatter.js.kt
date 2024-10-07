/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.common

actual object CurrencyFormatter {
    actual fun format(
        balance: Double?,
        currencyCode: String?,
        maximumFractionDigits: Int?,
    ): String {
        return "$currencyCode ${
            balance?.let {
                val formattedBalance = balance.toString()
                val fractionDigits = formattedBalance.substringAfterLast(".")
                val fractionDigitsLength = fractionDigits.length
                val fractionDigitsToDisplay = if (fractionDigitsLength > maximumFractionDigits!!) {
                    fractionDigits.substring(0, maximumFractionDigits)
                } else {
                    fractionDigits
                }
                val integerDigits = formattedBalance.substringBeforeLast(".")
                val integerDigitsWithCommas =
                    integerDigits.reversed().chunked(3).joinToString(",").reversed()
                "$integerDigitsWithCommas.$fractionDigitsToDisplay"
            } ?: "0.00"
        }"
    }
}
