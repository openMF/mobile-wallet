/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.ScreenState
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.network.model.entity.templates.beneficiary.BeneficiaryTemplate

interface BeneficiaryRepository {
    // Phase-3 cutover — reads on ScreenState.
    suspend fun getBeneficiaryList(): Flow<ScreenState<List<Beneficiary>>>

    suspend fun getBeneficiaryTemplate(): Flow<ScreenState<BeneficiaryTemplate>>

    // Writes execute the network call and throw on failure (no offline write
    // stores yet). Callers surface errors via their own try/catch.
    suspend fun createBeneficiary(beneficiaryPayload: BeneficiaryPayload)

    suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    )

    suspend fun deleteBeneficiary(beneficiaryId: Long)
}
