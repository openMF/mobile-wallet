/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * JS stub implementation of PaymentSuccessScreen.
 *
 * Screenshot functionality is not available in JS environments.
 */
@Composable
actual fun PaymentSuccessScreen(
    onShareScreenshot: () -> Unit,
    onDone: () -> Unit,
    onNavigateToSendMoneyOptions: () -> Unit,
    modifier: Modifier,
    viewModel: PaymentSuccessViewModel,
) {
    PaymentSuccessScreenDefault(
        onShareScreenshot = onShareScreenshot,
        onDone = onDone,
        onNavigateToSendMoneyOptions = onNavigateToSendMoneyOptions,
        modifier = modifier,
        viewModel = viewModel,
    )
}
