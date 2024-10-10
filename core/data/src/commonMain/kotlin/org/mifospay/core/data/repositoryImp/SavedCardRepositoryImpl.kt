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
import org.mifospay.core.data.repository.SavedCardRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.savedcards.Card

class SavedCardRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : SavedCardRepository {
    override suspend fun getSavedCards(clientId: Int): Flow<Result<List<Card>>> {
        return apiManager.savedCardApi.getSavedCards(clientId).asResult().flowOn(ioDispatcher)
    }

    override suspend fun addSavedCard(clientId: Int, card: Card): Flow<Result<GenericResponse>> {
        return apiManager.savedCardApi.addSavedCard(clientId, card).asResult().flowOn(ioDispatcher)
    }

    override suspend fun deleteCard(clientId: Int, cardId: Int): Flow<Result<GenericResponse>> {
        return apiManager.savedCardApi.deleteCard(clientId, cardId).asResult().flowOn(ioDispatcher)
    }

    override suspend fun updateCard(
        clientId: Int,
        cardId: Int,
        card: Card,
    ): Flow<Result<GenericResponse>> {
        return apiManager.savedCardApi
            .updateCard(clientId, cardId, card)
            .asResult().flowOn(ioDispatcher)
    }
}
