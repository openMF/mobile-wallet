/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.ui.utility.DialogState
import org.mifospay.core.ui.utility.DialogType

@Composable
internal fun DialogManager(
    dialogState: DialogState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (dialogState.type) {
        DialogType.DISABLE_ACCOUNT -> MifosDialogBox(
            showDialogState = true,
            onDismiss = onDismiss,
            title = R.string.feature_settings_alert_disable_account,
            confirmButtonText = R.string.feature_settings_ok,
            onConfirm = dialogState.onConfirm,
            dismissButtonText = R.string.feature_settings_cancel,
            message = R.string.feature_settings_alert_disable_account_desc,
            modifier = modifier,
        )

        DialogType.LOGOUT -> MifosDialogBox(
            showDialogState = true,
            onDismiss = onDismiss,
            title = R.string.feature_settings_log_out_title,
            confirmButtonText = R.string.feature_settings_yes,
            onConfirm = dialogState.onConfirm,
            dismissButtonText = R.string.feature_settings_no,
            message = R.string.feature_settings_empty,
            modifier = modifier,
        )

        DialogType.NONE -> {}
    }
}
