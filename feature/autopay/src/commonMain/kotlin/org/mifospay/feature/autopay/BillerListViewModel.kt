/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.autopay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.datastore.BillerRepository
import org.mifospay.core.model.autopay.Biller
import org.mifospay.core.ui.utils.BaseViewModel

class BillerListViewModel(
    savedStateHandle: SavedStateHandle,
    private val billerRepository: BillerRepository,
) : BaseViewModel<BillerListState, BillerListEvent, BillerListAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: BillerListState(),
) {

    init {
        loadBillers()
    }

    override fun handleAction(action: BillerListAction) {
        when (action) {
            is BillerListAction.LoadBillers -> {
                loadBillers()
            }
            is BillerListAction.SearchBillers -> {
                searchBillers(action.query)
            }
            is BillerListAction.DeleteBiller -> {
                deleteBiller(action.billerId)
            }
            is BillerListAction.EditBiller -> {
                sendEvent(BillerListEvent.NavigateToEditBiller(action.billerId))
            }
            is BillerListAction.AddNewBiller -> {
                sendEvent(BillerListEvent.NavigateToAddBiller)
            }
        }
    }

    private fun loadBillers() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isLoading = true) }

            try {
                billerRepository.getAllBillers().collect { billers ->
                    mutableStateFlow.update {
                        it.copy(
                            billers = billers,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load billers: ${e.message}",
                    )
                }
            }
        }
    }

    private fun searchBillers(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadBillers()
            } else {
                try {
                    val searchResults = billerRepository.searchBillersByName(query)
                    mutableStateFlow.update {
                        it.copy(
                            billers = searchResults,
                            isLoading = false,
                            error = null,
                        )
                    }
                } catch (e: Exception) {
                    mutableStateFlow.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to search billers: ${e.message}",
                        )
                    }
                }
            }
        }
    }

    private fun deleteBiller(billerId: String) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isLoading = true) }

            when (val result = billerRepository.deleteBiller(billerId)) {
                is DataState.Loading -> {
                    // Already set loading state above
                }
                is DataState.Success -> {
                    sendEvent(BillerListEvent.BillerDeleted)
                    loadBillers() // Reload the list
                }
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to delete biller: ${result.exception.message}",
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_STATE = "biller_list_state"
    }
}

@Serializable
data class BillerListState(
    val billers: List<Biller> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface BillerListEvent {
    data class NavigateToEditBiller(val billerId: String) : BillerListEvent
    data object NavigateToAddBiller : BillerListEvent
    data object BillerDeleted : BillerListEvent
}

sealed interface BillerListAction {
    data object LoadBillers : BillerListAction
    data class SearchBillers(val query: String) : BillerListAction
    data class DeleteBiller(val billerId: String) : BillerListAction
    data class EditBiller(val billerId: String) : BillerListAction
    data object AddNewBiller : BillerListAction
}
