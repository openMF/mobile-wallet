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
import org.mifospay.core.common.DataState
import org.mifospay.core.model.savedcards.CardPayload
import org.mifospay.core.model.savedcards.SavedCard

interface SavedCardRepository {
    fun getSavedCards(clientId: Long): Flow<DataState<List<SavedCard>>>

    fun getSavedCard(clientId: Long, cardId: Long): Flow<DataState<SavedCard>>

    suspend fun addSavedCard(clientId: Long, card: CardPayload): DataState<String>

    suspend fun deleteCard(clientId: Long, cardId: Long): DataState<String>

    suspend fun updateCard(clientId: Long, cardId: Long, card: CardPayload): DataState<String>
}
