package org.mifospay.feature.make.transfer.v2

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class MakeTransferScreenV2Route(
    val clientId: Long,
    val clientName: String,
    val accountNo: String,
    val amount: Int,
    val accountId: Long,
)

fun NavController.navigateToMakeTransferScreenV2(
    clientId: Long,
    clientName: String,
    accountNo: String,
    amount: Int =0,
    accountId: Long,
    navOptions: NavOptions? = null
) {
    this.navigate(MakeTransferScreenV2Route(clientId, clientName, accountNo, amount, accountId), navOptions)
}

fun NavGraphBuilder.makeTransferScreenV2(
    navigateBack: () -> Unit,
    onTransferSuccess: () -> Unit,
) {
    composable<MakeTransferScreenV2Route> {
        MakeTransferScreenV2(
            navigateBack = navigateBack,
            onTransferSuccess = onTransferSuccess
        )
    }
}