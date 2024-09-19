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
import com.mifospay.core.model.domain.NotificationPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.notification.FetchNotifications
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.feature.notification.NotificationUiState.Loading

class NotificationViewModel (
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val fetchNotificationsUseCase: FetchNotifications,
) : ViewModel() {

    private val mNotificationUiState: MutableStateFlow<NotificationUiState> =
        MutableStateFlow(Loading)
    val notificationUiState = mNotificationUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            fetchNotifications()
            _isRefreshing.emit(false)
        }
    }

    private fun fetchNotifications() {
        mUseCaseHandler.execute(
            fetchNotificationsUseCase,
            FetchNotifications.RequestValues(
                mLocalRepository.clientDetails.clientId,
            ),
            object : UseCase.UseCaseCallback<FetchNotifications.ResponseValue> {
                override fun onSuccess(response: FetchNotifications.ResponseValue) {
                    mNotificationUiState.value =
                        NotificationUiState.Success(response.notificationPayloadList.orEmpty())
                }

                override fun onError(message: String) {
                    mNotificationUiState.value = NotificationUiState.Error(message)
                }
            },
        )
    }
}

sealed interface NotificationUiState {
    data object Loading : NotificationUiState
    data class Success(val notificationList: List<NotificationPayload>) : NotificationUiState
    data class Error(val message: String) : NotificationUiState
}
