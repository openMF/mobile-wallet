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

object CreditCardUtils {
    fun validateCreditCardNumber(str: String): Boolean {
        if (str.isEmpty()) {
            return false
        }
        val ints = IntArray(str.length)
        for (i in str.indices) {
            ints[i] = str.substring(i, i + 1).toInt()
        }
        run {
            var i = ints.size - 2
            while (i >= 0) {
                var j = ints[i]
                j *= 2
                if (j > 9) {
                    j = j % 10 + 1
                }
                ints[i] = j
                i -= 2
            }
        }
        var sum = 0
        for (i in ints.indices) {
            sum += ints[i]
        }
        return sum % 10 == 0
    }
}
