package org.mifospay.feature.upi_setup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifospay.core.ui.OtpTextField
import org.mifospay.core.ui.VerifyStepHeader
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.upi_setup.R


@Composable
fun OtpScreen(
    verificationStatus: Boolean = false,
    contentVisibility: Boolean = false,
    realOtp: String,
    onOtpTextCorrectlyEntered: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 15.dp,
                bottom = 15.dp,
                start = 10.dp,
                end = 10.dp
            ), elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerifyStepHeader("OtpSetUp", verificationStatus)
            if (contentVisibility) {
                OtpScreenContent(realOtp, onOtpTextCorrectlyEntered)
            }
        }
    }
}

@Composable
private fun OtpScreenContent(realOtp: String, onOtpTextCorrectlyEntered: () -> Unit) {
    Text(
        text = stringResource(id = R.string.feature_upi_setup_enter_otp),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 18.sp,
        style = MaterialTheme.typography.headlineMedium
    )
    OtpTextField(modifier = Modifier.padding(top = 20.dp),
        realOtp = realOtp,
        onOtpTextCorrectlyEntered = {
            onOtpTextCorrectlyEntered()
        })
}

@Preview
@Composable
fun OtpScreenPreview() {
    MifosTheme {
        OtpScreen(realOtp = "1234",
            contentVisibility = true,
            verificationStatus = false,
            onOtpTextCorrectlyEntered = {})
    }
}

@Preview
@Composable
fun OtpScreenVerifiedPreview() {
    MifosTheme {
        OtpScreen(realOtp = "1234",
            contentVisibility = false,
            verificationStatus = true,
            onOtpTextCorrectlyEntered = {})
    }
}