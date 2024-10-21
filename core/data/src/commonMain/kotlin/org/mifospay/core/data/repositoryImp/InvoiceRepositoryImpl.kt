/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.InvoiceRepository
import org.mifospay.core.model.datatables.invoice.Invoice
import org.mifospay.core.model.datatables.invoice.InvoiceEntity
import org.mifospay.core.network.FineractApiManager

class InvoiceRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : InvoiceRepository {
    override fun getInvoice(clientId: Long, invoiceId: Long): Flow<DataState<Invoice>> {
        return apiManager.invoiceApi
            .getInvoice(clientId, invoiceId)
            .onStart { DataState.Loading }
            .catch { DataState.Error(it, null) }
            .map { it.first() }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getInvoices(clientId: Long): Flow<DataState<List<Invoice>>> {
        return apiManager.invoiceApi
            .getInvoices(clientId)
            .onStart { DataState.Loading }
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun createInvoice(clientId: Long, invoice: InvoiceEntity): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.invoiceApi.addInvoice(clientId, invoice)
            }

            DataState.Success("Invoice created successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun updateInvoice(
        clientId: Long,
        invoiceId: Long,
        invoice: InvoiceEntity,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.invoiceApi.updateInvoice(clientId, invoiceId, invoice)
            }

            DataState.Success("Invoice updated successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun deleteInvoice(clientId: Long, invoiceId: Long): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.invoiceApi.deleteInvoice(clientId, invoiceId)
            }

            DataState.Success("Invoice deleted successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }
}
