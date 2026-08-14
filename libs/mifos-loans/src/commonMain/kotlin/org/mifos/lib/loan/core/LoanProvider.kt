/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.lib.loan.core

import kotlinx.coroutines.flow.Flow
import org.mifos.lib.loan.core.model.LoanState
import org.mifos.lib.loan.core.model.LoanTemplate
import org.mifos.lib.loan.core.model.LoansPayload
import org.mifospay.core.common.DataState

interface LoanProvider {
    fun getLoanTemplate(clientId: Long): Flow<DataState<LoanTemplate>>

    fun getLoanTemplateByProduct(clientId: Long, productId: Long): Flow<DataState<LoanTemplate>>

    suspend fun submitLoanApplication(
        loanState: LoanState,
        payload: LoansPayload,
        loanId: Long,
    ): DataState<String>
}
