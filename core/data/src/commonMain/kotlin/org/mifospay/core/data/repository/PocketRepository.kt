/*
 * Copyright 2026 Mifos Initiative
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
import org.mifospay.core.model.payload.PocketLinkPayload
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.model.pocket.PocketAccount

interface PocketRepository {

    suspend fun getPocketAccounts(): DataState<List<PocketAccount>>

    fun getDetailedPocketAccounts(
        clientId: Long,
        forceRefresh: Boolean = false,
    ): Flow<DataState<List<DetailedPocketAccount>>>

    fun getAvailableAccountsToLink(
        clientId: Long,
    ): Flow<DataState<List<LinkableAccount>>>

    suspend fun linkAccounts(
        payload: PocketLinkPayload,
        explicitlyAddedAccounts: List<DetailedPocketAccount>,
        clientId: Long,
    ): DataState<Unit>

    suspend fun delinkAccounts(
        pocketAccountMappingIds: List<Long>,
        clientId: Long,
    ): DataState<Unit>
}
