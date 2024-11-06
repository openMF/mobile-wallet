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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.ui.MifosScrollableTabRow
import org.mifospay.core.ui.utility.TabContent

@Composable
fun PaymentsRoute(
    tabContents: List<TabContent>,
    modifier: Modifier = Modifier,
) {
    PaymentScreenContent(
        tabContents = tabContents,
        modifier = modifier,
    )
}

@Composable
private fun PaymentScreenContent(
    tabContents: List<TabContent>,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { tabContents.size })

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        MifosScrollableTabRow(
            tabContents = tabContents,
            pagerState = pagerState,
        )
    }
}

enum class PaymentsScreenContents {
    SEND,
    REQUEST,
    HISTORY,
    SI,
    INVOICES,
}

@Preview
@Composable
private fun PaymentsScreenPreview() {
    PaymentScreenContent(
        tabContents = emptyList(),
    )
}
