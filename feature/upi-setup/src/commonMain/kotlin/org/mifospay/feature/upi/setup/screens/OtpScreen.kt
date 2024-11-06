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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile_wallet.feature.upi_setup.generated.resources.Res
import mobile_wallet.feature.upi_setup.generated.resources.feature_upi_setup_enter_otp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.OtpTextField
import org.mifospay.core.ui.VerifyStepHeader

@Composable
internal fun OtpScreen(
    realOtp: String,
    onOtpTextCorrectlyEntered: () -> Unit,
    modifier: Modifier = Modifier,
    contentVisibility: Boolean = false,
    verificationStatus: Boolean = false,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 15.dp,
                bottom = 15.dp,
                start = 10.dp,
                end = 10.dp,
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VerifyStepHeader("OtpSetUp", verificationStatus)
            if (contentVisibility) {
                OtpScreenContent(realOtp, onOtpTextCorrectlyEntered)
            }
        }
    }
}

@Composable
private fun OtpScreenContent(
    realOtp: String,
    onOtpTextCorrectlyEntered: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.feature_upi_setup_enter_otp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            style = MaterialTheme.typography.headlineMedium,
        )
        OtpTextField(
            onOtpTextCorrectlyEntered = {
                onOtpTextCorrectlyEntered()
            },
            modifier = Modifier.padding(top = 20.dp),
            realOtp = realOtp,
        )
    }
}

@Preview
@Composable
private fun OtpScreenPreview() {
    MifosTheme {
        OtpScreen(
            realOtp = "1234",
            contentVisibility = true,
            verificationStatus = false,
            onOtpTextCorrectlyEntered = {},
        )
    }
}

@Preview
@Composable
private fun OtpScreenVerifiedPreview() {
    MifosTheme {
        OtpScreen(
            realOtp = "1234",
            contentVisibility = false,
            verificationStatus = true,
            onOtpTextCorrectlyEntered = {},
        )
    }
}
