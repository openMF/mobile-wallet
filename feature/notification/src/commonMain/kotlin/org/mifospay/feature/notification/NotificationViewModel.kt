/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.NotificationRepository
import org.mifospay.core.model.notification.Notification

internal class NotificationViewModel(
    repository: NotificationRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val notificationUiState = repository.fetchNotifications().mapLatest { result ->
        when (result) {
            is DataState.Loading -> {
                NotificationUiState.Loading
            }

            is DataState.Error -> {
                val message = result.exception.message.toString()
                NotificationUiState.Error(message)
            }

            is DataState.Success -> {
                NotificationUiState.Success(result.data)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotificationUiState.Loading,
    )
}

internal sealed interface NotificationUiState {
    data object Loading : NotificationUiState
    data class Success(val notificationList: List<Notification>) : NotificationUiState
    data class Error(val message: String) : NotificationUiState
}
