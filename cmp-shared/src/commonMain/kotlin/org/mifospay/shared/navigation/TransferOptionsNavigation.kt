/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.shared.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import org.mifospay.shared.ui.components.TransferOptionsBottomSheet

@Serializable
data object TransferOptionsRoute

fun NavController.navigateToTransferOptions() {
    this.navigate(TransferOptionsRoute)
}

fun NavGraphBuilder.transferOptionsDialog(
    onIntraBankTransferClick: () -> Unit,
    onInterBankTransferClick: () -> Unit,
    onUpiSendMoney: () -> Unit,
    onDismiss: () -> Unit,
) {
    dialog<TransferOptionsRoute> {
        TransferOptionsBottomSheet(
            onIntraBankTransferClick = {
                onIntraBankTransferClick()
            },
            onInterBankTransferClick = {
                onInterBankTransferClick()
            },
            onUpiSendMoney = {
                onUpiSendMoney()
            },
            onDismiss = onDismiss,
        )
    }
}
