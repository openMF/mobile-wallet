/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer.success

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.make_transfer.generated.resources.Res
import mobile_wallet.feature.make_transfer.generated.resources.back_to_home
import mobile_wallet.feature.make_transfer.generated.resources.payment_done
import mobile_wallet.feature.make_transfer.generated.resources.payment_success
import mobile_wallet.feature.make_transfer.generated.resources.process_ring
import mobile_wallet.feature.make_transfer.generated.resources.success
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold

@Composable
internal fun TransferSuccessScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                MifosButton(
                    onClick = navigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(text = stringResource(Res.string.back_to_home))
                }
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            ) {
                Image(
                    imageVector = vectorResource(Res.drawable.process_ring),
                    contentDescription = stringResource(Res.string.success),
                    modifier = Modifier.size(200.dp),
                )

                Text(
                    text = stringResource(Res.string.payment_success),
                    style = MaterialTheme.typography.headlineMedium,
                )

                Text(
                    text = stringResource(Res.string.payment_done),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
