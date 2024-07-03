package org.mifospay.feature.kyc.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.kyc.KYCLevel3Screen

const val KYC_LEVEL_3_ROUTE = "kyc_level_3_route"

fun NavController.navigateToKYCLevel3() {
    this.navigate(KYC_LEVEL_3_ROUTE)
}

fun NavGraphBuilder.kycLevel3Screen() {
    composable(route = KYC_LEVEL_3_ROUTE) {
        KYCLevel3Screen()
    }
}