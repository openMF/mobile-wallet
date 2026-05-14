/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.link

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.pocket.generated.resources.Res
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_account_id_label
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_account_id_placeholder
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_account_type_label
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_link_account
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_link_title
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_loan
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_savings
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_share
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.model.pocket.PocketAccountType
import org.mifospay.core.ui.utils.EventsEffect

@Composable
fun LinkAccountScreen(
    navigateBack: () -> Unit,
    onLinkSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LinkAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            LinkAccountEvent.NavigateBack -> navigateBack()
            LinkAccountEvent.LinkSuccess -> {
                navigateBack()
                onLinkSuccess()
            }
        }
    }

    MifosScaffold(
        backPress = { viewModel.trySendAction(LinkAccountAction.NavigateBack) },
        topBarTitle = stringResource(Res.string.feature_pocket_link_title),
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = state.accountId,
                onValueChange = { viewModel.trySendAction(LinkAccountAction.OnAccountIdChanged(it)) },
                label = { Text(stringResource(Res.string.feature_pocket_account_id_label)) },
                placeholder = { Text(stringResource(Res.string.feature_pocket_account_id_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.error != null,
                supportingText = state.error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Text(
                text = stringResource(Res.string.feature_pocket_account_type_label),
                style = MaterialTheme.typography.labelLarge,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PocketAccountType.entries.forEach { type ->
                    FilterChip(
                        selected = state.selectedType == type,
                        onClick = { viewModel.trySendAction(LinkAccountAction.OnAccountTypeSelected(type)) },
                        label = {
                            Text(
                                text = when (type) {
                                    PocketAccountType.SAVINGS -> stringResource(Res.string.feature_pocket_savings)
                                    PocketAccountType.LOAN -> stringResource(Res.string.feature_pocket_loan)
                                    PocketAccountType.SHARE -> stringResource(Res.string.feature_pocket_share)
                                },
                            )
                        },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.trySendAction(LinkAccountAction.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.accountId.isNotBlank(),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Text(stringResource(Res.string.feature_pocket_link_account))
            }
        }
    }
}
