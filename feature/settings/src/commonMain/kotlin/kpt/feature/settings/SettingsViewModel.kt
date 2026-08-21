/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.settings

import androidx.lifecycle.viewModelScope
import io.github.mobilebytelabs.kmptoolkit.firebase.analytics.AnalyticsHelper
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.user.UserDataRepository
import kpt.core.model.user.DarkThemeConfig
import kpt.core.model.user.LanguageConfig
import kpt.core.model.user.ThemeBrand

/**
 * Settings ViewModel — the toolkit's `local_only_prefs` demo, on the MVI `BaseViewModel`
 * idiom (state + typed actions), consistent with every other feature. Reads the reactive
 * [UserDataRepository.userData] into [SettingsUiState]; each preference change is a typed
 * [SettingsAction] handled in [handleAction], persisted to the repository, and logged.
 */
class SettingsViewModel(
    private val settingsRepository: UserDataRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel<SettingsUiState, Nothing, SettingsAction>(SettingsUiState.Loading) {

    /** Backward-compat alias for the inherited [stateFlow] — the dialogs read this. */
    val settingsUiState: StateFlow<SettingsUiState> get() = stateFlow

    init {
        settingsRepository.userData
            .onEach { userData ->
                updateState {
                    SettingsUiState.Success(
                        settings = UserEditableSettings(
                            brand = userData.themeBrand,
                            useDynamicColor = userData.useDynamicColor,
                            darkThemeConfig = userData.darkThemeConfig,
                            language = userData.appLanguage,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.UpdateThemeBrand -> viewModelScope.launch {
                analyticsHelper.logThemeBrandChanged(action.themeBrand)
                settingsRepository.setThemeBrand(action.themeBrand)
            }
            is SettingsAction.UpdateDarkThemeConfig -> viewModelScope.launch {
                analyticsHelper.logThemeChanged(action.darkThemeConfig)
                settingsRepository.setDarkThemeConfig(action.darkThemeConfig)
            }
            is SettingsAction.UpdateDynamicColor -> viewModelScope.launch {
                analyticsHelper.logDynamicColorPreferences(action.useDynamicColor)
                settingsRepository.setDynamicColorPreference(action.useDynamicColor)
            }
            is SettingsAction.UpdateLanguage -> viewModelScope.launch {
                analyticsHelper.logLanguageChanged(action.language)
                settingsRepository.setLanguage(action.language)
            }
        }
    }

    /** Convenience emitter — see [SettingsAction.UpdateThemeBrand]. */
    fun updateThemeBrand(themeBrand: ThemeBrand) = trySendAction(SettingsAction.UpdateThemeBrand(themeBrand))

    /** Convenience emitter — see [SettingsAction.UpdateDarkThemeConfig]. */
    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) =
        trySendAction(SettingsAction.UpdateDarkThemeConfig(darkThemeConfig))

    /** Convenience emitter — see [SettingsAction.UpdateDynamicColor]. */
    fun updateDynamicColorPreference(useDynamicColor: Boolean) =
        trySendAction(SettingsAction.UpdateDynamicColor(useDynamicColor))

    /** Convenience emitter — see [SettingsAction.UpdateLanguage]. */
    fun updateLanguage(language: LanguageConfig) = trySendAction(SettingsAction.UpdateLanguage(language))
}

/** Preference-mutation intents accepted by [SettingsViewModel]. */
sealed interface SettingsAction {
    /** Change the theme brand (Android / default). */
    data class UpdateThemeBrand(val themeBrand: ThemeBrand) : SettingsAction

    /** Change the dark-theme configuration (system / light / dark). */
    data class UpdateDarkThemeConfig(val darkThemeConfig: DarkThemeConfig) : SettingsAction

    /** Toggle Material You dynamic color. */
    data class UpdateDynamicColor(val useDynamicColor: Boolean) : SettingsAction

    /** Change the app language. */
    data class UpdateLanguage(val language: LanguageConfig) : SettingsAction
}

data class UserEditableSettings(
    val brand: ThemeBrand,
    val useDynamicColor: Boolean,
    val darkThemeConfig: DarkThemeConfig,
    val language: LanguageConfig,
)

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}

private fun AnalyticsHelper.logThemeBrandChanged(themeBrand: ThemeBrand) {
    logEvent(
        type = "theme_brand_changed",
        params = mapOf(
            "theme_brand" to themeBrand.name,
        ),
    )
}

private fun AnalyticsHelper.logDynamicColorPreferences(useDynamicColor: Boolean) {
    logEvent(
        type = "dynamic_color_preference_changed",
        params = mapOf(
            "use_dynamic_color" to useDynamicColor.toString(),
        ),
    )
}

private fun AnalyticsHelper.logThemeChanged(themeConfig: DarkThemeConfig) {
    logEvent(
        type = "dark_theme_config_changed",
        params = mapOf(
            "dark_theme_config" to themeConfig.name,
        ),
    )
}

private fun AnalyticsHelper.logLanguageChanged(language: LanguageConfig) {
    logEvent(
        type = "language_changed",
        params = mapOf(
            "language" to (language.localeName ?: "system_default"),
        ),
    )
}
