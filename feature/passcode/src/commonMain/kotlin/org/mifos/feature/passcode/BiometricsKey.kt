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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthenticationResult
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthOptions
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAvailableAuthenticationOption
import org.mifos.authenticator.passcode.PasscodeAction
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeStorageAdapter
import org.mifos.authenticator.passcode.components.PasscodeKey
import org.mifos.authenticator.passcode.screen.PasscodeKeyConfig
import template.core.base.designsystem.theme.KptTheme

@Composable
fun BiometricsKey(
    passcodeManager: PasscodeManager,
    systemAuthProvider: PlatformAuthenticationProvider,
    systemAvailableAuthOption: PlatformAvailableAuthenticationOption,
    coroutineScope: CoroutineScope,
    onUserNotRegistered: () -> Unit,
    modifier: Modifier = Modifier,
    passcodeStorageAdapter: PasscodeStorageAdapter = koinInject(),
) {
    val authOptions by systemAvailableAuthOption.currentAuthOption.collectAsStateWithLifecycle()
    val state by passcodeManager.state.collectAsStateWithLifecycle()

    val passcodeKeyConfig = PasscodeKeyConfig(
        shouldShuffleKeys = true,
        keyTextStyle = null,
        keyColor = KptTheme.colorScheme.primary,
        keyShape = CircleShape,
        keyElevation = null,
        keyContainerColor = KptTheme.colorScheme.surface,
        keySize = 60.dp,
    )

    if (state.isBiometricEnabled) {
        val icon: ImageVector = when {
            authOptions.contains(PlatformAuthOptions.Fingerprint) -> Icons.Default.Fingerprint
            authOptions.contains(PlatformAuthOptions.FaceId) -> Icons.Default.Face
            else -> Icons.Default.Lock
        }

        PasscodeKey(
            modifier = modifier,
            keyIcon = icon,
            onClick = {
                coroutineScope.launch {
                    val result = systemAuthProvider.onAuthenticatorClick(
                        "Mifos Pay",
                        passcodeStorageAdapter.loadRegistrationData() ?: "",
                    )
                    when (result) {
                        is AuthenticationResult.Success -> {
                            passcodeManager.trySendAction(PasscodeAction.BiometricUnlockSuccess)
                        }

                        is AuthenticationResult.Error -> {
                            passcodeManager.trySendAction(
                                PasscodeAction.BiometricUnlockFailure(
                                    result.message,
                                ),
                            )
                        }

                        is AuthenticationResult.UserNotRegistered -> {
                            onUserNotRegistered()
                        }
                    }
                }
            },
            keyColor = passcodeKeyConfig.keyColor,
            shape = passcodeKeyConfig.keyShape,
            elevation = passcodeKeyConfig.keyElevation ?: CardDefaults.elevatedCardElevation(
                defaultElevation = 2.dp,
            ),
            containerColor = passcodeKeyConfig.keyContainerColor,
            size = passcodeKeyConfig.keySize,
        )
    }
}
