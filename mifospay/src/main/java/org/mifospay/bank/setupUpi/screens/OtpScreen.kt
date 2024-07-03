package org.mifos.mobilewallet.mifospay.seup_upi.fragment

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobilewallet.mifospay.ui.VerifyStepHeader
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.R
import org.mifos.mobilewallet.mifospay.ui.OtpTextField


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
                top = dimensionResource(id = R.dimen.value_15dp),
                bottom = dimensionResource(id = R.dimen.value_15dp),
                start = dimensionResource(id = R.dimen.value_10dp),
                end = dimensionResource(id = R.dimen.value_10dp)
            ), elevation = 1.dp,
        contentColor = MaterialTheme.colorScheme.surface
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
        text = stringResource(id = R.string.enter_otp),
        color = MaterialTheme.colorScheme.primary,
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