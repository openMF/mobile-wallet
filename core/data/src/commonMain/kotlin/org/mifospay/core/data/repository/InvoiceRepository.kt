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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState
import org.mifospay.core.model.datatables.invoice.Invoice
import org.mifospay.core.model.datatables.invoice.InvoiceEntity

interface InvoiceRepository {
    // Phase-3 cutover — reads on ScreenState.
    fun getInvoice(clientId: Long, invoiceId: Long): Flow<ScreenState<Invoice>>

    fun getInvoices(clientId: Long): Flow<ScreenState<List<Invoice>>>

    /**
     * Phase-5 Batch-2 **LEDGER read** for the `invoice` archetype (GOAL D13) —
     * returns an offline-first `Flow<ScreenState<List<Invoice>>>` consumed
     * through the `invoice` Store5
     * [`Store`][org.mobilenativefoundation.store.store5.Store]
     * (`createStore` + Room [`SourceOfTruth`][org.mobilenativefoundation.store.store5.SourceOfTruth]
     * + atomic
     * [`replacePage`][kpt.core.database.wallet.invoice.InvoiceDao.replacePage]
     * writer) via
     * [`Store.asScreenStream`][kpt.core.base.store.screen.asScreenStream] — the
     * Store5-native bridge, distinct from the transitional
     * [`asScreenStateFlow`][org.mifospay.core.common.asScreenStateFlow] the
     * legacy [getInvoices] path uses.
     *
     * The store is driven by
     * [`FetchPolicy.CACHE_FIRST_SWR`][kpt.core.base.store.screen.FetchPolicy.CACHE_FIRST_SWR]
     * (Phase-5 T10 default) — subscribers see the cached page instantly and a
     * background revalidation fires on the Stale/VeryStale band edge.
     *
     * ### Write path (GOAL D1)
     *
     * This is a READ path. Invoice CRUD writes ([createInvoice], [updateInvoice],
     * [deleteInvoice]) continue to flow through their existing online paths and
     * appear here on the next refresh cycle — the LEDGER cache is server-truth.
     *
     * @param clientId owning client id — the store's page key AND the API path
     *   parameter (`GET /datatables/invoice/{clientId}`).
     * @param scope Coroutine scope for the stream's internal helper coroutines
     *   (typically `viewModelScope`).
     */
    fun getInvoicesScreen(
        clientId: Long,
        scope: CoroutineScope,
    ): Flow<ScreenState<List<Invoice>>>

    // Writes stay on DataState (Phase-3 D1).
    suspend fun createInvoice(clientId: Long, invoice: InvoiceEntity): DataState<String>

    suspend fun updateInvoice(
        clientId: Long,
        invoiceId: Long,
        invoice: InvoiceEntity,
    ): DataState<String>

    suspend fun deleteInvoice(clientId: Long, invoiceId: Long): DataState<String>
}
