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
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.repository.InvoiceRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.Invoice

class InvoiceRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : InvoiceRepository {
    override suspend fun getInvoice(clientId: Int, invoiceId: Int): Flow<Result<Invoice>> {
        return apiManager.invoiceApi.getInvoice(clientId, invoiceId).asResult().flowOn(ioDispatcher)
    }

    override suspend fun getInvoices(clientId: Int): Flow<Result<List<Invoice>>> {
        return apiManager.invoiceApi.getInvoices(clientId).asResult().flowOn(ioDispatcher)
    }

    override suspend fun createInvoice(clientId: Int, invoice: Invoice): Result<Unit> {
        return try {
            Result.Success(apiManager.invoiceApi.addInvoice(clientId, invoice))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateInvoice(
        clientId: Int,
        invoiceId: Int,
        invoice: Invoice,
    ): Flow<Result<GenericResponse>> {
        return apiManager.invoiceApi
            .updateInvoice(clientId, invoiceId, invoice).asResult()
            .flowOn(ioDispatcher)
    }

    override suspend fun deleteInvoice(
        clientId: Int,
        invoiceId: Int,
    ): Flow<Result<GenericResponse>> {
        return apiManager.invoiceApi
            .deleteInvoice(clientId, invoiceId).asResult()
            .flowOn(ioDispatcher)
    }
}
