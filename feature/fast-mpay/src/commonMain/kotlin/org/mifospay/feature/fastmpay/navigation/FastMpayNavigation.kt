/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.fastmpay.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.mifospay.core.data.util.MpayQrCodeProcessor
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.core.ui.composableWithSlideTransitions
import org.mifospay.feature.fastmpay.FastMpayScreen

private const val FAST_MPAY_ROUTE_BASE = "fast_mpay_route"
internal const val QR_DATA_ARG = "qrData"

const val FAST_MPAY_ROUTE = "$FAST_MPAY_ROUTE_BASE/{$QR_DATA_ARG}"

/**
 * Navigate to Fast MPay processing screen.
 *
 * @param qrData The decoded QR code data from scanner
 * @param navOptions Optional navigation options
 */
fun NavController.navigateToFastMpay(
    qrData: QrCodeData,
    navOptions: NavOptions? = null,
) {
    val encodedData = MpayQrCodeProcessor.encodeMpayString(qrData)
    navigate(
        route = "$FAST_MPAY_ROUTE_BASE/$encodedData",
        navOptions = navOptions,
    )
}

/**
 * Registers the Fast MPay screen in the navigation graph.
 *
 * @param onNavigateToAddBeneficiary Callback for INTRA_BANK/BENEFICIARY type when beneficiary doesn't exist
 * @param onNavigateToSendMoneyV2 Callback for INTRA_BANK type when beneficiary exists (qrData, beneficiaryName)
 * @param onNavigateToInterbankTransfer Callback for INTER_BANK type (accountExternalId, recipientName, amount)
 * @param onNavigateToIntraBankTransfer Callback for future intra-bank direct transfer
 * @param onNavigateToMerchantPayment Callback for MERCHANT type
 * @param onError Callback when processing fails
 */
fun NavGraphBuilder.fastMpayScreen(
    onNavigateToAddBeneficiary: (beneficiaryData: String) -> Unit,
    onNavigateToSendMoneyV2: (qrData: QrCodeData, beneficiaryName: String) -> Unit,
    onNavigateToInterbankTransfer: (accountExternalId: String, recipientName: String?, amount: String?) -> Unit,
    onNavigateToIntraBankTransfer: (qrData: QrCodeData) -> Unit,
    onNavigateToMerchantPayment: (qrData: QrCodeData) -> Unit,
    onError: (message: String) -> Unit,
) {
    composableWithSlideTransitions(
        route = FAST_MPAY_ROUTE,
        arguments = listOf(
            navArgument(QR_DATA_ARG) {
                type = NavType.StringType
            },
        ),
    ) {
        FastMpayScreen(
            onNavigateToAddBeneficiary = onNavigateToAddBeneficiary,
            onNavigateToSendMoneyV2 = onNavigateToSendMoneyV2,
            onNavigateToInterbankTransfer = onNavigateToInterbankTransfer,
            onNavigateToIntraBankTransfer = onNavigateToIntraBankTransfer,
            onNavigateToMerchantPayment = onNavigateToMerchantPayment,
            onError = onError,
        )
    }
}
