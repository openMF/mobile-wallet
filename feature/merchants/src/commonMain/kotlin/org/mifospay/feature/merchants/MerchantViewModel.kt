/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.merchants

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity

class MerchantViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _merchantUiState = MutableStateFlow<MerchantUiState>(MerchantUiState.Empty)
    val merchantUiState: StateFlow<MerchantUiState> = _merchantUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val merchantsListUiState: StateFlow<MerchantUiState> = searchQuery
        .map {
            when (_merchantUiState.value) {
                is MerchantUiState.ShowMerchants -> {
                    MerchantUiState.ShowMerchants(emptyList())
                }

                else -> MerchantUiState.ShowMerchants(arrayListOf())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MerchantUiState.ShowMerchants(arrayListOf()),
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(200)
            _isRefreshing.value = false
        }
    }
}

sealed class MerchantUiState {
    data object Loading : MerchantUiState()
    data object Empty : MerchantUiState()
    data class Error(val message: String) : MerchantUiState()
    data class ShowMerchants(val merchants: List<SavingsWithAssociationsEntity>) : MerchantUiState()
}
