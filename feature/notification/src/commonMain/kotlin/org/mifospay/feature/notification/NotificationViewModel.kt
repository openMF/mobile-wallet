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
import org.mifospay.core.common.ScreenState
import org.mifospay.core.data.repository.NotificationRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.notification.Notification

internal class NotificationViewModel(
    repository: NotificationRepository,
    // Phase-5 Batch-2: injected to source the clientId for the store's cache
    // key. The Fineract `notifications/` endpoint is session-scoped (no
    // clientId parameter), but the store still partitions the Room SoT per
    // client so a logout-then-login as a different client cannot surface the
    // previous user's cached list. `requireNotNull(...)` because the notification
    // screen is only reachable behind an authenticated session.
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val clientId: Long = requireNotNull(userPreferencesRepository.clientId.value)

    @OptIn(ExperimentalCoroutinesApi::class)
    val notificationUiState = repository.fetchNotificationsScreen(
        // Phase-5 Batch-2 LEDGER read (GOAL D13) — switched from the
        // transitional `fetchNotifications()` (`asScreenStateFlow` shim over
        // the raw Ktorfit flow) to the store-native `fetchNotificationsScreen(...)`
        // that consumes the `notification` Store5 read (`createStore` + Room
        // SoT + CACHE_FIRST_SWR + atomic replacePage). Same
        // `Flow<ScreenState<List<Notification>>>` shape; consumer branches
        // are unchanged. Requires `viewModelScope` for the stream's internal
        // reconnect + periodic + SWR side-fetch coroutines.
        clientId = clientId,
        scope = viewModelScope,
    ).mapLatest { result ->
        // Fold the 6-branch ScreenState back into the 3-branch feature UiState.
        // Empty is projected to Success(emptyList) so the Screen's existing
        // `notificationList.isEmpty()` empty-state renderer still fires. The two
        // richer error branches (NoNetwork / Unauthenticated) are funnelled into
        // Error until Phase-4 wires per-branch surfaces.
        when (result) {
            is ScreenState.Loading -> NotificationUiState.Loading

            is ScreenState.Empty -> NotificationUiState.Success(emptyList())

            is ScreenState.Content -> NotificationUiState.Success(result.data)

            is ScreenState.Error ->
                NotificationUiState.Error(result.error.message.toString())

            is ScreenState.NoNetwork ->
                NotificationUiState.Error("No network. Please check your connection.")

            is ScreenState.Unauthenticated ->
                NotificationUiState.Error("Session expired. Please log in again.")
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
