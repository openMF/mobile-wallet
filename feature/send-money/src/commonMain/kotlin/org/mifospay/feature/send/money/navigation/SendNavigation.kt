/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import org.mifospay.core.ui.composableWithSlideTransitions
import org.mifospay.feature.send.money.ContactsPickerScreen
import org.mifospay.feature.send.money.PayAnyoneScreen
import org.mifospay.feature.send.money.PayeeDetailsScreen
import org.mifospay.feature.send.money.PayeeDetailsState
import org.mifospay.feature.send.money.SendMoneyOptionsScreen
import org.mifospay.feature.send.money.SendMoneyScreen

const val SEND_MONEY_ROUTE = "send_money_route"
const val SEND_MONEY_ARG = "requestData"

const val SEND_MONEY_BASE_ROUTE = "$SEND_MONEY_ROUTE?$SEND_MONEY_ARG={$SEND_MONEY_ARG}"

const val SEND_MONEY_OPTIONS_ROUTE = "send_money_options_route"
const val PAYEE_DETAILS_ROUTE = "payee_details_route"
const val PAYEE_DETAILS_ARG = "qrCodeData"

const val PAYEE_DETAILS_BASE_ROUTE = "$PAYEE_DETAILS_ROUTE?$PAYEE_DETAILS_ARG={$PAYEE_DETAILS_ARG}"

const val PAY_ANYONE_ROUTE = "pay_anyone_route"
const val PAY_ANYONE_SELECTED_CONTACT_ARG = "selectedContact"
const val PAY_ANYONE_BASE_ROUTE = "$PAY_ANYONE_ROUTE?$PAY_ANYONE_SELECTED_CONTACT_ARG={$PAY_ANYONE_SELECTED_CONTACT_ARG}"
const val CONTACTS_PICKER_ROUTE = "contacts_picker_route"

fun NavController.navigateToPayAnyoneScreen(
    selectedContactPhone: String? = null,
    navOptions: NavOptions? = null,
) {
    val route = if (selectedContactPhone != null) {
        "$PAY_ANYONE_ROUTE?$PAY_ANYONE_SELECTED_CONTACT_ARG=$selectedContactPhone"
    } else {
        PAY_ANYONE_ROUTE
    }
    navigate(route, navOptions)
}

fun NavController.navigateToContactsPickerScreen(
    navOptions: NavOptions? = null,
) = navigate(CONTACTS_PICKER_ROUTE, navOptions)

fun NavController.navigateToSendMoneyScreen(
    navOptions: NavOptions? = null,
) = navigate(SEND_MONEY_ROUTE, navOptions)

fun NavController.navigateToSendMoneyOptionsScreen(
    navOptions: NavOptions? = null,
) = navigate(SEND_MONEY_OPTIONS_ROUTE, navOptions)

