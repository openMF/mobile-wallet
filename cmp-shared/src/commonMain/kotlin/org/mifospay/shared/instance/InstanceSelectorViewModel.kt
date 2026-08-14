/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
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
import org.mifospay.core.model.instance.InstancesConfig
import org.mifospay.core.model.instance.InterbankServer
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.network.config.InstanceConfigLoader
import org.mifospay.core.ui.utils.BaseViewModel

class InstanceSelectorViewModel(
    instanceConfigLoader: InstanceConfigLoader,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<InstanceSelectorState, InstanceSelectorEvent, InstanceSelectorAction>(
    initialState = InstanceSelectorState(),
) {

    private val tempSelectedMainInstance = MutableStateFlow<ServerInstance?>(null)
    private val tempSelectedInterbankInstance = MutableStateFlow<InterbankServer?>(null)

    init {
        // Combine all sources and update state
        combine(
            instanceConfigLoader.observeInstancesConfig(),
            userPreferencesRepository.selectedInstance,
            userPreferencesRepository.selectedInterbankInstance,
            tempSelectedMainInstance,
            tempSelectedInterbankInstance,
        ) { remoteConfig, selectedMainInstance, selectedInterbankInstance, tempMainInstance, tempInterbankInstance ->
            when (remoteConfig) {
                is DataState.Success -> {
                    val config = remoteConfig.data
                    val defaultInstance = config.getDefaultInstance()

                    // Determine the effective main instance
                    val effectiveMainInstance = tempMainInstance
                        ?: selectedMainInstance
                        ?: defaultInstance

                    // Determine the effective interbank instance
                    val effectiveInterbankInstance = tempInterbankInstance
                        ?: selectedInterbankInstance
                        ?: effectiveMainInstance?.getDefaultInterbankServer()

                    mutableStateFlow.update {
                        it.copy(
                            instances = config.instances,
                            selectedMainInstance = selectedMainInstance ?: defaultInstance,
                            selectedInterbankInstance = selectedInterbankInstance
                                ?: defaultInstance?.getDefaultInterbankServer(),
                            tempSelectedMainInstance = effectiveMainInstance,
                            tempSelectedInterbankInstance = effectiveInterbankInstance,
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
            is InstanceSelectorAction.SelectMainInstance -> {
                tempSelectedMainInstance.value = action.instance
                // Auto-select the default interbank server for the new main instance
                tempSelectedInterbankInstance.value = action.instance.getDefaultInterbankServer()
            }

            is InstanceSelectorAction.SelectInterbankInstance -> {
                tempSelectedInterbankInstance.value = action.instance
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
                tempSelectedMainInstance.value != currentState.selectedMainInstance
            val interbankInstanceChanged =
                tempSelectedInterbankInstance.value != currentState.selectedInterbankInstance

            if (mainInstanceChanged && tempSelectedMainInstance.value != null) {
                userPreferencesRepository.updateSelectedInstance(tempSelectedMainInstance.value!!)
            }

            if (interbankInstanceChanged && tempSelectedInterbankInstance.value != null) {
                userPreferencesRepository.updateSelectedInterbankInstance(
                    tempSelectedInterbankInstance.value!!,
                )
            }

            // Reset temp selections after update
            tempSelectedMainInstance.value = null
            tempSelectedInterbankInstance.value = null

            sendEvent(InstanceSelectorEvent.DismissSheet)
        }
    }
}

// State
data class InstanceSelectorState(
    val instances: List<ServerInstance> = emptyList(),
    val selectedMainInstance: ServerInstance? = null,
    val selectedInterbankInstance: InterbankServer? = null,
    val tempSelectedMainInstance: ServerInstance? = null,
    val tempSelectedInterbankInstance: InterbankServer? = null,
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
    data class SelectMainInstance(val instance: ServerInstance) : InstanceSelectorAction
    data class SelectInterbankInstance(val instance: InterbankServer) : InstanceSelectorAction
    data object UpdateInstances : InstanceSelectorAction
    data class ConfigLoaded(val config: DataState<InstancesConfig>) : InstanceSelectorAction
}
