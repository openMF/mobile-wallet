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
import org.koin.compose.koinInject
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.screen.PasscodeScreen

@Composable
fun MifosPasscode(
    onForgotButton: () -> Unit,
    onSkipButton: () -> Unit,
    onPasscodeConfirm: () -> Unit,
    onPasscodeCreation: () -> Unit,
    onPasscodeRejected: () -> Unit,
) {
    val passcodeManager: PasscodeManager = koinInject<PasscodeManager>()

    PasscodeScreen(
        passcodeManager,
        onForgotButton,
        onSkipButton,
        onPasscodeConfirm,
        onPasscodeCreation,
        onPasscodeRejected,
    )
}
