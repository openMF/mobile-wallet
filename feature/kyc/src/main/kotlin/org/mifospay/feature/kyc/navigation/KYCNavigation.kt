package org.mifospay.feature.kyc.navigation


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.kyc.KYCScreen

const val KYC_ROUTE = "kyc_route"

fun NavController.navigateToKYC() {
    this.navigate(KYC_ROUTE)
}

fun NavGraphBuilder.kycScreen(
    onLevel1Clicked: () -> Unit,
    onLevel2Clicked: () -> Unit,
    onLevel3Clicked: () -> Unit
) {
    composable(route = KYC_ROUTE) {
        KYCScreen(
            onLevel1Clicked = onLevel1Clicked,
            onLevel2Clicked = onLevel2Clicked,
            onLevel3Clicked = onLevel3Clicked
        )
    }
}