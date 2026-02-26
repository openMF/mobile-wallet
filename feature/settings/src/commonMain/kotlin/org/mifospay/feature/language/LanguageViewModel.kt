/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.language

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.LanguageConfig
import org.mifospay.core.ui.utils.BaseViewModel

class LanguageViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<LanguageState, LanguageEvent, LanguageAction>(
    LanguageState(
        currentLanguage = LanguageConfig.DEFAULT,
        selectedLanguage = LanguageConfig.DEFAULT,
    ),
) {
    init {
        userPreferencesRepository.language.map {
            LanguageAction.Internal.LoadLanguage(it)
        }.onEach(::trySendAction)
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: LanguageAction) {
        when (action) {
            is LanguageAction.OnNavigateBack -> sendEvent(LanguageEvent.NavigateBack)
            is LanguageAction.LanguageSelected -> handleLanguageSelection(action.language)
            is LanguageAction.SetLanguage -> handleSetLanguage(action)
            is LanguageAction.Internal.LoadLanguage -> handleLoadLanguage(action)
        }
    }

    private fun handleLanguageSelection(language: LanguageConfig) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(selectedLanguage = language)
            }
        }
    }

    private fun handleSetLanguage(action: LanguageAction.SetLanguage) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguage(action.languageConfig)
            sendEvent(LanguageEvent.NavigateBack)
        }
    }

    private fun handleLoadLanguage(action: LanguageAction.Internal.LoadLanguage) {
        mutableStateFlow.update {
            it.copy(
                currentLanguage = action.language,
                selectedLanguage = action.language,
            )
        }
    }
}

data class LanguageState(
    val selectedLanguage: LanguageConfig,
    val currentLanguage: LanguageConfig,
)

sealed interface LanguageEvent {
    data object NavigateBack : LanguageEvent
}

sealed interface LanguageAction {
    data object OnNavigateBack : LanguageAction
    data class LanguageSelected(val language: LanguageConfig) : LanguageAction
    data class SetLanguage(val languageConfig: LanguageConfig) : LanguageAction

    sealed interface Internal : LanguageAction {
        data class LoadLanguage(val language: LanguageConfig) : Internal
    }
}
