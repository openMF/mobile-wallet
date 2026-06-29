/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.mifos.loans.core

import org.mifospay.core.common.DataState
import org.mifospay.mifos.loans.core.model.Loan
import org.mifospay.mifos.loans.core.model.LoanPayment
import org.mifospay.mifos.loans.core.model.LoanProduct
import org.mifospay.mifos.loans.core.model.LoanSchedule

interface LoanProvider {

    suspend fun getProducts(): DataState<List<LoanProduct>>

    suspend fun getLoanDetails(
        loanId: Long,
    ): DataState<Loan>

    suspend fun applyForLoan(
        request: Loan,
    ): DataState<Loan>

    suspend fun makePayment(
        payment: LoanPayment,
    ): DataState<LoanSchedule>
}
