/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.common.presenter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.SearchClient
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val searchClient: SearchClient,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(SEARCH_QUERY, "")

    private val _searchResults = MutableStateFlow<SearchResultState>(SearchResultState.Loading)
    val searchResults: StateFlow<SearchResultState> = _searchResults.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
        if (query.length > 3) {
            performSearch(query)
        } else {
            _searchResults.value = SearchResultState.Idle
        }
    }

    private fun performSearch(query: String) {
        mUsecaseHandler.execute(
            searchClient,
            SearchClient.RequestValues(query),
            object : UseCase.UseCaseCallback<SearchClient.ResponseValue> {
                override fun onSuccess(response: SearchClient.ResponseValue) {
                    _searchResults.value =
                        SearchResultState.Success(response.results.toMutableList())
                }

                override fun onError(message: String) {}
            },
        )
    }
}

sealed class SearchResultState {
    data object Idle : SearchResultState()
    data object Loading : SearchResultState()
    data class Success(val results: MutableList<SearchResult>) : SearchResultState()
    data class Error(val message: String) : SearchResultState()
}

private const val SEARCH_QUERY = "searchQuery"
