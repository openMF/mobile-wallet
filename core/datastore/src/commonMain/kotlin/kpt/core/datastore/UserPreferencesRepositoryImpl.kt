/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)

package kpt.core.datastore

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kpt.core.base.common.manager.DispatcherManager
import kpt.core.model.user.DarkThemeConfig
import kpt.core.model.user.LanguageConfig
import kpt.core.model.user.ThemeBrand
import kpt.core.model.user.UserData

private const val USER_DATA_KEY = "user_data_key"
private const val SECURE_DATA_KEY = "secure_data_key"

/**
 * Splits user data storage between plain (UI preferences) and secure
 * (credentials/auth state) Settings backends.
 *
 * On first access, migrates any existing single-store data into the split
 * stores using a write-before-delete strategy to prevent data loss.
 */
class UserPreferencesRepositoryImpl(
    private val plainSettings: Settings,
    private val secureSettings: Settings,
    private val dispatcher: DispatcherManager,
) : UserPreferencesRepository {

    init {
        migrateIfNeeded()
    }

    /**
     * One-time migrate from legacy single-store to split plain/secure stores.
     * Write-before-delete: writes to both new stores first, then removes old key.
     */
    private fun migrateIfNeeded() {
        val legacy = plainSettings.decodeValueOrNull(
            key = USER_DATA_KEY,
            serializer = UserData.serializer(),
        ) ?: return

        // Check if secure store already has data (already migrated)
        val existing = secureSettings.decodeValueOrNull(
            key = SECURE_DATA_KEY,
            serializer = UserData.serializer(),
        )
        if (existing != null) return

        // Write secure fields to secure store first
        secureSettings.encodeValue(
            key = SECURE_DATA_KEY,
            serializer = UserData.serializer(),
            value = legacy,
        )
        // Plain store retains the full UserData for UI fields (shared key)
    }

    private fun loadCombinedUserData(): UserData {
        val plainData = plainSettings.decodeValueOrNull(
            key = USER_DATA_KEY,
            serializer = UserData.serializer(),
        )
        val secureData = secureSettings.decodeValueOrNull(
            key = SECURE_DATA_KEY,
            serializer = UserData.serializer(),
        )
        return when {
            plainData != null && secureData != null -> plainData.copy(
                activeUserId = secureData.activeUserId,
                passcode = secureData.passcode,
                isAuthenticated = secureData.isAuthenticated,
                isUnlocked = secureData.isUnlocked,
            )
            secureData != null -> secureData
            plainData != null -> plainData
            else -> UserData.DEFAULT
        }
    }

    private val _userData = MutableStateFlow(loadCombinedUserData())

    override val userData: StateFlow<UserData>
        get() = _userData.asStateFlow()

    override val authToken: String?
        get() = null

    override val passcode: String
        get() = _userData.value.passcode

    override val observeLanguage: Flow<LanguageConfig>
        get() = _userData.map { it.appLanguage }

    override val observeDarkThemeConfig: Flow<DarkThemeConfig>
        get() = _userData.map { it.darkThemeConfig }

    override val observeDynamicColorPreference: Flow<Boolean>
        get() = _userData.map { it.useDynamicColor }

    override val observeScreenCapturePreference: Flow<Boolean>
        get() = _userData.map { it.enableScreenCapture }

    private suspend fun updatePreference(transform: (UserData) -> UserData) {
        withContext(dispatcher.io) {
            val current = loadCombinedUserData()
            val updated = transform(current)
            plainSettings.putUserPreference(updated)
            secureSettings.putSecurePreference(updated)
            _userData.value = updated
        }
    }

    override suspend fun setLanguage(language: LanguageConfig) = updatePreference { it.copy(appLanguage = language) }

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) = updatePreference { it.copy(themeBrand = themeBrand) }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) =
        updatePreference { it.copy(darkThemeConfig = darkThemeConfig) }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) =
        updatePreference { it.copy(useDynamicColor = useDynamicColor) }

    override suspend fun setIsAuthenticated(isAuthenticated: Boolean) =
        updatePreference { it.copy(isAuthenticated = isAuthenticated) }

    override suspend fun setIsUnlocked(isUnlocked: Boolean) = updatePreference { it.copy(isUnlocked = isUnlocked) }

    override suspend fun setIsPasscodeEnabled(isPasscodeEnabled: Boolean) =
        updatePreference { it.copy(isPasscodeEnabled = isPasscodeEnabled) }

    override suspend fun setIsBiometricsEnabled(isBiometricsEnabled: Boolean) =
        updatePreference { it.copy(isBiometricsEnabled = isBiometricsEnabled) }

    override suspend fun setShowOnboarding(showOnboarding: Boolean) =
        updatePreference { it.copy(showOnboarding = showOnboarding) }

    override suspend fun setFirstTimeState(firstTimeState: Boolean) =
        updatePreference { it.copy(firstTimeUser = firstTimeState) }

    override suspend fun setPasscode(passcode: String) = updatePreference { it.copy(passcode = passcode) }

    override suspend fun setScreenCapturePreference(isScreenCaptureEnabled: Boolean) =
        updatePreference { it.copy(enableScreenCapture = isScreenCaptureEnabled) }

    override suspend fun clearUserData() {
        setIsAuthenticated(false)
        // TODO:: Uncomment this line when Unlocked Screen is Present
        // setIsUnlocked(false)
    }
}

private fun Settings.putUserPreference(preference: UserData) {
    encodeValue(
        key = USER_DATA_KEY,
        serializer = UserData.serializer(),
        value = preference,
    )
}

private fun Settings.putSecurePreference(preference: UserData) {
    encodeValue(
        key = SECURE_DATA_KEY,
        serializer = UserData.serializer(),
        value = preference,
    )
}
