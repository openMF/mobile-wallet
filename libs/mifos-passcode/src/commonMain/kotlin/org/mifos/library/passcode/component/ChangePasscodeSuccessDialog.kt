/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.library.passcode.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import mobile_wallet.libs.mifos_passcode.generated.resources.Res
import mobile_wallet.libs.mifos_passcode.generated.resources.library_mifos_passcode_done
import mobile_wallet.libs.mifos_passcode.generated.resources.library_mifos_passcode_passcode_changed_message
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun ChangePasscodeSuccessDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (visible) {
        AlertDialog(
            shape = MaterialTheme.shapes.large,
            containerColor = KptTheme.colorScheme.surface,
            title = {
                Text(
                    text = stringResource(Res.string.library_mifos_passcode_passcode_changed_message),
                    color = KptTheme.colorScheme.onSurface,
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(Res.string.library_mifos_passcode_done),
                        color = KptTheme.colorScheme.onSurface,
                    )
                }
            },
            onDismissRequest = onDismiss,
        )
    }
}
