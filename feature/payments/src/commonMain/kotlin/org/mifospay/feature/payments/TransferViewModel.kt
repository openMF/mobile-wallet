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
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.ui.utils.BaseViewModel

private const val KEY = "TransferState"

class TransferViewModel(
    private val repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TransferState, TransferEvent, TransferAction>(
    initialState = savedStateHandle[KEY] ?: run {
        val client = requireNotNull(repository.client.value)

        TransferState(
            mobileNo = client.mobileNo,
            externalId = client.externalId,
        )
    },
) {
    init {
        stateFlow
            .onEach { savedStateHandle[KEY] = it }
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

@Parcelize
data class TransferState(
    val mobileNo: String,
    val externalId: String,
) : Parcelable

sealed interface TransferEvent {
    data class OnCopyTextToClipboard(val text: String) : TransferEvent
    data object OnShowQR : TransferEvent
}

sealed interface TransferAction {
    data class CopyTextToClipboard(val text: String) : TransferAction
    data object ShowQR : TransferAction
}
