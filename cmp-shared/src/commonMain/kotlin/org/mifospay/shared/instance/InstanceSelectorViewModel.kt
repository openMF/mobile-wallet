/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.instance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.instance.InstancesConfig
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.network.config.InstanceConfigLoader

sealed interface InstanceSelectorUiState {
    data object Loading : InstanceSelectorUiState
    data class Success(
        val instances: List<ServerInstance>,
        val selectedInstance: ServerInstance?,
    ) : InstanceSelectorUiState
    data class Error(val message: String) : InstanceSelectorUiState
}

class InstanceSelectorViewModel(
    private val instanceConfigLoader: InstanceConfigLoader,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<InstanceSelectorUiState>(InstanceSelectorUiState.Loading)
    val uiState: StateFlow<InstanceSelectorUiState> = _uiState.asStateFlow()

    init {
        loadInstances()
    }

    fun loadInstances() {
        viewModelScope.launch {
            _uiState.value = InstanceSelectorUiState.Loading
            when (val result = instanceConfigLoader.fetchInstancesConfig()) {
                is DataState.Success -> {
                    val config = result.data
                    val selectedInstance = userPreferencesRepository.selectedInstance.value
                    _uiState.value = InstanceSelectorUiState.Success(
                        instances = config.instances,
                        selectedInstance = selectedInstance ?: config.getDefaultInstance(),
                    )
                }
                is DataState.Error -> {
                    _uiState.value = InstanceSelectorUiState.Error(
                        message = result.exception.message ?: "Failed to load instances",
                    )
                }
                is DataState.Loading -> {
                    _uiState.value = InstanceSelectorUiState.Loading
                }
            }
        }
    }

    fun selectInstance(instance: ServerInstance) {
        viewModelScope.launch {
            userPreferencesRepository.updateSelectedInstance(instance)
            val currentState = _uiState.value
            if (currentState is InstanceSelectorUiState.Success) {
                _uiState.value = currentState.copy(selectedInstance = instance)
            }
        }
    }
}
