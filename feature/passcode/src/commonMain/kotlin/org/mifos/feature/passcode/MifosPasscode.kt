/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.feature.passcode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.launch
import mobile_wallet.feature.passcode.generated.resources.Res
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_error
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_ok
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_user_not_registered_error_message
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.mifos.authenticator.biometrics.platformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthenticationResult
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifos.authenticator.biometrics.platformAvailableAuthenticationOption
import org.mifos.authenticator.passcode.PasscodeAction
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeStep
import org.mifos.authenticator.passcode.PasscodeStorageAdapter
import org.mifos.authenticator.passcode.screen.PasscodeAppearanceConfig
import org.mifos.authenticator.passcode.screen.PasscodeButtonConfig
import org.mifos.authenticator.passcode.screen.PasscodeDialogConfig
import org.mifos.authenticator.passcode.screen.PasscodeDotConfig
import org.mifos.authenticator.passcode.screen.PasscodeKeyConfig
import org.mifos.authenticator.passcode.screen.PasscodeLogoConfig
import org.mifos.authenticator.passcode.screen.PasscodeScreen
import org.mifos.authenticator.passcode.screen.PasscodeSwitchConfig
import org.mifospay.core.data.repository.AppLockRepository
import org.mifospay.core.designsystem.component.MifosDialogBox
import template.core.base.designsystem.theme.KptTheme

