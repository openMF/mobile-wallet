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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.instance.InstanceType
import org.mifospay.core.model.instance.InstancesConfig
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.network.config.InstanceConfigLoader
import org.mifospay.core.ui.utils.BaseViewModel

class InstanceSelectorViewModel(
    private val instanceConfigLoader: InstanceConfigLoader,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<InstanceSelectorState, InstanceSelectorEvent, InstanceSelectorAction>(
    initialState = InstanceSelectorState(),
) {

    private val _tempSelectedMainInstance = MutableStateFlow<ServerInstance?>(null)
    private val _tempSelectedInterbankInstance = MutableStateFlow<ServerInstance?>(null)

    init {
        // Combine all sources and update state
        combine(
            instanceConfigLoader.observeInstancesConfig(),
            userPreferencesRepository.selectedInstance,
            userPreferencesRepository.selectedInterbankInstance,
            _tempSelectedMainInstance,
            _tempSelectedInterbankInstance,
        ) { remoteConfig, selectedMainInstance, selectedInterbankInstance, tempMainInstance, tempInterbankInstance ->
            when (remoteConfig) {
                is DataState.Success -> {
                    val config = remoteConfig.data
                    mutableStateFlow.update {
                        it.copy(
                            mainInstances = config.getMainInstances(),
                            interbankInstances = config.getInterbankInstances(),
                            selectedMainInstance = selectedMainInstance
                                ?: config.getDefaultInstance(),
                            selectedInterbankInstance = selectedInterbankInstance
                                ?: config.getDefaultInterbankInstance(),
                            tempSelectedMainInstance = tempMainInstance
                                ?: selectedMainInstance ?: config.getDefaultInstance(),
                            tempSelectedInterbankInstance = tempInterbankInstance
                                ?: selectedInterbankInstance
                                ?: config.getDefaultInterbankInstance(),
                            isLoading = false,
                            error = null,
                        )
                    }
                }

                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            isLoading = false,
                            error = remoteConfig.exception.message ?: "Failed to load instances",
                        )
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(isLoading = true)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        ).launchIn(viewModelScope)
    }

    override fun handleAction(action: InstanceSelectorAction) {
        when (action) {
            is InstanceSelectorAction.SelectInstance -> {
                when (action.instance.type) {
                    InstanceType.MAIN -> {
                        _tempSelectedMainInstance.value = action.instance
                    }

                    InstanceType.INTERBANK -> {
                        _tempSelectedInterbankInstance.value = action.instance
                    }
                }
            }

            is InstanceSelectorAction.UpdateInstances -> {
                updateInstances()
            }

            is InstanceSelectorAction.ConfigLoaded -> {
                // Handled by init block
            }
        }
    }

    private fun updateInstances() {
        viewModelScope.launch {
            val currentState = state
            val mainInstanceChanged =
                _tempSelectedMainInstance.value != currentState.selectedMainInstance
            val interbankInstanceChanged =
                _tempSelectedInterbankInstance.value != currentState.selectedInterbankInstance

            if (mainInstanceChanged && _tempSelectedMainInstance.value != null) {
                userPreferencesRepository.updateSelectedInstance(_tempSelectedMainInstance.value!!)
            }

            if (interbankInstanceChanged && _tempSelectedInterbankInstance.value != null) {
                userPreferencesRepository.updateSelectedInterbankInstance(
                    _tempSelectedInterbankInstance.value!!,
                )
            }

            // Reset temp selections after update
            _tempSelectedMainInstance.value = null
            _tempSelectedInterbankInstance.value = null

            sendEvent(InstanceSelectorEvent.DismissSheet)
        }
    }
}

// State
data class InstanceSelectorState(
    val mainInstances: List<ServerInstance> = emptyList(),
    val interbankInstances: List<ServerInstance> = emptyList(),
    val selectedMainInstance: ServerInstance? = null,
    val selectedInterbankInstance: ServerInstance? = null,
    val tempSelectedMainInstance: ServerInstance? = null,
    val tempSelectedInterbankInstance: ServerInstance? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val isUpdateEnabled: Boolean
        get() = tempSelectedMainInstance != selectedMainInstance ||
                tempSelectedInterbankInstance != selectedInterbankInstance
}

// Events
sealed interface InstanceSelectorEvent {
    data object DismissSheet : InstanceSelectorEvent
}

// Actions
sealed interface InstanceSelectorAction {
    data class SelectInstance(val instance: ServerInstance) : InstanceSelectorAction
    data object UpdateInstances : InstanceSelectorAction
    data class ConfigLoaded(val config: DataState<InstancesConfig>) : InstanceSelectorAction
}
