/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation

import androidx.lifecycle.viewModelScope
import cmp.navigation.AppAction.Internal.DynamicColorsUpdate
import cmp.navigation.AppAction.Internal.ScreenCaptureUpdate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kpt.core.base.platform.garbage.GarbageCollectionManager
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.user.UserDataRepository
import kpt.core.model.user.DarkThemeConfig
import kpt.core.model.user.LanguageConfig

class AppViewModel(
    private val settingsRepository: UserDataRepository,
    private val garbageCollectionManager: GarbageCollectionManager,
) : BaseViewModel<AppState, AppEvent, AppAction>(
    initialState = AppState(
        darkTheme = false,
        isAndroidTheme = false,
        isDynamicColorsEnabled = false,
        isScreenCaptureAllowed = false,
    ),
) {
    init {
        settingsRepository
            .observeDarkThemeConfig
            .onEach { trySendAction(AppAction.Internal.ThemeUpdate(it)) }
            .launchIn(viewModelScope)

        settingsRepository
            .observeDynamicColorPreference
            .onEach { trySendAction(DynamicColorsUpdate(it)) }
            .launchIn(viewModelScope)

        settingsRepository
            .observeScreenCapturePreference
            .onEach { trySendAction(ScreenCaptureUpdate(it)) }
            .launchIn(viewModelScope)

        settingsRepository
            .observeLanguage
            .distinctUntilChanged()
            .map { AppEvent.UpdateAppLocale(it.localeName) }
            .onEach(::sendEvent)
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: AppAction) {
        when (action) {
            is AppAction.AppSpecificLanguageUpdate -> handleAppSpecificLanguageUpdate(action)

            is ScreenCaptureUpdate -> handleScreenCaptureUpdate(action)

            is AppAction.Internal.ThemeUpdate -> handleAppThemeUpdated(action)

            is DynamicColorsUpdate -> handleDynamicColorsUpdate(action)

            is AppAction.Internal.CurrentUserStateChange -> handleCurrentUserStateChange()

            is AppAction.Internal.UserUnlockStateChange -> handleUserUnlockStateChange()
        }
    }

    private fun handleAppSpecificLanguageUpdate(action: AppAction.AppSpecificLanguageUpdate) {
        viewModelScope.launch {
            settingsRepository.setLanguage(action.appLanguage)
        }
    }

    private fun handleScreenCaptureUpdate(action: ScreenCaptureUpdate) {
        mutableStateFlow.update { it.copy(isScreenCaptureAllowed = action.isScreenCaptureEnabled) }
    }

    private fun handleAppThemeUpdated(action: AppAction.Internal.ThemeUpdate) {
        mutableStateFlow.update {
            it.copy(darkTheme = action.theme == DarkThemeConfig.DARK)
        }
        sendEvent(AppEvent.UpdateAppTheme(osValue = action.theme.osValue))
    }

    private fun handleDynamicColorsUpdate(action: DynamicColorsUpdate) {
        mutableStateFlow.update { it.copy(isDynamicColorsEnabled = action.isDynamicColorsEnabled) }
    }

    private fun handleUserUnlockStateChange() {
        recreateUiAndGarbageCollect()
    }

    private fun handleCurrentUserStateChange() {
        recreateUiAndGarbageCollect()
    }

    private fun recreateUiAndGarbageCollect() {
        sendEvent(AppEvent.Recreate)
        garbageCollectionManager.tryCollect()
    }
}

data class AppState(
    val darkTheme: Boolean,
    val isAndroidTheme: Boolean,
    val isDynamicColorsEnabled: Boolean,
    val isScreenCaptureAllowed: Boolean,
)

sealed interface AppEvent {
    data object Recreate : AppEvent

    data class ShowToast(val message: String) : AppEvent

    data class UpdateAppLocale(
        val localeName: String?,
    ) : AppEvent

    data class UpdateAppTheme(
        val osValue: Int,
    ) : AppEvent
}

sealed interface AppAction {
    data class AppSpecificLanguageUpdate(val appLanguage: LanguageConfig) : AppAction

    sealed class Internal : AppAction {

        data object CurrentUserStateChange : Internal()

        data class ScreenCaptureUpdate(
            val isScreenCaptureEnabled: Boolean,
        ) : Internal()

        data class ThemeUpdate(
            val theme: DarkThemeConfig,
        ) : Internal()

        data object UserUnlockStateChange : Internal()

        data class DynamicColorsUpdate(
            val isDynamicColorsEnabled: Boolean,
        ) : Internal()
    }
}
