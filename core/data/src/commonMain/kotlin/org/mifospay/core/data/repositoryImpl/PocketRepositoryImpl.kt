/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.model.pocket.Pocket
import org.mifospay.core.model.pocket.PocketDelinkPayload
import org.mifospay.core.model.pocket.PocketLinkPayload
import org.mifospay.core.network.SelfServiceApiManager

class PocketRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : PocketRepository {

    override fun getPocket(): Flow<DataState<Pocket>> =
        apiManager.pocketApi.getPockets()
            .map<Pocket, DataState<Pocket>> { DataState.Success(it) }
            .onStart { emit(DataState.Loading) }
            .catch { emit(DataState.Error(it)) }
            .flowOn(ioDispatcher)

    override fun linkAccount(accountId: Long, accountType: String): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            apiManager.pocketApi.linkAccounts(payload = PocketLinkPayload(accountId, accountType))
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(ioDispatcher)

    override fun delinkAccount(accountId: Long): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            apiManager.pocketApi.delinkAccounts(payload = PocketDelinkPayload(accountId))
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }.flowOn(ioDispatcher)
}
