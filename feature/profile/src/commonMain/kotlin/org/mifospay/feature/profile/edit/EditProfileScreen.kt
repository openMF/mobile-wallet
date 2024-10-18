/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.profile.generated.resources.Res
import mobile_wallet.feature.profile.generated.resources.feature_profile_edit_profile
import mobile_wallet.feature.profile.generated.resources.feature_profile_email
import mobile_wallet.feature.profile.generated.resources.feature_profile_firstname
import mobile_wallet.feature.profile.generated.resources.feature_profile_lastname
import mobile_wallet.feature.profile.generated.resources.feature_profile_mobile
import mobile_wallet.feature.profile.generated.resources.feature_profile_save
import mobile_wallet.feature.profile.generated.resources.feature_profile_vpa
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.theme.MifosBlue
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.profile.components.EditableProfileImage

@Composable
internal fun EditProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is EditProfileEvent.NavigateBack -> onBackClick.invoke()
            is EditProfileEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Box(modifier) {
        EditProfileScreenContent(
            state = state,
            onAction = remember(viewModel) {
                { action -> viewModel.trySendAction(action) }
            },
            snackbarHostState = snackbarHostState,
        )

        EditProfileDialogs(
            dialogState = state.dialogState,
            onDismissRequest = remember(viewModel) {
                { viewModel.trySendAction(EditProfileAction.DismissErrorDialog) }
            },
        )
    }
}

@Composable
private fun EditProfileScreenContent(
    modifier: Modifier = Modifier,
    state: EditProfileState,
    onAction: (EditProfileAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    MifosScaffold(
        topBarTitle = stringResource(Res.string.feature_profile_edit_profile),
        backPress = {
            onAction(EditProfileAction.NavigateBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                EditableProfileImage(
                    serverImage = state.imageInput,
                    onChooseImage = {
                        onAction(EditProfileAction.ProfileImageChange(it))
                    },
                )
            }

            item {
                MifosTextField(
                    value = state.firstNameInput,
                    onValueChange = {
                        onAction(EditProfileAction.FirstNameInputChange(it))
                    },
                    label = stringResource(Res.string.feature_profile_firstname),
                )
            }

            item {
                MifosTextField(
                    value = state.lastNameInput,
                    onValueChange = {
                        onAction(EditProfileAction.LastNameInputChange(it))
                    },
                    label = stringResource(Res.string.feature_profile_lastname),
                )
            }

            item {
                MifosTextField(
                    value = state.emailInput,
                    onValueChange = {
                        onAction(EditProfileAction.EmailInputChange(it))
                    },
                    label = stringResource(Res.string.feature_profile_email),
                )
            }

            item {
                MifosTextField(
                    value = state.externalIdInput,
                    onValueChange = {
                        onAction(EditProfileAction.ExternalIdInputChange(it))
                    },
                    label = stringResource(Res.string.feature_profile_vpa),
                )
            }

            item {
                MifosTextField(
                    value = state.phoneNumberInput,
                    onValueChange = {
                        onAction(EditProfileAction.PhoneNumberInputChange(it))
                    },
                    label = stringResource(Res.string.feature_profile_mobile),
                )
            }

            item {
                Spacer(modifier.height(16.dp))

                MifosButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    color = MifosBlue,
                    text = { Text(text = stringResource(Res.string.feature_profile_save)) },
                    onClick = {
                        onAction(EditProfileAction.UpdateProfile)
                    },
                )
            }
        }
    }
}

@Composable
private fun EditProfileDialogs(
    dialogState: EditProfileState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is EditProfileState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is EditProfileState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}
