/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.payments.generated.resources.Res
import mobile_wallet.feature.payments.generated.resources.baseline_content_copy
import mobile_wallet.feature.payments.generated.resources.feature_payments_mobile_number
import mobile_wallet.feature.payments.generated.resources.feature_payments_receive
import mobile_wallet.feature.payments.generated.resources.feature_payments_show_code
import mobile_wallet.feature.payments.generated.resources.feature_payments_vpa
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.utils.EventsEffect

@Composable
fun RequestScreen(
    showQr: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransferViewModel = koinViewModel(),
) {
    val clipboard = LocalClipboardManager.current
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is TransferEvent.OnShowQR -> showQr.invoke()

            is TransferEvent.OnCopyTextToClipboard -> {
                clipboard.setText(AnnotatedString(event.text))
            }
        }
    }

    RequestScreenContent(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun RequestScreenContent(
    state: TransferState,
    onAction: (TransferAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = stringResource(Res.string.feature_payments_receive),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(text = stringResource(Res.string.feature_payments_vpa))
                    Text(
                        text = state.externalId,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        onAction(TransferAction.ShowQR)
                    },
                ) {
                    Icon(
                        imageVector = MifosIcons.QrCode,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = stringResource(Res.string.feature_payments_show_code),
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(text = stringResource(Res.string.feature_payments_mobile_number))
                    Text(
                        text = state.mobileNo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        onAction(TransferAction.CopyTextToClipboard(state.mobileNo))
                    },
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.baseline_content_copy),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Copy Text",
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RequestScreenPreview() {
    MifosTheme {
        RequestScreenContent(
            state = TransferState(
                mobileNo = "iisque",
                externalId = "nonumes",
            ),
            onAction = {},
            modifier = Modifier,
        )
    }
}
