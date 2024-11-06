/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.DataState
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.network.model.entity.templates.beneficiary.BeneficiaryTemplate

interface BeneficiaryRepository {
    suspend fun getBeneficiaryList(): Flow<DataState<List<Beneficiary>>>

    suspend fun getBeneficiaryTemplate(): Flow<DataState<BeneficiaryTemplate>>

    suspend fun createBeneficiary(beneficiaryPayload: BeneficiaryPayload): DataState<String>

    suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): DataState<String>

    suspend fun deleteBeneficiary(beneficiaryId: Long): DataState<String>
}
