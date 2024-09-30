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
import org.mifospay.core.model.entity.kyc.KYCLevel1Details
import org.mifospay.core.network.model.GenericResponse

interface KycLevelRepository {
    suspend fun fetchKYCLevel1Details(clientId: Int): Flow<Result<List<KYCLevel1Details>>>

    suspend fun addKYCLevel1Details(
        clientId: Int,
        kycLevel1Details: KYCLevel1Details,
    ): Flow<Result<GenericResponse>>

    suspend fun updateKYCLevel1Details(
        clientId: Int,
        kycLevel1Details: KYCLevel1Details,
    ): Flow<Result<GenericResponse>>
}
