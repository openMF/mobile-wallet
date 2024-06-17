package org.mifospay.feature.settings

import androidx.compose.runtime.Composable
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.ui.utility.DialogState
import org.mifospay.core.ui.utility.DialogType
import org.mifospay.settings.R

@Composable
fun DialogManager(dialogState: DialogState, onDismiss: () -> Unit) {
    when (dialogState.type) {
        DialogType.DISABLE_ACCOUNT -> MifosDialogBox(
            showDialogState = true,
            title = R.string.feature_settings_alert_disable_account,
            message = R.string.feature_settings_alert_disable_account_desc,
            confirmButtonText = R.string.feature_settings_ok,
            dismissButtonText = R.string.feature_settings_cancel,
            onConfirm = dialogState.onConfirm,
            onDismiss = onDismiss
        )

        DialogType.LOGOUT -> MifosDialogBox(
            showDialogState = true,
            title = R.string.feature_settings_log_out_title,
            message = R.string.feature_settings_empty,
            confirmButtonText = R.string.feature_settings_yes,
            dismissButtonText = R.string.feature_settings_no,
            onConfirm = dialogState.onConfirm,
            onDismiss = onDismiss
        )

        DialogType.NONE -> {}
    }
}
