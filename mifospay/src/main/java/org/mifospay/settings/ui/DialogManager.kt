package org.mifospay.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.mifospay.R
import org.mifospay.core.designsystem.component.MifosDialogBox
import org.mifospay.core.ui.utility.DialogState
import org.mifospay.core.ui.utility.DialogType

@Composable
fun DialogManager(dialogState: DialogState, onDismiss: () -> Unit) {
    when (dialogState.type) {
        DialogType.DISABLE_ACCOUNT -> MifosDialogBox(
            showDialogState = true,
            title = R.string.alert_disable_account,
            message = R.string.alert_disable_account_desc,
            confirmButtonText = R.string.ok,
            dismissButtonText = R.string.cancel,
            onConfirm = dialogState.onConfirm,
            onDismiss = onDismiss
        )

        DialogType.LOGOUT -> MifosDialogBox(
            showDialogState =true,
            title = R.string.log_out_title,
            message = R.string.empty,
            confirmButtonText = R.string.yes,
            dismissButtonText = R.string.no,
            onConfirm = dialogState.onConfirm,
            onDismiss = onDismiss
        )

        DialogType.NONE -> {}
    }
}
