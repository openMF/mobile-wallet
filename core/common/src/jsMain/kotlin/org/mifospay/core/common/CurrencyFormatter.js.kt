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
        if (balance == null || currencyCode == null) {
            return ""
        }

        val options = js("{}").unsafeCast<dynamic>()
        options.style = "currency"
        options.currency = currencyCode
        if (maximumFractionDigits != null) {
            options.maximumFractionDigits = maximumFractionDigits
        }

        return try {
            js("new Intl.NumberFormat('en-US', options).format(balance)").toString()
        } catch (e: Exception) {
            console.error("Error formatting currency: ${e.message}")
            balance.toString()
        }
    }
}
