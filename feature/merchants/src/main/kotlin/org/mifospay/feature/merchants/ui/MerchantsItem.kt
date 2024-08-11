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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.theme.mifosText
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.feature.merchants.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MerchantsItem(
    savingsWithAssociations: SavingsWithAssociations,
    onMerchantClicked: () -> Unit,
    onMerchantLongPressed: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosCard(
        modifier = modifier.combinedClickable(
            onClick = onMerchantClicked,
            onLongClick = {
                onMerchantLongPressed(savingsWithAssociations.externalId)
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
                    painter = painterResource(id = R.drawable.feature_merchants_ic_bank),
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
                        text = savingsWithAssociations.externalId,
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

@Preview(showBackground = true)
@Composable
private fun AccountsItemPreview() {
    MerchantsItem(
        savingsWithAssociations = SavingsWithAssociations(),
        onMerchantClicked = {},
        onMerchantLongPressed = {},
    )
}
