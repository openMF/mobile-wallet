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

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme

@Composable
fun RequestScreen(
    showQr: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransferViewModel = koinViewModel(),
) {
    val vpa by viewModel.vpa.collectAsState()
    val mobile by viewModel.mobile.collectAsState()

    RequestScreenContent(
        vpa = vpa,
        mobile = mobile,
        showQr = showQr,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun RequestScreenContent(
    vpa: String,
    mobile: String,
    showQr: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Text(
            modifier = Modifier.padding(start = 20.dp, top = 30.dp),
            text = stringResource(id = R.string.feature_payments_receive),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .weight(1f),
            ) {
                Column {
                    Text(text = stringResource(id = R.string.feature_payments_virtual_payment_address_vpa))
                    Text(
                        text = vpa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(text = stringResource(id = R.string.feature_payments_mobile_number))
                    Text(
                        text = mobile,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    onClick = { showQr(vpa) },
                ) {
                    Icon(
                        imageVector = MifosIcons.QrCode,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = stringResource(id = R.string.feature_payments_show_code),
                    )
                }

                Text(
                    text = stringResource(id = R.string.feature_payments_show_code),
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RequestScreenPreview() {
    MifosTheme {
        RequestScreenContent(
            vpa = "1234567898",
            mobile = "9078563421",
            showQr = {},
            modifier = Modifier,
        )
    }
}
