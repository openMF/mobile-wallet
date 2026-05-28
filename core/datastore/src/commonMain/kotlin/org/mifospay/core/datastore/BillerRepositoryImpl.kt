/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mifospay.core.common.DataState
import org.mifospay.core.model.autopay.Biller
import org.mifospay.core.model.autopay.BillerCategory

/**
 * TODO: This implementation currently uses local storage (Multiplatform Settings) for biller data.
 * When the backend APIs for biller management are clarified and implemented, this should be
 * refactored to use network-based repository pattern similar to other repositories in the codebase
 * (e.g., UserRepositoryImpl, BeneficiaryRepositoryImpl) with proper API integration.
 */
class BillerRepositoryImpl(
    private val billerDataSource: BillerDataSource,
) : BillerRepository {

    override fun getAllBillers(): Flow<List<Biller>> {
        return billerDataSource.billers
    }

    override suspend fun getBillerById(id: String): Biller? {
        return try {
            val billers = billerDataSource.billers.first()
            billers.find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveBiller(biller: Biller): DataState<Biller> {
        return try {
            val existingBillers = billerDataSource.billers.first()

            // Check if biller already exists
            val existingBiller = existingBillers.find {
                it.name == biller.name && it.accountNumber == biller.accountNumber
            }

            if (existingBiller != null) {
                DataState.Error(Exception("Biller with this name and account number already exists"))
            } else {
                billerDataSource.addBiller(biller)
                DataState.Success(biller)
            }
        } catch (e: Exception) {
            DataState.Error(Exception("Failed to save biller: ${e.message}"))
        }
    }

    override suspend fun updateBiller(biller: Biller): DataState<Biller> {
        return try {
            val existingBillers = billerDataSource.billers.first().toMutableList()
            val index = existingBillers.indexOfFirst { it.id == biller.id }

            if (index != -1) {
                existingBillers[index] = biller
                billerDataSource.updateBillers(existingBillers)
                DataState.Success(biller)
            } else {
                DataState.Error(Exception("Biller not found"))
            }
        } catch (e: Exception) {
            DataState.Error(Exception("Failed to update biller: ${e.message}"))
        }
    }

    override suspend fun deleteBiller(id: String): DataState<Unit> {
        return try {
            billerDataSource.removeBiller(id)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(Exception("Failed to delete biller: ${e.message}"))
        }
    }

    override suspend fun getBillersByCategory(category: BillerCategory): List<Biller> {
        return try {
            val billers = billerDataSource.billers.first()
            billers.filter { it.category == category }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun searchBillersByName(query: String): List<Biller> {
        return try {
            val billers = billerDataSource.billers.first()
            billers.filter { it.name.contains(query, ignoreCase = true) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun isBillerExists(name: String, accountNumber: String): Boolean {
        return try {
            val billers = billerDataSource.billers.first()
            billers.any { it.name == name && it.accountNumber == accountNumber }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearAllBillers(): DataState<Unit> {
        return try {
            billerDataSource.clearBillers()
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(Exception("Failed to clear billers: ${e.message}"))
        }
    }
}
