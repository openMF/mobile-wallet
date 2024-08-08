@file:Suppress("MaxLineLength")

package org.mifospay.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.mifospay.feature.auth.signup.SignupScreen

const val SIGNUP_ROUTE = "signup_route"

@Suppress("UnusedParameter")
fun NavGraphBuilder.signupScreen(
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    composable(
        route = "$SIGNUP_ROUTE?savingProductId={savingProductId}&mobileNumber={mobileNumber}&country={country}&email={email}&firstName={firstName}&lastName={lastName}&businessName={businessName}",
        arguments = listOf(
            navArgument("savingProductId") { type = NavType.IntType; defaultValue = 0 },
            navArgument("mobileNumber") { type = NavType.StringType; defaultValue = "" },
            navArgument("country") { type = NavType.StringType; defaultValue = "" },
            navArgument("email") { type = NavType.StringType; defaultValue = "" },
            navArgument("firstName") { type = NavType.StringType; defaultValue = "" },
            navArgument("lastName") { type = NavType.StringType; defaultValue = "" },
            navArgument("businessName") { type = NavType.StringType; defaultValue = "" }
        )
    ) { backStackEntry ->
        val savingProductId = backStackEntry.arguments?.getInt("savingProductId") ?: 0
        val mobileNumber = backStackEntry.arguments?.getString("mobileNumber") ?: ""
        val country = backStackEntry.arguments?.getString("country") ?: ""
        val email = backStackEntry.arguments?.getString("email") ?: ""
        val firstName = backStackEntry.arguments?.getString("firstName") ?: ""
        val lastName = backStackEntry.arguments?.getString("lastName") ?: ""
        val businessName = backStackEntry.arguments?.getString("businessName") ?: ""

        SignupScreen(
            onLoginSuccess = onLoginSuccess,
            savingProductId = savingProductId,
            mobileNumber = mobileNumber,
            country = country,
            email = email,
            firstName = firstName,
            lastName = lastName,
            businessName = businessName
        )
    }
}

fun NavController.navigateToSignup(
    savingProductId: Int = 0,
    mobileNumber: String = "",
    country: String = "",
    email: String = "",
    firstName: String = "",
    lastName: String = "",
    businessName: String = ""
) {
    this.navigate(
        "$SIGNUP_ROUTE?savingProductId=$savingProductId" +
                "&mobileNumber=$mobileNumber&country=$country&email=$email" +
                "&firstName=$firstName&lastName=$lastName&businessName=$businessName"
    )
}

@Suppress("UnusedParameter")
fun onRegisterSuccess(s: String?) {
    // registered but unable to login or user not updated with client
    // TODO :: Consider this case
    // 1. User not updated: when logging in update user
    // 2. User unable to login (must be caused due to server)
    // Toast.makeText(this, "Registered successfully.", Toast.LENGTH_SHORT).show()
    // startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
    // finish()
}