package org.mifospay.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.profile.edit.EditProfileScreenRoute

const val EDIT_PROFILE_ROUTE = "edit_profile_route"
fun NavController.navigateToEditProfile(navOptions: NavOptions? = null) = navigate(EDIT_PROFILE_ROUTE, navOptions)

fun NavGraphBuilder.editProfileScreen(
    onBackPress: () -> Unit,
) {
    composable(route = EDIT_PROFILE_ROUTE) {
        EditProfileScreenRoute(
            onBackClick = onBackPress
        )
    }
}
