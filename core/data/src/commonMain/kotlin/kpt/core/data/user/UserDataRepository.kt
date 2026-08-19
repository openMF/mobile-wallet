/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kpt.core.model.user.DarkThemeConfig
import kpt.core.model.user.LanguageConfig
import kpt.core.model.user.ThemeBrand
import kpt.core.model.user.UserData

/**
 * Repository interface for managing user preferences with reactive
 * capabilities.
 *
 * This interface provides reactive access to user preferences including
 * theme settings, dark mode configuration, and dynamic color preferences.
 */
interface UserDataRepository {

    val userData: StateFlow<UserData>

    val authToken: String?

    val passcode: String

    val observeLanguage: Flow<LanguageConfig>

    val observeDarkThemeConfig: Flow<DarkThemeConfig>

    val observeDynamicColorPreference: Flow<Boolean>

    val observeScreenCapturePreference: Flow<Boolean>

    suspend fun setLanguage(language: LanguageConfig)

    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    suspend fun setIsAuthenticated(isAuthenticated: Boolean)

    suspend fun setIsUnlocked(isUnlocked: Boolean)

    suspend fun setIsPasscodeEnabled(isPasscodeEnabled: Boolean)

    suspend fun setIsBiometricsEnabled(isBiometricsEnabled: Boolean)

    suspend fun setShowOnboarding(showOnboarding: Boolean)

    suspend fun setFirstTimeState(firstTimeState: Boolean)

    suspend fun setPasscode(passcode: String)

    suspend fun clearUserData()
}
