package org.mifospay.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.auth.login.LoginScreen

const val LOGIN_ROUTE = "login_route"

@Suppress("UnusedParameter")
fun NavGraphBuilder.loginScreen(
    onDismissSignUp: () -> Unit,
    onNavigateToMobileVerificationScreen:(Int,String,String,String,String,) -> Unit
) {
    composable(route = LOGIN_ROUTE) {
        LoginScreen(
//            onDismissSignUp = onDismissSignUp,
//            onNavigateToMobileVerificationScreen = onNavigateToMobileVerificationScreen
        )
    }
}

fun NavController.navigateToLogin() {
    this.navigate(LOGIN_ROUTE)
}