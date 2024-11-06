/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.upi.setup.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.common.Constants
import org.mifospay.core.designsystem.theme.MifosTheme

@Composable
internal fun SetUpUpiScreenContent(
    type: String,
    otpText: String,
    correctlySettingUpi: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        if (type == Constants.CHANGE) {
            ChangeUpi(
                otpText = otpText,
                correctlySettingUpi = correctlySettingUpi,
                modifier = modifier,
            )
        } else {
            SettingAndForgotUpi(
                correctlySettingUpi,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SettingAndForgotUpi(
    correctlySettingUpi: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var debitCardVerified by rememberSaveable { mutableStateOf(false) }
    var otpVerified by rememberSaveable { mutableStateOf(false) }
    var debitCardScreenVisible by rememberSaveable { mutableStateOf(true) }
    var otpScreenVisible by rememberSaveable { mutableStateOf(false) }
    var upiPinScreenVisible by rememberSaveable { mutableStateOf(false) }
    var upiPinScreenVerified by rememberSaveable { mutableStateOf(false) }
    var realOtp by rememberSaveable { mutableStateOf("") }

    Column(modifier) {
        DebitCardScreen(
            verificationStatus = debitCardVerified,
            isContentVisible = debitCardScreenVisible,
            onDebitCardVerified = {
                debitCardVerified = true
                otpScreenVisible = true
                realOtp = it
                debitCardScreenVisible = false
            },
            onDebitCardVerificationFailed = {
            },
        )
        OtpScreen(
            verificationStatus = otpVerified,
            contentVisibility = otpScreenVisible,
            realOtp = realOtp,
            onOtpTextCorrectlyEntered = {
                otpScreenVisible = false
                otpVerified = true
                upiPinScreenVisible = true
            },
        )
        UpiPinScreen(
            correctlySettingUpi = {
                upiPinScreenVerified = true
                correctlySettingUpi(it)
            },
            contentVisibility = upiPinScreenVisible,
        )
    }
}

@Composable
private fun ChangeUpi(
    otpText: String,
    correctlySettingUpi: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var otpVerified by rememberSaveable { mutableStateOf(false) }
    var otpScreenVisible by rememberSaveable { mutableStateOf(true) }
    var upiPinScreenVisible by rememberSaveable { mutableStateOf(false) }
    var upiPinScreenVerified by rememberSaveable { mutableStateOf(false) }
    val realOtp by rememberSaveable { mutableStateOf(otpText) }

    Column(modifier) {
        OtpScreen(
            verificationStatus = otpVerified,
            contentVisibility = otpScreenVisible,
            realOtp = realOtp,
            onOtpTextCorrectlyEntered = {
                otpScreenVisible = false
                otpVerified = true
                upiPinScreenVisible = true
                otpScreenVisible = false
            },
        )

        UpiPinScreen(
            correctlySettingUpi = {
                upiPinScreenVerified = true
                correctlySettingUpi(it)
            },
            contentVisibility = upiPinScreenVisible,
        )
    }
}

@Preview
@Composable
private fun PreviewSetUpUpiPin() {
    MifosTheme {
        SetUpUpiScreenContent(
            type = Constants.SETUP,
            otpText = "907889",
            correctlySettingUpi = {},
        )
    }
}

@Preview
@Composable
private fun PreviewForgetUpiPin() {
    MifosTheme {
        SetUpUpiScreenContent(
            type = Constants.FORGOT,
            otpText = "907889",
            correctlySettingUpi = {},
        )
    }
}

@Preview
@Composable
private fun PreviewChangeUpiPin() {
    MifosTheme {
        SetUpUpiScreenContent(
            type = Constants.CHANGE,
            otpText = "907889",
            correctlySettingUpi = {},
        )
    }
}
