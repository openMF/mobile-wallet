/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.profile.generated.resources.Res
import mobile_wallet.feature.profile.generated.resources.feature_profile_link_bank_account
import mobile_wallet.feature.profile.generated.resources.feature_profile_personal_qr_code
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.profile.components.ProfileDetailsCard
import org.mifospay.feature.profile.components.ProfileImage

@Composable
internal fun ProfileScreen(
    onEditProfile: () -> Unit,
    onLinkBackAccount: () -> Unit,
    showQrCode: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val clientState by viewModel.clientState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            ProfileEvent.OnEditProfile -> onEditProfile.invoke()
            ProfileEvent.OnLinkBankAccount -> onLinkBackAccount.invoke()
            ProfileEvent.ShowQRCode -> showQrCode.invoke()
        }
    }

    ProfileDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(ProfileAction.DismissErrorDialog) }
        },
    )

    ProfileScreenContent(
        state = state,
        clientState = clientState,
        onAction = remember(viewModel) {
            { action -> viewModel.trySendAction(action) }
        },
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
internal fun ProfileScreenContent(
    state: ProfileState,
    clientState: ProfileState.ViewState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (clientState) {
                is ProfileState.ViewState.Loading -> {
                    MifosOverlayLoadingWheel(
                        contentDesc = "ProfileLoading",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is ProfileState.ViewState.Error -> {
                    ErrorScreenContent(
                        onClickRetry = { },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is ProfileState.ViewState.Success -> {
                    ProfileScreenContent(
                        state = clientState,
                        clientImage = state.clientImage,
                        onAction = onAction,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileScreenContent(
    state: ProfileState.ViewState.Success,
    clientImage: String?,
    modifier: Modifier = Modifier,
    onAction: (ProfileAction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProfileImage(bitmap = clientImage)

        ProfileDetailsCard(
            client = state.client,
            modifier = Modifier.fillMaxWidth(),
        )

        MifosButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            color = MaterialTheme.colorScheme.primary,
            text = { Text(text = stringResource(Res.string.feature_profile_personal_qr_code)) },
            onClick = {
                onAction(ProfileAction.ShowPersonalQRCode)
            },
            leadingIcon = {
                Icon(
                    imageVector = MifosIcons.QrCode,
                    contentDescription = "Personal QR Code",
                )
            },
        )

        MifosButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            color = MaterialTheme.colorScheme.primary,
            text = { Text(text = stringResource(Res.string.feature_profile_link_bank_account)) },
            onClick = {
                onAction(ProfileAction.NavigateToLinkBankAccount)
            },
            leadingIcon = {
                Icon(imageVector = MifosIcons.AttachMoney, contentDescription = "")
            },
        )

        Spacer(modifier = Modifier.height(1.dp))
    }
}

@Composable
private fun ProfileDialogs(
    dialogState: ProfileState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is ProfileState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is ProfileState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}
