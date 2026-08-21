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
import cmp.navigation.AppAction.Internal.UnauthorizedFlagChange
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
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeStep
import org.mifospay.core.common.GlobalAuthManager
import org.mifospay.core.data.repository.AppLockRepository
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/**
 * Root app-shell ViewModel scoped to [ComposeApp].
 *
 * Owns the four kinds of state the shell needs to make its cross-cutting
 * decisions:
 *
 *  - **Theme / display prefs** — the original template concerns
 *    (dark-theme / dynamic-color / screen-capture / locale), unchanged.
 *  - **401 forced-logout flag** ([AppState.showUnauthorizedDialog]) — mirrors
 *    the fork's [GlobalAuthManager.isUnauthorized]. `ComposeApp` reads this
 *    flag and renders [cmp.navigation.security.UnauthorizedDialogGate] when
 *    it flips `true`; on the user's "Ok" tap it calls [forcedLogout], which
 *    resets the flag and emits [AppEvent.LogoutRequested] for the outer
 *    shell (`SharedApp` in `:cmp-shared`) to fan out to the fork's
 *    `MifosPayViewModel.logOut()`.
 *  - **Background-lock timer** ([lastStopMark]) — monotonic-clock backing
 *    for the 15-second re-auth gate re-homed from `MifosPayApp.kt`. The
 *    lifecycle observer inside [cmp.navigation.security.AppLockGate] calls
 *    [markBackgrounded] on `ON_STOP` and [checkBackgroundLock] on `ON_RESUME`;
 *    a qualifying resume emits [AppEvent.ReAuthRequired] which `ComposeApp`
 *    then routes to `navigateToReAuthMifosPasscodeScreen`.
 *  - **Instance-selector overlay** ([AppState.showInstanceSelector]) — one-shot
 *    bottom-sheet flag preserving the fork's `RootNavGraph`
 *    `showInstanceSelector = true/false` pattern (see T5); the multi-tap
 *    gesture on `LoginScreen` calls [showInstanceSelectorSheet] and the
 *    sheet's dismiss callback calls [dismissInstanceSelectorSheet].
 *
 * Behaviors (a) background-lock, (b) 401 forced-logout, (c) start-destination
 * (`cmp.navigation.rootnav.RootNavViewModel` already handles this off
 * `UserPreferencesRepository.userInfo` + `GlobalAuthManager.isUnauthorized`),
 * and (d) biometric composition provider (wrap in
 * [cmp.navigation.security.PlatformAuthenticatorGate]) are the four shell
 * concerns re-homed per Phase 2 T4.
 *
 * DI dependencies added in this rev — [AppLockRepository] and [PasscodeManager]
 * — resolve via the fork's `RepositoryModule` + `MifosPasscodeModule`; Phase 2
 * T6/T7 wire those into `cmp.navigation.di.KoinModules.allModules`. Until then
 * a fresh app start surfaces a Koin `NoBeanDefFound` at composition, which is
 * the correct signal that DI reconciliation is incomplete (not a masked stub —
 * see RULE-IMPL-STUB-DETECTION-001).
 */
