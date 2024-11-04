/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.merchants.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.merchants.generated.resources.Res
import mobile_wallet.feature.merchants.generated.resources.feature_merchants_ic_bank
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.theme.mifosText
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MerchantsItem(
    savingsWithAssociations: SavingsWithAssociationsEntity,
    onMerchantClicked: () -> Unit,
    onMerchantLongPressed: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosCard(
        modifier = modifier.combinedClickable(
            onClick = onMerchantClicked,
            onLongClick = {
                onMerchantLongPressed(savingsWithAssociations.id.toString())
            },
        ),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.feature_merchants_ic_bank),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp, end = 16.dp)
                        .size(39.dp),
                )

                Column {
                    Text(
                        text = savingsWithAssociations.clientName,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = savingsWithAssociations.accountNo,
                        modifier = Modifier.padding(top = 4.dp),
                        style = styleMedium16sp.copy(mifosText),
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview
@Composable
private fun AccountsItemPreview() {
    MerchantsItem(
        savingsWithAssociations = sampleMerchantList.first(),
        onMerchantClicked = {},
        onMerchantLongPressed = {},
    )
}
