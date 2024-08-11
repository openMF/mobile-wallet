/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.bank.accounts.choose.sim

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.bank.accounts.R

@Composable
internal fun ChooseSimDialogSheet(
    onSimSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosBottomSheet(
        content = {
            ChooseSimDialogSheetContent(
                onSimSelected = onSimSelected,
            )
        },
        onDismiss = {
            onSimSelected.invoke(-1)
        },
        modifier = modifier,
    )
}

/**
 * TODO Read Device SIM and show numbers accordingly If one sim exist then show only one otherwise
 * show both of them and implement send SMS after select and confirm.
 */
@Composable
@Suppress("LongMethod")
private fun ChooseSimDialogSheetContent(
    onSimSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSim by rememberSaveable { mutableIntStateOf(-1) }
    var showMessage by remember { mutableStateOf(false) }
    val message = stringResource(id = R.string.feature_accounts_choose_a_sim)

    LaunchedEffect(key1 = showMessage) {
        if (showMessage) {
            delay(5000)
            showMessage = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Text(
            text = stringResource(id = R.string.feature_accounts_verify_mobile_number),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.feature_accounts_confirm_mobile_number_message),
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(id = R.string.feature_accounts_bank_account_mobile_verification_conditions),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SimCard(
                simNumber = 1,
                isSelected = selectedSim == 1,
                onSimSelected = { selectedSim = 1 },
            )

            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = stringResource(id = R.string.feature_accounts_or),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(24.dp))
            SimCard(
                simNumber = 2,
                isSelected = selectedSim == 2,
                onSimSelected = { selectedSim = 2 },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.feature_accounts_regular_charges_will_apply),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
        )

        AnimatedVisibility(
            visible = showMessage,
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        MifosButton(
            modifier = Modifier
                .width(200.dp)
                .padding(top = 16.dp),
            onClick = {
                if (selectedSim == -1) {
                    showMessage = true
                } else {
                    onSimSelected(selectedSim)
                }
            },
        ) {
            Text(text = stringResource(id = R.string.feature_accounts_confirm))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SimCard(
    simNumber: Int,
    isSelected: Boolean,
    onSimSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawable: Painter = painterResource(
        id = if (isSelected) {
            R.drawable.feature_accounts_sim_card_selected
        } else {
            R.drawable.feature_accounts_sim_card_unselected
        },
    )
    Image(
        painter = drawable,
        contentDescription = "SIM Card $simNumber",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(50.dp)
            .clickable { onSimSelected() },
    )
}

@Preview
@Composable
private fun SimSelectionPreview() {
    MifosTheme {
        Surface {
            ChooseSimDialogSheetContent(
                onSimSelected = {},
            )
        }
    }
}
