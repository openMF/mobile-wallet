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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kpt.core.base.store.screen.ScreenDataStream
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.NewClient
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.client.ClientAccountsEntity

interface ClientRepository {

    // Phase-3 cutover — streaming reads on ScreenState.
    fun getClientInfo(clientId: Long): Flow<ScreenState<Client>>

    /**
     * Phase-5 Batch-2 **SINGLE-ROW-PER-KEY read** for the `clientDetail` archetype
     * (GOAL D13) — returns an offline-first `Flow<ScreenState<Client>>` consumed
     * through the `clientDetail` Store5
     * [`Store`][org.mobilenativefoundation.store.store5.Store]
     * (`createStore` + Room
     * [`SourceOfTruth`][org.mobilenativefoundation.store.store5.SourceOfTruth]
     * + single-row upsert writer) via
     * [`Store.asScreenStream`][kpt.core.base.store.screen.asScreenStream].
     *
     * The store is driven by
     * [`FetchPolicy.CACHE_FIRST_SWR`][kpt.core.base.store.screen.FetchPolicy.CACHE_FIRST_SWR]
     * (Phase-5 T10 default). Distinct from the transitional
     * [`asScreenStateFlow`][org.mifospay.core.common.asScreenStateFlow] the
     * legacy [getClientInfo] path uses; both signatures return
     * `Flow<ScreenState<Client>>` so a consumer can swap between them without
     * any downstream shape change.
     *
     * ### Write path (GOAL D1)
     *
     * This is a READ path. Profile mutation writes ([updateClient],
     * [updateClientImage]) continue to flow through their existing online
     * paths and appear here on the next refresh cycle. [getClientImage]
     * remains on the transitional shim — its blob-URL caching story is
     * out of scope for this batch.
     *
     * @param clientId Fineract client id (SoT key).
     * @param scope Coroutine scope for the stream's internal helper coroutines
     *   (typically `viewModelScope`).
     */
    fun getClientInfoStream(
        clientId: Long,
        scope: CoroutineScope,
    ): ScreenDataStream<Client>

    suspend fun getClients(): Flow<ScreenState<Page<Client>>>

    // Point-lookup (non-streaming suspend) — kept on DataState. The
    // ScreenState surface is inherently stream-shaped (Loading → terminal);
    // one-shot suspend fetches don't have a Loading phase to model, so the
    // template's own point-lookups keep a sealed Result equivalent. Callers
    // that want a Loading tick can wrap the suspend in `flow { … }` +
    // `asScreenStateFlow()` at the callsite.
    suspend fun getClient(clientId: Long): DataState<Client>

    // Writes stay on DataState (Phase-3 D1).
    suspend fun updateClient(clientId: Long, client: UpdatedClient): DataState<String>

    fun getClientImage(clientId: Long): Flow<ScreenState<String>>

    suspend fun updateClientImage(clientId: Long, image: String): DataState<String>

    // Non-DataState/ScreenState surface — throwing suspend; leave as-is.
    suspend fun getClientAccounts(clientId: Long): ClientAccountsEntity

    suspend fun getAccounts(clientId: Long, accountType: String): Flow<ScreenState<List<Account>>>

    suspend fun createClient(newClient: NewClient): DataState<Int>

    suspend fun deleteClient(clientId: Int): DataState<Int>
}
