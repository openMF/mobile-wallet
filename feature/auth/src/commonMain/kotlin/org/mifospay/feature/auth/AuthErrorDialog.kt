/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.mifospay.core.common.GlobalAuthManager
import org.mifospay.core.data.util.Constants.UNAUTHORIZED_ERROR
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog

@Composable
fun AuthErrorDialog(
    onLogout: () -> Unit,
) {
    val dialogState = remember { mutableStateOf<BasicDialogState>(BasicDialogState.Hidden) }

    LaunchedEffect(Unit) {
        GlobalAuthManager.isUnauthorized.collect { unauthorized ->
            if (unauthorized) {
                dialogState.value = BasicDialogState.Shown(
                    title = "Unauthorized 401",
                    message = UNAUTHORIZED_ERROR,
                )
            }
        }
    }

    MifosBasicDialog(
        visibilityState = dialogState.value,
        onDismissRequest = {
            dialogState.value = BasicDialogState.Hidden
            onLogout()
        },
    )
}
