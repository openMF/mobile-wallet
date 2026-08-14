/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.lib.loan.repository

import kotlinx.coroutines.flow.Flow
import org.mifos.lib.loan.core.model.LoanState
import org.mifos.lib.loan.core.model.LoanTemplate
import org.mifos.lib.loan.core.model.LoansPayload
import org.mifospay.core.common.DataState

/**
 * Facade injected by the wizard's data-consuming ViewModels ([org.mifos.lib.loan.ui.selectLoanType.SelectLoanTypeViewModel],
 * [org.mifos.lib.loan.ui.loanProductDetails.LoanProductDetailsViewModel],
 * [org.mifos.lib.loan.ui.loanApply.LoanApplyViewModel], [org.mifos.lib.loan.ui.confirmDetails.ConfirmDetailsViewModel])
 * instead of [org.mifos.lib.loan.core.LoanProvider] directly.
 *
 * Each call takes the `providerId` chosen on the Select Loan Provider screen and resolves the
 * concrete [org.mifos.lib.loan.core.LoanProvider] adapter to delegate to via
 * [org.mifos.lib.loan.providers.LoanProviderRegistry], so the same wizard behaves differently per
 * provider once distinct adapters exist for each.
 */
interface LoansRepository {
    fun getLoanTemplate(providerId: String, clientId: Long): Flow<DataState<LoanTemplate>>

    fun getLoanTemplateByProduct(
        providerId: String,
        clientId: Long,
        productId: Long,
    ): Flow<DataState<LoanTemplate>>

    suspend fun submitLoanApplication(
        providerId: String,
        loanState: LoanState,
        payload: LoansPayload,
        loanId: Long,
    ): DataState<String>
}
