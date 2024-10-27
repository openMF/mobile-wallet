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
import org.mifospay.core.data.repository.SavedCardRepository
import org.mifospay.core.model.savedcards.CardPayload
import org.mifospay.core.model.savedcards.SavedCard
import org.mifospay.core.network.FineractApiManager

class SavedCardRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : SavedCardRepository {
    override fun getSavedCards(clientId: Long): Flow<DataState<List<SavedCard>>> {
        return apiManager.savedCardApi
            .getSavedCards(clientId)
            .catch { DataState.Error(it, null) }
            .onStart { DataState.Loading }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getSavedCard(clientId: Long, cardId: Long): Flow<DataState<SavedCard>> {
        return apiManager.savedCardApi
            .getSavedCard(clientId, cardId)
            .catch { DataState.Error(it, null) }
            .onStart { DataState.Loading }
            .map { it.first() }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun addSavedCard(clientId: Long, card: CardPayload): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savedCardApi.addSavedCard(clientId, card)
            }

            DataState.Success("Card added successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun deleteCard(clientId: Long, cardId: Long): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savedCardApi.deleteCard(clientId, cardId)
            }

            DataState.Success("Card deleted successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun updateCard(
        clientId: Long,
        cardId: Long,
        card: CardPayload,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savedCardApi.updateCard(clientId, cardId, card)
            }

            DataState.Success("Card updated successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }
}
