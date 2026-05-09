/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.*
import org.mifospay.core.ui.MifosProgressIndicator
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun KYCLevel3Screen(
    navigateBack: () -> Unit,
    onKycComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KYCLevel3ViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            KycLevel3Event.OnNavigateBack -> navigateBack()
            KycLevel3Event.OnKycComplete -> onKycComplete()
        }
    }

    KycLevel3Dialogs(
        dialogState = state.dialogState,
        onDismiss = { viewModel.trySendAction(KycLevel3Action.NavigateBack) },
    )

    MifosScaffold(
        topBarTitle = "Review & Submit",
        backPress = { viewModel.trySendAction(KycLevel3Action.NavigateBack) },
        modifier = modifier,
    ) { padding ->
        if (state.isLoading) {
            MifosProgressIndicator(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
            ) {
                state.kycDetails?.let { details ->
                    item { SectionHeader("Personal Details") }
                    item { ReviewRow("First Name", details.firstName) }
                    item { ReviewRow("Last Name", details.lastName) }
                    item { ReviewRow("Mobile Number", details.mobileNo) }
                    item { ReviewRow("Date of Birth", details.dob) }
                    item { SectionHeader("Address") }
                    item { ReviewRow("Address Line 1", details.addressLine1) }
                    item { ReviewRow("Address Line 2", details.addressLine2) }
                }
                item {
                    MifosButton(
                        onClick = { viewModel.trySendAction(KycLevel3Action.ConfirmAndSubmit) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Confirm & Submit") }
                }
            }
        }
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
    HorizontalDivider(modifier = Modifier.padding(top = KptTheme.spacing.sm))
}

@Composable
private fun SectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = KptTheme.spacing.xs))
}

@Composable
private fun KycLevel3Dialogs(dialogState: KycLevel3State.DialogState?, onDismiss: () -> Unit) {
    when (dialogState) {
        is KycLevel3State.DialogState.Loading -> MifosLoadingDialog(LoadingDialogState.Shown)
        is KycLevel3State.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(message = dialogState.message),
            onDismissRequest = onDismiss,
        )
        null -> Unit
    }
}
