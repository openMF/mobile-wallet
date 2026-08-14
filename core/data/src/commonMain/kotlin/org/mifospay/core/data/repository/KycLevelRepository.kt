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

import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.model.kyc.KYCLevel1Details

interface KycLevelRepository {
    // Phase-3 cutover — reads on ScreenState.
    // `null` payload is legal here (client has not yet submitted KYC); screens
    // treat Content(null) as "empty form" — not Empty (which would render a
    // blocking empty-state message).
    fun fetchKYCLevel1Details(clientId: Long): ScreenStateStream<KYCLevel1Details?>

    // Writes stay on DataState (Phase-3 D1).
    suspend fun addKYCLevel1Details(
        clientId: Long,
        kycLevel1Details: KYCLevel1Details,
    ): DataState<String>

    suspend fun updateKYCLevel1Details(
        clientId: Long,
        kycLevel1Details: KYCLevel1Details,
    ): DataState<String>
}
