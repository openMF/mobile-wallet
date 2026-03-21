/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.onboarding.language

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile_wallet.feature.onboarding_language.generated.resources.Res
import mobile_wallet.feature.onboarding_language.generated.resources.feature_onboarding_error_saving_settings
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.LanguageConfig
import org.mifospay.core.ui.utils.BaseViewModel

class OnboardingLanguageViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<OnboardingLanguageState, OnboardingLanguageEvent, OnboardingLanguageAction>(
    OnboardingLanguageState(LanguageConfig.DEFAULT),
) {
    init {
        userPreferencesRepository.language.map {
            OnboardingLanguageAction.Internal.LoadLanguage(it)
        }.onEach(::trySendAction)
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: OnboardingLanguageAction) {
        when (action) {
            is OnboardingLanguageAction.Internal.LoadLanguage -> handleLoadLanguage(action)
            is OnboardingLanguageAction.LanguageSelected -> handleLanguageSelected(action)
            is OnboardingLanguageAction.SetLanguage -> handleSetLanguage(action)
            is OnboardingLanguageAction.DismissError -> handleDismissError()
        }
    }

    private fun handleDismissError() {
        mutableStateFlow.update { it.copy(error = null) }
    }

    private fun handleLanguageSelected(action: OnboardingLanguageAction.LanguageSelected) {
        mutableStateFlow.update {
            it.copy(selectedLanguage = action.languageConfig)
        }
    }

    private fun handleSetLanguage(action: OnboardingLanguageAction.SetLanguage) {
        viewModelScope.launch {
            val showLanguageScreenResult = userPreferencesRepository.setShowLanguageScreen(false)
            val saveLanguageResult = userPreferencesRepository.setLanguage(action.languageConfig)

            when {
                saveLanguageResult is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(error = Res.string.feature_onboarding_error_saving_settings)
                    }
                }
                showLanguageScreenResult is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(error = Res.string.feature_onboarding_error_saving_settings)
                    }
                }
                else -> sendEvent(OnboardingLanguageEvent.NavigateToNext)
            }
        }
    }

    private fun handleLoadLanguage(action: OnboardingLanguageAction.Internal.LoadLanguage) {
        mutableStateFlow.update {
            it.copy(
                currentLanguage = action.language,
                selectedLanguage = if (it.selectedLanguage == LanguageConfig.DEFAULT) {
                    action.language
                } else {
                    it.selectedLanguage
                },
            )
        }
    }
}

data class OnboardingLanguageState(
    val currentLanguage: LanguageConfig,
    val selectedLanguage: LanguageConfig = LanguageConfig.DEFAULT,
    val error: StringResource? = null,
)

sealed interface OnboardingLanguageEvent {
    data object NavigateToNext : OnboardingLanguageEvent
}

sealed interface OnboardingLanguageAction {
    data class LanguageSelected(val languageConfig: LanguageConfig) : OnboardingLanguageAction
    data class SetLanguage(val languageConfig: LanguageConfig) : OnboardingLanguageAction
    data object DismissError : OnboardingLanguageAction

    sealed interface Internal : OnboardingLanguageAction {
        data class LoadLanguage(val language: LanguageConfig) : Internal
    }
}
