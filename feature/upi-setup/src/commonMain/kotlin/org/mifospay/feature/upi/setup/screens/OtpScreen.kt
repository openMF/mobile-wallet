/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.upi.setup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import mifos_pay.feature.upi_setup.generated.resources.Res
import mifos_pay.feature.upi_setup.generated.resources.feature_upi_setup_enter_otp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.OtpTextField
import org.mifospay.core.ui.VerifyStepHeader
import template.core.base.designsystem.theme.KptTheme

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
                top = KptTheme.spacing.md,
                bottom = KptTheme.spacing.md,
                start = KptTheme.spacing.sm,
                end = KptTheme.spacing.sm,
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = KptTheme.elevation.level1,
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
            color = KptTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            style = KptTheme.typography.headlineMedium,
        )
        OtpTextField(
            onOtpTextCorrectlyEntered = {
                onOtpTextCorrectlyEntered()
            },
            modifier = Modifier.padding(top = KptTheme.spacing.lg),
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
