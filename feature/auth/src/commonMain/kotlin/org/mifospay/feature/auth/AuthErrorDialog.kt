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
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog

@Composable
fun AuthErrorDialog(
    dialogState: BasicDialogState,
    onDismiss: () -> Unit,
) {
    MifosBasicDialog(
        visibilityState = dialogState,
        onDismissRequest = onDismiss,
    )
}
