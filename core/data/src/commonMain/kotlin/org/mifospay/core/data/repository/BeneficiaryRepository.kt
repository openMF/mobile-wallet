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
import org.mifospay.core.common.Result
import org.mifospay.core.model.entity.beneficary.Beneficiary
import org.mifospay.core.model.entity.beneficary.BeneficiaryPayload
import org.mifospay.core.model.entity.beneficary.BeneficiaryUpdatePayload
import org.mifospay.core.model.entity.templates.beneficiary.BeneficiaryTemplate
import org.mifospay.core.network.model.CommonResponse

interface BeneficiaryRepository {
    fun getBeneficiaryList(): Flow<Result<List<Beneficiary>>>

    fun getBeneficiaryTemplate(): Flow<Result<BeneficiaryTemplate>>

    fun createBeneficiary(beneficiaryPayload: BeneficiaryPayload): Flow<Result<CommonResponse>>

    fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): Flow<Result<CommonResponse>>

    fun deleteBeneficiary(beneficiaryId: Long): Flow<Result<CommonResponse>>
}