internal object MifosPasscodeCurrentInfo : NavigationEventInfo()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MifosPasscode(
    onAuthenticationSuccess: () -> Unit,
    onForgotButton: () -> Unit = {},
    onPasscodeCreation: () -> Unit = {},
    onPasscodeRejected: () -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    appLockRepository: AppLockRepository = koinInject(),
    passcodeStorageAdapter: PasscodeStorageAdapter = koinInject(),
) {
    val passcodeManager: PasscodeManager = koinInject<PasscodeManager>()
    val state by passcodeManager.state.collectAsStateWithLifecycle()

    val systemAuthProvider = platformAuthenticationProvider.current
    val systemAvailableAuthOption = platformAvailableAuthenticationOption.current
    val coroutineScope = rememberCoroutineScope()

    val biometricsStatus by systemAuthProvider.authenticatorStatus.collectAsState()

    val userNotRegisteredErrorMessage = stringResource(Res.string.feature_authenticator_user_not_registered_error_message)
    var dialogBoxType by remember { mutableStateOf(DialogBoxType.None) }
    var showDialogBox by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf<String?>(null) }
    var showBiometricsKeyButton by remember { mutableStateOf(true) }

    val navEventState = rememberNavigationEventState(
        currentInfo = MifosPasscodeCurrentInfo,
    )
    NavigationBackHandler(
        state = navEventState,
        isBackEnabled = true,
        onBackCancelled = { },
        onBackCompleted = { },
    )

    val lifeCycleObserver = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifeCycleObserver) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (state.passcodeStep == PasscodeStep.Enter) appLockRepository.lockApp()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (
                        biometricsStatus.contains(PlatformAuthenticatorStatus.BIOMETRICS_SET) &&
                        state.passcodeStep == PasscodeStep.Enter
                    ) {
                        coroutineScope.launch {
                            val result =
                                passcodeStorageAdapter.loadRegistrationData()?.let { data ->
                                    systemAuthProvider.onAuthenticatorClick(
                                        appName = "Mifos Pay",
                                        savedRegistrationData = data,
                                    )
                                }
                            when (result) {
                                is AuthenticationResult.Error -> {
                                    dialogBoxType = DialogBoxType.BiometricError
                                    showDialogBox = true
                                    dialogMessage = result.message
                                }

                                AuthenticationResult.Success -> {
                                    passcodeManager.trySendAction(PasscodeAction.BiometricUnlockSuccess)
                                }

                                AuthenticationResult.UserNotRegistered -> {
                                    dialogBoxType = DialogBoxType.UserBiometricsNotRegistered
                                    showDialogBox = true
                                    dialogMessage = userNotRegisteredErrorMessage
                                }

                                null -> {}
                            }
                        }
                    }
                }
                else -> {}
            }
        }
        lifeCycleObserver.addObserver(observer)
        onDispose { lifeCycleObserver.removeObserver(observer) }
    }

    MifosDialogBox(
        title = stringResource(Res.string.feature_authenticator_error),
        showDialogState = showDialogBox,
        confirmButtonText = stringResource(Res.string.feature_authenticator_ok),
        dismissButtonText = null,
        onConfirm = {
            showDialogBox = false
            dialogMessage = null
            when (dialogBoxType) {
                DialogBoxType.UserBiometricsNotRegistered -> {
                    passcodeManager.trySendAction(PasscodeAction.BiometricUserNotRegistered)
                    showBiometricsKeyButton = false
                }
                else -> {}
            }
            dialogBoxType = DialogBoxType.None
        },
        onDismiss = {
            showDialogBox = false
            dialogMessage = null
            dialogBoxType = DialogBoxType.None
        },
        message = dialogMessage,
    )

    PasscodeScreen(
        passcodeManager = passcodeManager,
        onForgotButton = {
            appLockRepository.unlockApp()
            onForgotButton()
        },
        // onPasscodeConfirm will be renamed to onAuthenticationSuccess in next update to the passcode library.
        // It is the commonCallBack function for successful biometrics and passcode authentication.
        onPasscodeConfirm = {
            appLockRepository.unlockApp()
            onAuthenticationSuccess()
        },
        onPasscodeCreation = {
            appLockRepository.unlockApp()
            onPasscodeCreation()
        },
        onPasscodeChanged = onPasscodeChanged,
        onPasscodeRejected = {
            if(state.passcodeStep == PasscodeStep.Enter) {
                onPasscodeRejected()
            }
        },
        appearanceConfig = PasscodeAppearanceConfig(
            backgroundColor = KptTheme.colorScheme.background,
            headerTextStyle = KptTheme.typography.headlineMedium,
        ),
        logoConfig = PasscodeLogoConfig(),
        dotConfig = PasscodeDotConfig(
            dotColor = KptTheme.colorScheme.primary,
            inactiveDotColor = KptTheme.colorScheme.onBackground,
            visiblePasscodeTextStyle = KptTheme.typography.headlineSmall,
        ),
        biometricButton = { modifier ->
            AnimatedVisibility(showBiometricsKeyButton) {
                BiometricsKey(
                    modifier = modifier,
                    passcodeManager = passcodeManager,
                    coroutineScope = coroutineScope,
                    onUserNotRegistered = {
                        dialogBoxType = DialogBoxType.UserBiometricsNotRegistered
                        showDialogBox = true
                        dialogMessage = userNotRegisteredErrorMessage
                    },
                    systemAuthProvider = systemAuthProvider,
                    systemAvailableAuthOption = systemAvailableAuthOption,
                )
            }
        },
        onBiometricError = {
            dialogBoxType = DialogBoxType.BiometricError
            showDialogBox = true
            dialogMessage = it
        },
        keyConfig = PasscodeKeyConfig(
            shouldShuffleKeys = true,
            keyTextStyle = null,
            keyColor = KptTheme.colorScheme.primary,
            keyShape = CircleShape,
            keyElevation = null,
            keyContainerColor = KptTheme.colorScheme.surface,
            keySize = 60.dp,
        ),
        buttonConfig = PasscodeButtonConfig(
            skipButtonTextStyle = KptTheme.typography.labelLarge,
            forgotButtonTextStyle = KptTheme.typography.labelLarge,
        ),
        switchConfig = PasscodeSwitchConfig(
            switchTabColor = KptTheme.colorScheme.primary,
            switchEnabledColor = KptTheme.colorScheme.surfaceContainerHighest,
            switchEnabledTextColor = KptTheme.colorScheme.onSurface,
            switchDisabledTextColor = KptTheme.colorScheme.surface,
            switchTextStyle = null,
        ),
        dialogConfig = PasscodeDialogConfig(
            dialogContainerColor = KptTheme.colorScheme.surface,
            dialogTitleColor = KptTheme.colorScheme.onSurface,
            dialogButtonTextColor = KptTheme.colorScheme.onSurface,
            dialogShape = null,
        ),
    )
}

enum class DialogBoxType {
    UserBiometricsNotRegistered,
    BiometricError,
    None,
}
