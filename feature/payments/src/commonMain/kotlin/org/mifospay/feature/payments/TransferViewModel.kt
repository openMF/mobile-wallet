/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.payments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.ui.utils.BaseViewModel

class TransferViewModel(
    private val repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TransferState, TransferEvent, TransferAction>(
    initialState = savedStateHandle.getSerialized(TRANSFER_STATE_KEY) ?: run {
        val client = requireNotNull(repository.client.value)

        TransferState(
            mobileNo = client.mobileNo,
            externalId = client.externalId,
        )
    },
) {

    companion object {
        private const val TRANSFER_STATE_KEY = "TransferState"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = TRANSFER_STATE_KEY, value = it) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: TransferAction) {
        when (action) {
            is TransferAction.ShowQR -> {
                sendEvent(TransferEvent.OnShowQR)
            }

            is TransferAction.CopyTextToClipboard -> {
                sendEvent(TransferEvent.OnCopyTextToClipboard(action.text))
            }
        }
    }
}

@Serializable
data class TransferState(
    val mobileNo: String,
    val externalId: String,
)

sealed interface TransferEvent {
    data class OnCopyTextToClipboard(val text: String) : TransferEvent
    data object OnShowQR : TransferEvent
}

sealed interface TransferAction {
    data class CopyTextToClipboard(val text: String) : TransferAction
    data object ShowQR : TransferAction
}
