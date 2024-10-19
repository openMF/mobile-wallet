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
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.NotificationRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.NotificationPayload

class NotificationRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : NotificationRepository {
    override suspend fun fetchNotifications(
        clientId: Long,
    ): Flow<DataState<List<NotificationPayload>>> {
        return apiManager.notificationApi
            .fetchNotifications(clientId)
            .asDataStateFlow().flowOn(ioDispatcher)
    }
}
