/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.ScreenState
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.client.Client
import org.mifospay.core.ui.utils.BaseViewModel

internal class ProfileViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val clientRepository: ClientRepository,
) : BaseViewModel<ProfileState, ProfileEvent, ProfileAction>(
    initialState = run {
        val clientId = requireNotNull(preferencesRepository.clientId.value)
        ProfileState(clientId = clientId)
    },
) {
    val clientState = clientRepository.getClientInfoScreen(
        // Phase-5 Batch-2 SINGLE-ROW-PER-KEY read (GOAL D13) — switched from
        // the transitional `getClientInfo(clientId)` (`asScreenStateFlow` shim
        // over the raw Ktorfit flow) to the store-native
        // `getClientInfoScreen(...)` that consumes the `clientDetail` Store5
        // read (`createStore` + Room SoT + CACHE_FIRST_SWR + single-row
        // upsert). Same `Flow<ScreenState<Client>>` shape; consumer branches
        // are unchanged. Requires `viewModelScope` for the stream's internal
        // reconnect + periodic + SWR side-fetch coroutines.
        //
        // NOTE — `getClientImage(...)` (the image blob-URL stream, observed
        // in the `init` block below) intentionally stays on the transitional
        // shim; its caching story (Coil vs Room blob) is out of scope for
        // this batch.
        clientId = state.clientId,
        scope = viewModelScope,
    ).map {
        // Fold ScreenState → the existing 3-branch ProfileState.ViewState.
        // Empty is defensively projected to Error (single-record endpoint
        // shouldn't emit Empty). NoNetwork / Unauthenticated fold into Error
        // until Phase-4 differentiates.
        when (it) {
            is ScreenState.Loading -> ProfileState.ViewState.Loading

            is ScreenState.Empty -> ProfileState.ViewState.Error("Client not found.")

            is ScreenState.Content -> ProfileState.ViewState.Success(it.data)

            is ScreenState.Error -> ProfileState.ViewState.Error(it.error.message.toString())

            is ScreenState.NoNetwork ->
                ProfileState.ViewState.Error("No network. Please check your connection.")

            is ScreenState.Unauthenticated ->
                ProfileState.ViewState.Error("Session expired. Please log in again.")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileState.ViewState.Loading,
    )

    init {
        // Load the client image side-stream. The prior implementation shuttled
        // a `DataState<String>` through an internal action; with `getClientImage`
        // now returning a ScreenState stream we fold it inline via the
        // `observeScreen` bridge. Only success updates `clientImage` — the
        // image tile silently falls back to the placeholder for all other
        // branches (matches prior behaviour, which suppressed error dialogs
        // for image failures per the commented-out lines in the old code).
        clientRepository.getClientImage(state.clientId).observeScreen { screenState ->
            if (screenState is ScreenState.Content) {
                mutableStateFlow.update { it.copy(clientImage = screenState.data) }
            }
        }
    }

    override fun handleAction(action: ProfileAction) {
        when (action) {
            ProfileAction.NavigateToEditProfile -> {
                sendEvent(ProfileEvent.OnEditProfile)
            }

            ProfileAction.NavigateToLinkBankAccount -> {
                sendEvent(ProfileEvent.OnLinkBankAccount)
            }

            ProfileAction.ShowPersonalQRCode -> {
                sendEvent(ProfileEvent.ShowQRCode)
            }

            ProfileAction.DismissErrorDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is ProfileAction.NavigateBack -> {
                sendEvent(ProfileEvent.OnNavigateBack)
            }
        }
    }
}

internal data class ProfileState(
    val clientId: Long,
    val clientImage: String? = null,
    val dialogState: DialogState? = null,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: String) : ViewState
        data class Success(val client: Client) : ViewState
    }

    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: StringResource) : DialogState
    }
}

internal sealed interface ProfileEvent {
    data object OnEditProfile : ProfileEvent
    data object OnLinkBankAccount : ProfileEvent
    data object ShowQRCode : ProfileEvent
    data object OnNavigateBack : ProfileEvent
}

internal sealed interface ProfileAction {
    data object NavigateToEditProfile : ProfileAction
    data object NavigateToLinkBankAccount : ProfileAction
    data object ShowPersonalQRCode : ProfileAction

    data object DismissErrorDialog : ProfileAction

    data object NavigateBack : ProfileAction
}
