package org.mifos.mobilewallet.mifospay.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource

@Composable
fun MifosDialogBox(
    showDialogState: MutableState<Boolean>,
    onDismiss: () -> Unit,
    title: Int,
    message: Int? = null,
    confirmButtonText: Int,
    onConfirm: () -> Unit,
    dismissButtonText: Int
) {
    if (showDialogState.value) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(id = title)) },
            text = {
                if (message != null) {
                    Text(text = stringResource(id = message))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text(stringResource(id = confirmButtonText))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = dismissButtonText))
                }
            }
        )
    }
}
