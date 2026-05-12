/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
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
            onDismiss = onDismiss,
        )
    }
}
