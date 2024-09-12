package com.mifos.passcode

import androidx.compose.ui.window.ComposeUIViewController
import com.mifos.passcode.utility.BioMetricUtil
import com.mifos.passcode.component.PasscodeScreen
import com.mifos.passcode.viewmodels.BiometricAuthorizationViewModel
import platform.UIKit.UIViewController

fun MainViewController(
    bioMetricUtil: BioMetricUtil,
    biometricViewModel: BiometricAuthorizationViewModel
): UIViewController = ComposeUIViewController {
    PasscodeScreen(
        onPasscodeConfirm = {
        },
        onSkipButton = {
        },
        onForgotButton = {},
        onPasscodeRejected = {},
        bioMetricUtil = bioMetricUtil,
        biometricAuthorizationViewModel = biometricViewModel,
        onBiometricAuthSuccess = {
         },
        enableBiometric = true
    )
}