@OptIn(ExperimentalTime::class)
class AppViewModel(
    private val settingsRepository: UserDataRepository,
    private val garbageCollectionManager: GarbageCollectionManager,
    private val appLockRepository: AppLockRepository,
    private val passcodeManager: PasscodeManager,
) : BaseViewModel<AppState, AppEvent, AppAction>(
    initialState = AppState(
        darkTheme = false,
        isAndroidTheme = false,
        isDynamicColorsEnabled = false,
        isScreenCaptureAllowed = false,
        showUnauthorizedDialog = false,
        showInstanceSelector = false,
    ),
) {
    /**
     * Monotonic-clock mark of the most recent `ON_STOP`. `null` means either
     * "no stop recorded yet" or "already consumed on a prior `ON_RESUME`" —
     * both cases mean the next `ON_RESUME` must skip the re-auth check.
     * Monotonic (not wall-clock) is used to defeat device-clock rewind attacks.
     */
    private var lastStopMark: TimeSource.Monotonic.ValueTimeMark? = null

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

        // Fork's 401 flag → local shell state. Behavior (b), plumbed via a
        // trySendAction so state mutation stays inside handleAction.
        // NOTE: no `.distinctUntilChanged()` — StateFlow already conflates equal values
        // (Operator Fusion; the operator was deprecated to error in Kotlin 2.x on StateFlow).
        GlobalAuthManager.isUnauthorized
            .onEach { trySendAction(UnauthorizedFlagChange(it)) }
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

            is UnauthorizedFlagChange -> handleUnauthorizedFlagChange(action)
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

    private fun handleUnauthorizedFlagChange(action: UnauthorizedFlagChange) {
        mutableStateFlow.update { it.copy(showUnauthorizedDialog = action.isUnauthorized) }
    }

    /**
     * Called by [cmp.navigation.security.AppLockGate] on
     * `Lifecycle.Event.ON_STOP`. Records the monotonic-clock instant so the
     * paired [checkBackgroundLock] on the next `ON_RESUME` can compute the
     * elapsed background duration.
     */
    fun markBackgrounded() {
        lastStopMark = TimeSource.Monotonic.markNow()
    }

    /**
     * Called by [cmp.navigation.security.AppLockGate] on
     * `Lifecycle.Event.ON_RESUME`.
     *
     * If **all** of the following hold, emits [AppEvent.ReAuthRequired]:
     *  - elapsed background duration exceeds [thresholdMs] milliseconds,
     *  - [appLockRepository]`.isAppLocked()` returned `false` — session
     *    exists and was actively unlocked (a `null` return means "no session
     *    yet" and must NOT trigger re-auth, matching the fork's
     *    `isAppLocked()?.let { if (!it) ... }` pattern),
     *  - [passcodeManager]`.state.value.passcodeStep == PasscodeStep.Enter`
     *    — the passcode UI is in unlock mode, not creating / changing.
     *
     * The stored mark is cleared unconditionally so a subsequent `ON_RESUME`
     * without a paired `ON_STOP` won't re-fire.
     *
     * @param thresholdMs Background-duration threshold in milliseconds
     *        (default `15_000L` — matches the fork's `MifosPayApp` 15 s gate).
     */
    fun checkBackgroundLock(thresholdMs: Long = 15_000L) {
        val mark = lastStopMark
        lastStopMark = null
        if (mark != null) {
            val elapsedMs = mark.elapsedNow().inWholeMilliseconds
            val appLocked = appLockRepository.isAppLocked()
            if (elapsedMs > thresholdMs &&
                appLocked != null &&
                !appLocked &&
                passcodeManager.state.value.passcodeStep == PasscodeStep.Enter
            ) {
                sendEvent(AppEvent.ReAuthRequired)
            }
        }
    }

    /**
     * Called by [cmp.navigation.security.UnauthorizedDialogGate]'s "Ok" tap.
     * Resets [GlobalAuthManager.isUnauthorized] (which in turn flips
     * [AppState.showUnauthorizedDialog] back to `false` via the observer
     * wired in [init]) and emits [AppEvent.LogoutRequested] so the outer
     * shell (`SharedApp` in `:cmp-shared`, which owns `MifosPayViewModel`) can
     * fan out to `MifosPayViewModel.logOut()` + navigate to the login graph.
     * `RootNavViewModel` independently routes to `RootNavState.Auth` on the
     * same flag change — the event is only responsible for the imperative
     * session-clear + explicit nav pop.
     */
    fun forcedLogout() {
        GlobalAuthManager.reset()
        sendEvent(AppEvent.LogoutRequested)
    }

    /**
     * Dismisses the 401 dialog without logging out (kept for tests /
     * accidental-dismiss handling; the fork's `MifosPayApp` treats dismiss as
     * a no-op).
     */
    fun dismissUnauthorizedDialog() {
        mutableStateFlow.update { it.copy(showUnauthorizedDialog = false) }
    }

    /**
     * Show the fork's Supabase multi-instance selector as a bottom-sheet
     * overlay. Triggered by the login screen's multi-tap gesture (see
     * `feature/auth/.../LoginScreen.kt` `detectMultiTapGesture`) via a
     * lambda plumbed through the login graph.
     */
    fun showInstanceSelectorSheet() {
        mutableStateFlow.update { it.copy(showInstanceSelector = true) }
    }

    /** Called by `InstanceSelectorScreen.onDismiss` — hides the overlay. */
    fun dismissInstanceSelectorSheet() {
        mutableStateFlow.update { it.copy(showInstanceSelector = false) }
    }
}

data class AppState(
    val darkTheme: Boolean,
    val isAndroidTheme: Boolean,
    val isDynamicColorsEnabled: Boolean,
    val isScreenCaptureAllowed: Boolean,
    // Fork shell-security state re-homed from MifosPayApp.kt (Phase 2 T4).
    val showUnauthorizedDialog: Boolean = false,
    val showInstanceSelector: Boolean = false,
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

    /**
     * Emitted when [AppViewModel.checkBackgroundLock] qualifies a resume as
     * "long background + session actively unlocked + passcode in unlock step".
     * The outer shell (or `RootNavScreen`) routes to the re-auth passcode
     * screen.
     */
    data object ReAuthRequired : AppEvent

    /**
     * Emitted from [AppViewModel.forcedLogout] after resetting the 401 flag.
     * The outer shell calls the fork's `MifosPayViewModel.logOut()` and
     * navigates to the login graph in response.
     */
    data object LogoutRequested : AppEvent
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

        /**
         * Mirrors [GlobalAuthManager.isUnauthorized] into
         * [AppState.showUnauthorizedDialog].
         */
        data class UnauthorizedFlagChange(
            val isUnauthorized: Boolean,
        ) : Internal()
    }
}
