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
import org.mifospay.core.model.kyc.KYCLevel1Details

interface KycLevelRepository {
    fun fetchKYCLevel1Details(clientId: Long): Flow<DataState<KYCLevel1Details?>>

    suspend fun addKYCLevel1Details(
        clientId: Long,
        kycLevel1Details: KYCLevel1Details,
    ): DataState<String>

    suspend fun updateKYCLevel1Details(
        clientId: Long,
        kycLevel1Details: KYCLevel1Details,
    ): DataState<String>
}
