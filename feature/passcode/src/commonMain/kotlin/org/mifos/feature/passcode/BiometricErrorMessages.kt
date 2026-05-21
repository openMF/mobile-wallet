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

import androidx.compose.runtime.Composable
import mobile_wallet.feature.passcode.generated.resources.Res
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_hardware_unavailable
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_invalid_arguments_auth
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_invalid_arguments_registration
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_invalid_registration_data
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_lockout
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_no_space
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_not_enrolled
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_timeout
import mobile_wallet.feature.passcode.generated.resources.feature_authenticator_biometric_error_unknown
import org.jetbrains.compose.resources.stringResource
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthStage
import org.mifos.authenticator.biometrics.platformAuthenticator.BiometricError

/**
 * Pre-resolved localized strings for each [BiometricError] case, bundled so
 * callers can map errors to strings outside a `@Composable` context — e.g.
 * inside `viewModelScope.launch { provider.onAuthenticatorClick(...) }` where
 * `stringResource(...)` is unavailable.
 *
 * Construct via [rememberBiometricErrorMessages] in a Composable, then pass
 * the instance into ViewModel actions that drive the platform authenticator.
 *
 * Mirrors the upstream sample's pattern (cmp-sample-shared's
 * `BiometricErrorMessages`) — the v2.3.0-beta biometrics library
 * deliberately ships no error copy, leaving message text to the consumer.
 */
class BiometricErrorMessages(
    val lockout: String,
    val hardwareUnavailable: String,
    val notEnrolled: String,
    val timeout: String,
    val noSpace: String,
    val invalidRegistrationData: String,
    val invalidArgumentsAuth: String,
    val invalidArgumentsRegistration: String,
    val unknown: String,
) {
    /**
     * Maps a [BiometricError] from the library to a pre-resolved localized
     * string. `Lockout` and `LockoutPermanent` collapse to one user-facing
     * message; `InvalidArguments` splits by [AuthStage]; `Unknown` surfaces
     * the generic copy without the inner platform message (avoids leaking
     * device-locale text into an app-locale UI).
     */
    fun localize(error: BiometricError): String = when (error) {
        BiometricError.Lockout, BiometricError.LockoutPermanent -> lockout
        BiometricError.HardwareUnavailable -> hardwareUnavailable
        BiometricError.NotEnrolled -> notEnrolled
        BiometricError.Timeout -> timeout
        BiometricError.NoSpace -> noSpace
        BiometricError.InvalidRegistrationData -> invalidRegistrationData
        is BiometricError.InvalidArguments -> when (error.stage) {
            AuthStage.Authentication -> invalidArgumentsAuth
            AuthStage.Registration -> invalidArgumentsRegistration
        }
        is BiometricError.Unknown -> unknown
    }
}

/**
 * Composable factory for [BiometricErrorMessages] — reads all nine
 * `feature_authenticator_biometric_error_*` keys from compose resources at
 * the current locale and bundles them into a single object the ViewModel can
 * carry across `viewModelScope.launch` boundaries.
 */
@Composable
fun rememberBiometricErrorMessages(): BiometricErrorMessages = BiometricErrorMessages(
    lockout = stringResource(Res.string.feature_authenticator_biometric_error_lockout),
    hardwareUnavailable = stringResource(Res.string.feature_authenticator_biometric_error_hardware_unavailable),
    notEnrolled = stringResource(Res.string.feature_authenticator_biometric_error_not_enrolled),
    timeout = stringResource(Res.string.feature_authenticator_biometric_error_timeout),
    noSpace = stringResource(Res.string.feature_authenticator_biometric_error_no_space),
    invalidRegistrationData = stringResource(Res.string.feature_authenticator_biometric_error_invalid_registration_data),
    invalidArgumentsAuth = stringResource(Res.string.feature_authenticator_biometric_error_invalid_arguments_auth),
    invalidArgumentsRegistration = stringResource(Res.string.feature_authenticator_biometric_error_invalid_arguments_registration),
    unknown = stringResource(Res.string.feature_authenticator_biometric_error_unknown),
)
