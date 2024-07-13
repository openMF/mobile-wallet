package org.mifospay.feature.editpassword.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.mifospay.feature.editpassword.EditPasswordScreen

const val EDIT_PASSWORD_ROUTE = "edit_password_route"

fun NavGraphBuilder.editPasswordScreen(
    onBackPress: () -> Unit,
    onCancelChanges: () -> Unit
) {
    composable(route = EDIT_PASSWORD_ROUTE) {
        EditPasswordScreen(
            onBackPress = onBackPress,
            onCancelChanges = onCancelChanges
        )
    }
}

fun NavController.navigateToEditPassword() {
    this.navigate(EDIT_PASSWORD_ROUTE)
}