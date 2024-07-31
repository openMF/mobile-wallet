@file:Suppress("MaxLineLength")

package org.mifospay.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.common.Constants
import org.mifospay.feature.auth.mobile_verify.MobileVerificationScreen

const val MOBILE_VERIFICATION_ROUTE = "mobile_verification_route"

fun NavGraphBuilder.mobileVerificationScreen(
    onOtpVerificationSuccess: (String, Map<String, Any?>) -> Unit
) {
    composable(
        route = "$MOBILE_VERIFICATION_ROUTE?mifosSignedUp={mifosSignedUp}&googleDisplayName={googleDisplayName}&googleEmail={googleEmail}&googleFamilyName={googleFamilyName}&googleGivenName={googleGivenName}",
        arguments = listOf(
            navArgument("mifosSignedUp") { type = NavType.IntType; defaultValue = 0 },
            navArgument("googleDisplayName") { type = NavType.StringType; nullable = true },
            navArgument("googleEmail") { type = NavType.StringType; nullable = true },
            navArgument("googleFamilyName") { type = NavType.StringType; nullable = true },
            navArgument("googleGivenName") { type = NavType.StringType; nullable = true }
        )
    ) { backStackEntry ->
        val mifosSignedUp = backStackEntry.arguments?.getInt("mifosSignedUp") ?: 0
        val googleDisplayName = backStackEntry.arguments?.getString("googleDisplayName")
        val googleEmail = backStackEntry.arguments?.getString("googleEmail")
        val googleFamilyName = backStackEntry.arguments?.getString("googleFamilyName")
        val googleGivenName = backStackEntry.arguments?.getString("googleGivenName")

        MobileVerificationScreen(
            onOtpVerificationSuccess = { fullNumber ->
                val extraData = mapOf(
                    Constants.MIFOS_SAVINGS_PRODUCT_ID to mifosSignedUp,
                    Constants.GOOGLE_DISPLAY_NAME to googleDisplayName,
                    Constants.GOOGLE_EMAIL to googleEmail,
                    Constants.GOOGLE_FAMILY_NAME to googleFamilyName,
                    Constants.GOOGLE_GIVEN_NAME to googleGivenName,
                    Constants.COUNTRY to "Canada",
                    Constants.MOBILE_NUMBER to fullNumber
                )
                onOtpVerificationSuccess(fullNumber, extraData)
            }
        )
    }
}

fun NavController.navigateToMobileVerification(
    mifosSignedUp: Int,
    googleDisplayName: String?,
    googleEmail: String?,
    googleFamilyName: String?,
    googleGivenName: String?
) {
    this.navigate("$MOBILE_VERIFICATION_ROUTE?mifosSignedUp=$mifosSignedUp&googleDisplayName=$googleDisplayName&googleEmail=$googleEmail&googleFamilyName=$googleFamilyName&googleGivenName=$googleGivenName")
}

