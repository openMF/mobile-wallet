/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.mobileVerify

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.library.countrycodepicker.CountryCodePicker
import org.koin.androidx.compose.koinViewModel
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.auth.R

@Composable
internal fun MobileVerificationScreen(
    onOtpVerificationSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MobileVerificationViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MobileVerificationScreen(
        uiState = uiState,
        showProgressState = viewModel.showProgress,
        verifyMobileAndRequestOtp = { phone, fullPhone ->
            viewModel.verifyMobileAndRequestOtp(fullPhone, phone) {
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        },
        verifyOtp = { validatedOtp, fullNumber ->
            viewModel.verifyOTP(validatedOtp) {
                onOtpVerificationSuccess(fullNumber)
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun MobileVerificationScreen(
    uiState: MobileVerificationUiState,
    verifyMobileAndRequestOtp: (String, String) -> Unit,
    verifyOtp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    showProgressState: Boolean = false,
) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var fullPhoneNumber by rememberSaveable { mutableStateOf("") }
    var isNumberValid: Boolean by rememberSaveable { mutableStateOf(false) }

    var isOtpValidated by rememberSaveable { mutableStateOf(false) }
    var validatedOtp by rememberSaveable { mutableStateOf("") }

    fun verifyMobileOrOtp() {
        if (uiState == MobileVerificationUiState.VerifyPhone && isNumberValid) {
            verifyMobileAndRequestOtp(phoneNumber, fullPhoneNumber)
        } else if (isOtpValidated) {
            verifyOtp(validatedOtp, fullPhoneNumber)
        }
    }

    Box(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .focusable(!showProgressState),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    text = if (uiState == MobileVerificationUiState.VerifyPhone) {
                        stringResource(id = R.string.feature_auth_enter_mobile_number)
                    } else {
                        stringResource(id = R.string.feature_auth_enter_otp)
                    },
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
                )
                Text(
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 32.dp,
                        start = 24.dp,
                        end = 24.dp,
                    ),
                    text = if (uiState == MobileVerificationUiState.VerifyPhone) {
                        stringResource(id = R.string.feature_auth_enter_mobile_number_description)
                    } else {
                        stringResource(id = R.string.feature_auth_enter_otp_received_on_your_registered_device)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            when (uiState) {
                MobileVerificationUiState.VerifyPhone -> {
                    EnterPhoneScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        onNumberUpdated = { phone, fullPhone, valid ->
                            phoneNumber = phone
                            fullPhoneNumber = fullPhone
                            isNumberValid = valid
                        },
                    )
                }

                MobileVerificationUiState.VerifyOtp -> {
                    EnterOtpScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp, vertical = 24.dp),
                        onOtpValidated = { isValidated, otp ->
                            isOtpValidated = isValidated
                            validatedOtp = otp
                        },
                    )
                }
            }

            MifosButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                enabled = if (uiState == MobileVerificationUiState.VerifyPhone) {
                    isNumberValid
                } else {
                    isOtpValidated
                },
                onClick = { verifyMobileOrOtp() },
                contentPadding = PaddingValues(12.dp),
            ) {
                Text(
                    text = if (uiState == MobileVerificationUiState.VerifyPhone) {
                        stringResource(id = R.string.feature_auth_verify_phone).uppercase()
                    } else {
                        stringResource(id = R.string.feature_auth_verify_otp).uppercase()
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        if (showProgressState) {
            ShowProgressScreen(uiState = uiState)
        }
    }
}

@Composable
private fun EnterPhoneScreen(
    onNumberUpdated: (String, String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    CountryCodePicker(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        onValueChange = { (code, phone), isValid ->
            onNumberUpdated(phone, code + phone, isValid)
        },
        label = { Text(stringResource(id = R.string.feature_auth_phone_number)) },
        keyboardActions = KeyboardActions { keyboardController?.hide() },
    )
}

@Composable
private fun EnterOtpScreen(
    onOtpValidated: (Boolean, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var otp by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    MifosOutlinedTextField(
        label = R.string.feature_auth_enter_otp,
        value = otp,
        onValueChange = {
            otp = it
            onOtpValidated(otp.text.length == 6, otp.text)
        },
        modifier = modifier,
        keyboardActions = KeyboardActions { keyboardController?.hide() },
    )
}

@Composable
private fun ShowProgressScreen(
    uiState: MobileVerificationUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            .focusable(),
        contentAlignment = Alignment.Center,
    ) {
        MifosLoadingWheel(
            modifier = Modifier.wrapContentSize(),
            contentDesc = if (uiState == MobileVerificationUiState.VerifyPhone) {
                Constants.SENDING_OTP_TO_YOUR_MOBILE_NUMBER
            } else {
                Constants.VERIFYING_OTP
            },
        )
    }
}

@Preview
@Composable
private fun MobileVerificationScreenVerifyPhonePreview() {
    MifosTheme {
        MobileVerificationScreen(
            uiState = MobileVerificationUiState.VerifyPhone,
            showProgressState = false,
            verifyMobileAndRequestOtp = { _, _ -> },
            verifyOtp = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun MobileVerificationScreenVerifyOtpPreview() {
    MifosTheme {
        MobileVerificationScreen(
            uiState = MobileVerificationUiState.VerifyOtp,
            showProgressState = false,
            verifyMobileAndRequestOtp = { _, _ -> },
            verifyOtp = { _, _ -> },
        )
    }
}
