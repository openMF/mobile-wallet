package org.mifospay.feature.kyc.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.kyc.KYCLevel1Screen

const val KYC_LEVEL_1_ROUTE = "kyc_level_1_route"

fun NavController.navigateToKYCLevel1() {
    this.navigate(KYC_LEVEL_1_ROUTE)
}

fun NavGraphBuilder.kycLevel1Screen(
    navigateToKycLevel2: () -> Unit
) {
    composable(route = KYC_LEVEL_1_ROUTE) {
        KYCLevel1Screen(
            navigateToKycLevel2 = navigateToKycLevel2
        )
    }
}