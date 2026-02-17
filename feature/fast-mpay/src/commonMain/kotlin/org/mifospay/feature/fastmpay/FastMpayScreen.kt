/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.fastmpay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.feature.fastmpay.model.QrProcessResult

/**
 * Fast MPay processing screen.
 *
 * This screen acts as a routing hub - it receives encoded QR data via navigation,
 * decodes and processes it using [FastMpayProcessor], and immediately navigates to the
 * appropriate destination based on the QR type.
 *
 * The screen shows a brief loading indicator while processing.
 *
 * @param onNavigateToAddBeneficiary Callback for INTRA_BANK/BENEFICIARY type when beneficiary doesn't exist
 * @param onNavigateToSendMoneyV2 Callback for INTRA_BANK type when beneficiary exists (qrData, beneficiaryName)
 * @param onNavigateToInterbankTransfer Callback for INTER_BANK type
 * @param onNavigateToIntraBankTransfer Callback for future intra-bank direct transfer
 * @param onNavigateToMerchantPayment Callback for MERCHANT type
 * @param onError Callback when processing fails
 * @param viewModel The ViewModel for processing
 */
@Composable
fun FastMpayScreen(
    onNavigateToAddBeneficiary: (String) -> Unit,
    onNavigateToSendMoneyV2: (QrCodeData, String) -> Unit,
    onNavigateToInterbankTransfer: (String, String?, String?) -> Unit,
    onNavigateToIntraBankTransfer: (QrCodeData) -> Unit,
    onNavigateToMerchantPayment: (QrCodeData) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FastMpayViewModel = koinViewModel(),
) {
    val result by viewModel.resultFlow.collectAsStateWithLifecycle()
    val error by viewModel.errorFlow.collectAsStateWithLifecycle()

    // Handle errors
    LaunchedEffect(error) {
        error?.let { message ->
            onError(message)
            viewModel.clearError()
        }
    }

    // Navigate based on result
    LaunchedEffect(result) {
        when (val r = result) {
            is QrProcessResult.NavigateToAddBeneficiary -> {
                viewModel.clearResult()
                onNavigateToAddBeneficiary(r.beneficiaryData)
            }

            is QrProcessResult.NavigateToSendMoneyV2 -> {
                viewModel.clearResult()
                onNavigateToSendMoneyV2(r.qrData, r.beneficiaryName)
            }

            is QrProcessResult.NavigateToInterbankTransfer -> {
                viewModel.clearResult()
                onNavigateToInterbankTransfer(r.accountExternalId, r.recipientName, r.amount)
            }

            is QrProcessResult.NavigateToIntraBankTransfer -> {
                viewModel.clearResult()
                onNavigateToIntraBankTransfer(r.qrData)
            }

            is QrProcessResult.NavigateToMerchantPayment -> {
                viewModel.clearResult()
                onNavigateToMerchantPayment(r.qrData)
            }

            is QrProcessResult.Error -> {
                viewModel.clearResult()
                onError(r.message)
            }

            null -> {
                // Still processing - show loading indicator
            }
        }
    }

    // Show brief loading indicator while processing
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