fun NavController.navigateToPayeeDetailsScreen(
    qrCodeData: String,
    navOptions: NavOptions? = null,
) {
    // URL encode the QR code data to handle special characters like &, =, etc.
    val encodedQrCodeData = qrCodeData.urlEncode()
    val route = "$PAYEE_DETAILS_ROUTE?$PAYEE_DETAILS_ARG=$encodedQrCodeData"
    val options = navOptions ?: navOptions {
        popUpTo(SEND_MONEY_OPTIONS_ROUTE) { inclusive = false }
    }
    navigate(route, options)
}
fun NavGraphBuilder.sendMoneyScreen(
    onBackClick: () -> Unit,
    navigateToTransferScreen: (String) -> Unit,
    navigateToPayeeDetailsScreen: (String) -> Unit,
    navigateToScanQrScreen: () -> Unit,
) {
    composableWithSlideTransitions(
        route = SEND_MONEY_BASE_ROUTE,
        arguments = listOf(
            navArgument(SEND_MONEY_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        SendMoneyScreen(
            onBackClick = onBackClick,
            navigateToTransferScreen = navigateToTransferScreen,
            navigateToScanQrScreen = navigateToScanQrScreen,
            navigateToPayeeDetails = navigateToPayeeDetailsScreen,
        )
    }
}

fun NavGraphBuilder.sendMoneyOptionsScreen(
    onBackClick: () -> Unit,
    onScanQrClick: () -> Unit,
    onPayAnyoneClick: () -> Unit,
    onBankTransferClick: () -> Unit,
    onFineractPaymentsClick: () -> Unit,
    onQrCodeScanned: (String) -> Unit,
    onNavigateToPayeeDetails: (String) -> Unit,
) {
    composableWithSlideTransitions(
        route = SEND_MONEY_OPTIONS_ROUTE,
    ) {
        SendMoneyOptionsScreen(
            onBackClick = onBackClick,
            onScanQrClick = onScanQrClick,
            onPayAnyoneClick = onPayAnyoneClick,
            onBankTransferClick = onBankTransferClick,
            onFineractPaymentsClick = onFineractPaymentsClick,
            onQrCodeScanned = onQrCodeScanned,
            onNavigateToPayeeDetails = onNavigateToPayeeDetails,
        )
    }
}

fun NavGraphBuilder.payAnyoneScreen(
    onBackClick: () -> Unit,
    onContactPickerClick: () -> Unit,
    onContactSelected: (String) -> Unit = {},
) {
    composableWithSlideTransitions(
        route = PAY_ANYONE_BASE_ROUTE,
        arguments = listOf(
            navArgument(PAY_ANYONE_SELECTED_CONTACT_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) { backStackEntry ->
        val selectedContactPhone = backStackEntry.arguments?.getString(PAY_ANYONE_SELECTED_CONTACT_ARG)

        PayAnyoneScreen(
            onBackClick = onBackClick,
            onContactPickerClick = onContactPickerClick,
            onContactSelected = onContactSelected,
            selectedContactPhone = selectedContactPhone,
        )
    }
}

fun NavGraphBuilder.contactsPickerScreen(
    onBackClick: () -> Unit,
    onContactSelected: (String) -> Unit,
) {
    composableWithSlideTransitions(
        route = CONTACTS_PICKER_ROUTE,
    ) {
        ContactsPickerScreen(
            onBackClick = onBackClick,
            onContactSelected = onContactSelected,
        )
    }
}

fun NavGraphBuilder.payeeDetailsScreen(
    onBackClick: () -> Unit,
    onNavigateToUpiPayment: (PayeeDetailsState) -> Unit,
    onNavigateToFineractPayment: (PayeeDetailsState) -> Unit,
) {
    composableWithSlideTransitions(
        route = PAYEE_DETAILS_BASE_ROUTE,
        arguments = listOf(
            navArgument(PAYEE_DETAILS_ARG) {
                type = NavType.StringType
                nullable = false
            },
        ),
    ) {
        PayeeDetailsScreen(
            onBackClick = onBackClick,
            onNavigateToUpiPayment = onNavigateToUpiPayment,
            onNavigateToFineractPayment = onNavigateToFineractPayment,
        )
    }
}

fun NavController.navigateToSendMoneyScreen(
    requestData: String,
    navOptions: NavOptions? = null,
) {
    val route = "$SEND_MONEY_ROUTE?$SEND_MONEY_ARG=$requestData"
    val options = navOptions ?: navOptions {
        popUpTo(SEND_MONEY_ROUTE) { inclusive = true }
    }

    navigate(route, options)
}

/**
 * URL encodes a string to handle special characters in navigation
 *
 * Optimized for UPI QR codes with future-proofing for common special characters.
 *
 * Essential UPI characters (12):
 * - URL structure: ?, &, =, %
 * - VPA format: @
 * - Common text: space, ", ', comma
 * - URLs: /, :, #, +
 *
 * Future-proofing characters (5):
 * - Currency symbols: $
 * - URL parameters: ;
 * - JSON/structured data: [, ], {, }
 */
private fun String.urlEncode(): String {
    return this.replace("%", "%25")
        .replace(" ", "%20")
        .replace("&", "%26")
        .replace("=", "%3D")
        .replace("?", "%3F")
        .replace("@", "%40")
        .replace("+", "%2B")
        .replace("/", "%2F")
        .replace(":", "%3A")
        .replace("#", "%23")
        .replace("\"", "%22")
        .replace("'", "%27")
        .replace(",", "%2C")
        .replace("$", "%24")
        .replace(";", "%3B")
        .replace("[", "%5B")
        .replace("]", "%5D")
        .replace("{", "%7B")
        .replace("}", "%7D")
}
