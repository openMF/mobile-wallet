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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosScaffold

// TODO:: Implement KYC Level 3 screen
@Composable
internal fun KYCLevel3Screen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KYCLevel3ViewModel = koinViewModel(),
) {
    KYCLevel3ScreenContent(
        modifier = modifier,
        navigateBack = navigateBack,
    )
}

@Composable
fun KYCLevel3ScreenContent(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
) {
    MifosScaffold(
        topBarTitle = "Review & Submit",
        backPress = navigateBack,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "KYC Level 3")
        }
    }
}
