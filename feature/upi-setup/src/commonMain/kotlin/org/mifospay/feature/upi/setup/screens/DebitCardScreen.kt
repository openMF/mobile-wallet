/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.upi.setup.screens

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.VerifyStepHeader
import org.mifospay.feature.upi.setup.viewmodel.DebitCardUiState
import org.mifospay.feature.upi.setup.viewmodel.DebitCardViewModel

@Composable
internal fun DebitCardScreen(
    onDebitCardVerified: (String) -> Unit,
    onDebitCardVerificationFailed: (String) -> Unit,
    modifier: Modifier = Modifier,
    verificationStatus: Boolean = false,
    isContentVisible: Boolean = true,
    viewModel: DebitCardViewModel = koinViewModel(),
) {
    val debitCardUiState by viewModel.debitCardUiState.collectAsStateWithLifecycle()

    DebitCardScreenWithHeaderAndContent(
        debitCardUiState = debitCardUiState,
        onDebitCardVerified = onDebitCardVerified,
        onDebitCardVerificationFailed = onDebitCardVerificationFailed,
        onDone = viewModel::verifyDebitCard,
        verificationStatus = verificationStatus,
        isContentVisible = isContentVisible,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun DebitCardScreenWithHeaderAndContent(
    debitCardUiState: DebitCardUiState,
    onDebitCardVerified: (String) -> Unit,
    onDebitCardVerificationFailed: (String) -> Unit,
    onDone: (String, String, String) -> Unit,
    modifier: Modifier = Modifier,
    verificationStatus: Boolean = false,
    isContentVisible: Boolean = true,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
        ),
    ) {
        Column(
            modifier = Modifier.padding(
                top = 15.dp,
                bottom = 15.dp,
                start = 10.dp,
                end = 10.dp,
            ),
        ) {
            VerifyStepHeader("Debit Card Details ", verificationStatus)
            if (isContentVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp),
                ) {
                    // handle debit card ui state
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        DebitCardScreenContent(
                            onDone = onDone,
                        )

                        when (debitCardUiState) {
                            is DebitCardUiState.Initials -> {
                            }

                            is DebitCardUiState.Verifying -> {
                                MifosLoadingWheel(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .align(Alignment.Center),
                                    contentDesc = "Verifying Debit Card",
                                )
                            }

                            is DebitCardUiState.Verified -> {
                                onDebitCardVerified((debitCardUiState).otp)
                            }

                            is DebitCardUiState.VerificationFailed -> {
                                onDebitCardVerificationFailed((debitCardUiState).errorMessage)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DebitCardScreenInitialsPreview() {
    MifosTheme {
        DebitCardScreenWithHeaderAndContent(
            debitCardUiState = DebitCardUiState.Initials,
            onDebitCardVerified = {},
            onDebitCardVerificationFailed = {},
            onDone = { _, _, _ -> },
        )
    }
}

@Preview
@Composable
private fun DebitCardScreenLoadingPreview() {
    MifosTheme {
        DebitCardScreenWithHeaderAndContent(
            debitCardUiState = DebitCardUiState.Verifying,
            onDebitCardVerified = {},
            onDebitCardVerificationFailed = {},
            onDone = { _, _, _ -> },
        )
    }
}

@Preview
@Composable
private fun DebitCardScreenVerificationFailedPreview() {
    MifosTheme {
        DebitCardScreenWithHeaderAndContent(
            debitCardUiState = DebitCardUiState.VerificationFailed("Error Message"),
            onDebitCardVerified = {},
            onDebitCardVerificationFailed = {},
            onDone = { _, _, _ -> },
        )
    }
}
