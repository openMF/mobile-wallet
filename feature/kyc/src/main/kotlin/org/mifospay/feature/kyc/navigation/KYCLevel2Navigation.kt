package org.mifospay.feature.kyc.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.kyc.KYCLevel2Screen

const val KYC_LEVEL_2_ROUTE = "kyc_level_2_route"

fun NavController.navigateToKYCLevel2() {
    this.navigate(KYC_LEVEL_2_ROUTE)
}

fun NavGraphBuilder.kycLevel2Screen(
    onSuccessKyc2: () -> Unit
) {
    composable(route = KYC_LEVEL_2_ROUTE) {
        KYCLevel2Screen(
            onSuccessKyc2 = onSuccessKyc2
        )
    }
}