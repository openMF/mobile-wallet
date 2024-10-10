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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.auth.generated.resources.Res
import mobile_wallet.feature.auth.generated.resources.feature_auth_enter_mobile_number
import mobile_wallet.feature.auth.generated.resources.feature_auth_enter_mobile_number_description
import mobile_wallet.feature.auth.generated.resources.feature_auth_enter_otp
import mobile_wallet.feature.auth.generated.resources.feature_auth_enter_otp_received
import mobile_wallet.feature.auth.generated.resources.feature_auth_phone_number
import mobile_wallet.feature.auth.generated.resources.feature_auth_verify_otp
import mobile_wallet.feature.auth.generated.resources.feature_auth_verify_phone
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopAppBar
import org.mifospay.core.designsystem.component.NavigationIcon
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun MobileVerificationScreen(
    onNavigateBack: () -> Unit,
    onOtpVerificationSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MobileVerificationViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    EventsEffect(viewModel) { event ->
        when (event) {
            is MobileVerificationEvent.NavigateBack -> onNavigateBack.invoke()

            is MobileVerificationEvent.NavigateToSignup -> {
                onOtpVerificationSuccess(event.phoneNo)
            }

            is MobileVerificationEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    MobileVerificationScreen(
        uiState = state,
        onEvent = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobileVerificationScreen(
    uiState: MobileVerificationState,
    onEvent: (MobileVerificationAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val title = if (uiState is MobileVerificationState.VerifyPhoneState) {
        stringResource(Res.string.feature_auth_enter_mobile_number)
    } else {
        stringResource(Res.string.feature_auth_enter_otp)
    }

    MifosScaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHostState = snackbarHostState,
        topBar = {
            MifosTopAppBar(
                title = title,
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = MifosIcons.Back,
                    navigationIconContentDescription = "Go Back",
                    onNavigationIconClick = {
                        onEvent(MobileVerificationAction.CloseButtonClick)
                    },
                ),
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is MobileVerificationState.VerifyPhoneState -> {
                PhoneNoVerifyContent(
                    modifier = Modifier.padding(paddingValues),
                    state = uiState,
                    onEvent = onEvent,
                )
            }

            is MobileVerificationState.VerifyOtpState -> {
                OtpVerifyContent(
                    modifier = Modifier.padding(paddingValues),
                    state = uiState,
                    onEvent = onEvent,
                )
            }
        }
    }
}

@Composable
fun PhoneNoVerifyContent(
    modifier: Modifier = Modifier,
    state: MobileVerificationState.VerifyPhoneState,
    onEvent: (MobileVerificationAction) -> Unit,
) {
    MobileVerificationDialogs(
        dialogState = state.dialogState,
        onDismissRequest = {
            onEvent(MobileVerificationAction.DismissDialog)
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MifosOutlinedTextField(
            modifier = modifier,
            value = state.phoneNo,
            label = stringResource(Res.string.feature_auth_phone_number),
            onValueChange = {
                onEvent(MobileVerificationAction.PhoneNoChanged(it))
            },
        )

        Text(
            text = stringResource(Res.string.feature_auth_enter_mobile_number_description),
            style = MaterialTheme.typography.bodySmall,
        )

        MifosButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            enabled = state.isPhoneNoValid,
            onClick = {
                onEvent(MobileVerificationAction.VerifyPhoneBtnClicked)
            },
            contentPadding = PaddingValues(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.feature_auth_verify_phone).uppercase(),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun OtpVerifyContent(
    modifier: Modifier = Modifier,
    state: MobileVerificationState.VerifyOtpState,
    onEvent: (MobileVerificationAction) -> Unit,
) {
    MobileVerificationDialogs(
        dialogState = state.dialogState,
        onDismissRequest = {
            onEvent(MobileVerificationAction.DismissDialog)
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MifosOutlinedTextField(
            modifier = modifier,
            value = state.otp,
            label = stringResource(Res.string.feature_auth_enter_otp),
            onValueChange = {
                onEvent(MobileVerificationAction.PhoneNoChanged(it))
            },
        )

        Text(
            text = stringResource(Res.string.feature_auth_enter_otp_received),
            style = MaterialTheme.typography.bodySmall,
        )

        MifosButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            enabled = state.isOtpValid,
            onClick = {
                onEvent(MobileVerificationAction.VerifyOtpBtnClicked)
            },
            contentPadding = PaddingValues(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.feature_auth_verify_otp).uppercase(),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun MobileVerificationDialogs(
    dialogState: MobileVerificationState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is MobileVerificationState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is MobileVerificationState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Preview
@Composable
private fun MobileVerificationScreenVerifyPhonePreview() {
    MifosTheme {
        MobileVerificationScreen(
            uiState = MobileVerificationState.VerifyPhoneState(),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun MobileVerificationScreenVerifyOtpPreview() {
    MifosTheme {
        MobileVerificationScreen(
            uiState = MobileVerificationState.VerifyOtpState(phoneNo = ""),
            onEvent = {},
        )
    }
}
