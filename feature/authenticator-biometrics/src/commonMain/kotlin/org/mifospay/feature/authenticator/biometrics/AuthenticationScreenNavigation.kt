package org.mifospay.feature.authenticator.biometrics

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable


const val PLATFORM_AUTHENTICATOR = "platform_authenticator"

fun NavController.navigateToPlatformAuthenticator(navOptions: NavOptions? = null) =
    navigate(PLATFORM_AUTHENTICATOR, navOptions)

fun NavGraphBuilder.platformAuthenticator(
    onAuthenticationSuccess: () -> Unit,
    onForcedLogOut:() -> Unit,
){
    composable(route = PLATFORM_AUTHENTICATOR) {
        AuthenticationScreen(
            onAuthenticationSuccess,
            onForcedLogOut,
        )
    }
}