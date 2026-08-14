/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.lib.loan.providers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mifos.lib.loan.core.LoanProvider
import org.mifos.lib.loan.core.model.LoanState
import org.mifos.lib.loan.core.model.LoanTemplate
import org.mifos.lib.loan.core.model.LoansPayload
import org.mifos.lib.loan.mapper.toDto
import org.mifos.lib.loan.mapper.toModel
import org.mifos.lib.loan.network.LoansService
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow

/**
 * The real, Fineract-backed [LoanProvider] adapter. Registered in [org.mifos.lib.loan.providers.LoanProviderRegistry]
 * under the `"fineract"` key for future activation once a real per-provider backend exists — it is
 * not reachable from today's static 4-item Select Loan Provider list.
 */
class FineractLoanProvider(
    private val loansService: LoansService,
    private val ioDispatcher: CoroutineDispatcher,
) : LoanProvider {

    override fun getLoanTemplate(clientId: Long): Flow<DataState<LoanTemplate>> {
        return loansService.getLoanTemplate(clientId)
            .map { it.toModel() }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getLoanTemplateByProduct(
        clientId: Long,
        productId: Long,
    ): Flow<DataState<LoanTemplate>> {
        return loansService.getLoanTemplateByProduct(clientId, productId)
            .map { it.toModel() }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    // Note: [loanState] and [loanId] are kept for interface symmetry with mifos-mobile's
    // CREATE/UPDATE distinction, but this port only ever submits new loan applications, so
    // an UPDATE path (updateLoanAccount) is intentionally not wired up here.
    override suspend fun submitLoanApplication(
        loanState: LoanState,
        payload: LoansPayload,
        loanId: Long,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                loansService.createLoansAccount(payload.toDto())
            }
            DataState.Success("Loan application submitted successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
