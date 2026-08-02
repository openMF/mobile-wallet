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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.BillerRepository
import org.mifospay.core.model.autopay.Biller
import org.mifospay.core.model.autopay.BillerCategory
import org.mifospay.core.network.FineractApiManager

/**
 * Network-based implementation of BillerRepository.
 *
 * TODO: This implementation uses placeholder API endpoints. When the backend APIs
 * for biller management are finalized, update the endpoints and request/response
 * models according to the actual API contract.
 *
 * TODO(phase-4): Migrate to ScreenState via `createOfflineStore` (Biller
 * autopay-bills tier). Deferred from Phase-3 Batch B cutover because billers
 * need a real Store5 offline store (SourceOfTruth = Room, fetcher = Fineract
 * billers/categories endpoints) before their reads can adopt the fork-wide
 * `ScreenStateStream<T>` envelope.
 */
class BillerRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BillerRepository {

    override fun getAllBillers(): Flow<List<Biller>> {
        return apiManager.billerApi
            .getAllBillers()
            .catch {
                // Return empty list on error for now
                emit(emptyList())
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun getBillerById(id: String): Biller? {
        return try {
            withContext(ioDispatcher) {
                apiManager.billerApi.getBillerById(id).first()
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveBiller(biller: Biller): DataState<Biller> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.billerApi.createBiller(biller).first()
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateBiller(biller: Biller): DataState<Biller> {
        return try {
            val billerId = biller.id ?: return DataState.Error(Exception("Biller ID is required for update"))
            val result = withContext(ioDispatcher) {
                apiManager.billerApi.updateBiller(billerId, biller).first()
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun deleteBiller(id: String): DataState<Unit> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billerApi.deleteBiller(id).first()
            }
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getBillersByCategory(category: BillerCategory): List<Biller> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billerApi.getBillersByCategory(category).first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun searchBillersByName(query: String): List<Biller> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billerApi.searchBillersByName(query).first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun isBillerExists(name: String, accountNumber: String): Boolean {
        return try {
            withContext(ioDispatcher) {
                apiManager.billerApi.isBillerExists(name, accountNumber).first()
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearAllBillers(): DataState<Unit> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billerApi.clearAllBillers().first()
            }
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
