/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.lib.loan.repository

import kotlinx.coroutines.flow.Flow
import org.mifos.lib.loan.core.model.LoanState
import org.mifos.lib.loan.core.model.LoanTemplate
import org.mifos.lib.loan.core.model.LoansPayload
import org.mifos.lib.loan.providers.LoanProviderRegistry
import org.mifospay.core.common.DataState

/**
 * Default [LoansRepository] implementation — resolves the concrete [org.mifos.lib.loan.core.LoanProvider]
 * for the given `providerId` via [registry] and delegates every call to it.
 */
class LoansRepositoryImpl(
    private val registry: LoanProviderRegistry,
) : LoansRepository {

    override fun getLoanTemplate(providerId: String, clientId: Long): Flow<DataState<LoanTemplate>> =
        registry.resolve(providerId).getLoanTemplate(clientId)

    override fun getLoanTemplateByProduct(
        providerId: String,
        clientId: Long,
        productId: Long,
    ): Flow<DataState<LoanTemplate>> =
        registry.resolve(providerId).getLoanTemplateByProduct(clientId, productId)

    override suspend fun submitLoanApplication(
        providerId: String,
        loanState: LoanState,
        payload: LoansPayload,
        loanId: Long,
    ): DataState<String> =
        registry.resolve(providerId).submitLoanApplication(loanState, payload, loanId)
}
