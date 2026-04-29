/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.transfer.intrabank.confirm

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransferConfirmStateTest {

    @Test
    fun amountIsValidReturnsFalseForZeroAmount() {
        val state = TransferConfirmState(
            amount = "0",
            selectedAccountBalance = 100.0,
        )

        assertFalse(state.amountIsValid)
    }

    @Test
    fun amountIsValidReturnsFalseForNegativeAmount() {
        val state = TransferConfirmState(
            amount = "-1",
            selectedAccountBalance = 100.0,
        )

        assertFalse(state.amountIsValid)
    }

    @Test
    fun amountIsValidReturnsTrueForPositiveAmountWithinBalance() {
        val state = TransferConfirmState(
            amount = "10.50",
            selectedAccountBalance = 100.0,
        )

        assertTrue(state.amountIsValid)
    }

    @Test
    fun amountIsValidReturnsFalseWhenAmountExceedsBalance() {
        val state = TransferConfirmState(
            amount = "101",
            selectedAccountBalance = 100.0,
        )

        assertFalse(state.amountIsValid)
    }

    @Test
    fun amountIsValidReturnsFalseForNonNumericAmount() {
        val state = TransferConfirmState(
            amount = "abc",
            selectedAccountBalance = 100.0,
        )

        assertFalse(state.amountIsValid)
    }
}
