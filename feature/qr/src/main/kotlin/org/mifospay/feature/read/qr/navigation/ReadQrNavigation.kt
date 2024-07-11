package org.mifospay.feature.read.qr.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.read.qr.ShowQrScreenRoute

const val READ_QR_ROUTE = "read_qr_route"

fun NavController.navigateToReadQr() = navigate(READ_QR_ROUTE)

fun NavGraphBuilder.readQrScreen(
    onBackClick: () -> Unit
) {
    composable(route = READ_QR_ROUTE) {
        ShowQrScreenRoute(
            backPress = onBackClick
        )
    }
}

