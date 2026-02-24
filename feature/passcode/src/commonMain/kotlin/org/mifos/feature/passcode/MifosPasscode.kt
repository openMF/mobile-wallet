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
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.screen.PasscodeAppearanceConfig
import org.mifos.authenticator.passcode.screen.PasscodeButtonConfig
import org.mifos.authenticator.passcode.screen.PasscodeDialogConfig
import org.mifos.authenticator.passcode.screen.PasscodeDotConfig
import org.mifos.authenticator.passcode.screen.PasscodeKeyConfig
import org.mifos.authenticator.passcode.screen.PasscodeLogoConfig
import org.mifos.authenticator.passcode.screen.PasscodeScreen
import org.mifos.authenticator.passcode.screen.PasscodeSwitchConfig
import org.mifos.authenticator.passcode.screen.PasscodeToolbarConfig
import org.mifos.authenticator.passcode.theme.blueTint
import template.core.base.designsystem.theme.KptTheme

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
        toolbarConfig = PasscodeToolbarConfig(
            toolbarIndicatorActiveColor = KptTheme.colorScheme.primary,
            toolbarIndicatorInactiveColor = KptTheme.colorScheme.surfaceContainerHighest,
        ),
        dialogConfig = PasscodeDialogConfig(
            dialogContainerColor = KptTheme.colorScheme.surface,
            dialogTitleColor = KptTheme.colorScheme.onSurface,
            dialogButtonTextColor = KptTheme.colorScheme.onSurface,
            dialogShape = null,
        ),
    )
}
