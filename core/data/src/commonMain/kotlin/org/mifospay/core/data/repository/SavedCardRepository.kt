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

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.Result
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.savedcards.Card

interface SavedCardRepository {
    suspend fun getSavedCards(clientId: Int): Flow<Result<List<Card>>>

    suspend fun addSavedCard(clientId: Int, card: Card): Flow<Result<GenericResponse>>

    suspend fun deleteCard(clientId: Int, cardId: Int): Flow<Result<GenericResponse>>

    suspend fun updateCard(clientId: Int, cardId: Int, card: Card): Flow<Result<GenericResponse>>
}
