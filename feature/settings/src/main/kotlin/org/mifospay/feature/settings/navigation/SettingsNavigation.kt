package org.mifospay.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.settings.SettingsScreenRoute

const val SETTINGS_ROUTE = "settings_route"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) = navigate(SETTINGS_ROUTE)

fun NavGraphBuilder.settingsScreen(
    onBackPress: () -> Unit,
    navigateToEditPasswordScreen:() -> Unit
) {
    composable(route = SETTINGS_ROUTE) {
        SettingsScreenRoute(
            backPress = onBackPress,
            navigateToEditPasswordScreen = navigateToEditPasswordScreen
        )
    }
}