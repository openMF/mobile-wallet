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

// STUB VERDICT (Phase-5 Batch-4, 2026-08-01):
// -----------------------------------------------------------------------------
// The `merchants` feature has NO backend endpoint today. Neither
// `SelfServiceApiManager` nor `FineractApiManager` exposes a `merchantApi`
// property (verified by grep across `core/network`), and this ViewModel returns
// a hardcoded `emptyList()` without depending on any repository. Consequently
// there is no data source to cache and no store to build.
//
// Phase-5 Batch-4 therefore emits NO store for merchants — no `MerchantEntity`,
// no `MerchantDao`, no `MerchantStore`, no DI wiring. When a Fineract-side
// merchant list endpoint materializes (or a fork-side directory is authored),
// add:
//   1. `core/model/src/.../merchant/Merchant.kt` (domain).
//   2. `core/network/.../services/MerchantService.kt` +
//      `SelfServiceApiManager.merchantApi` (or the Fineract mount if that is
//      where the endpoint lives).
//   3. `core/database/.../wallet/merchant/{MerchantEntity,MerchantDao,MerchantEntityMapper}.kt`.
//   4. `core/store/.../wallet/merchant/{MerchantKey,MerchantStore}.kt` — LEDGER
//      read (`createStore` + CACHE_FIRST_SWR + `replacePage`) mirroring the
//      Batch-1 `beneficiary` / `savedCards` shape (client-scoped) OR the
//      Batch-3 `offices` shape (global singleton-keyed reference data) —
//      whichever the endpoint semantics dictate.
//   5. AppDatabase v-bump + AutoMigration; AppStoreRegistry + StoreModule
//      qualifier + register; MerchantRepository / MerchantRepositoryImpl;
//      RepositoryModule wiring; MerchantViewModel cutover to a store-backed
//      `Flow<ScreenState<List<Merchant>>>` (drop the `arrayListOf()` stub).
//
// TODO: merchants has no backend endpoint; add MerchantStore when the API materializes.
// -----------------------------------------------------------------------------
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
