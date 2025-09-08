package org.mifospay.feature.make.transfer.v2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.make.transfer.MakeTransferAction
import org.mifospay.feature.make.transfer.MakeTransferDialogs
import org.mifospay.feature.make.transfer.MakeTransferEvent
import org.mifospay.feature.make.transfer.MakeTransferScreen
import org.mifospay.feature.make.transfer.MakeTransferState
import org.mifospay.feature.make.transfer.MakeTransferViewModel


@Composable
internal fun MakeTransferScreenV2(
    navigateBack: () -> Unit,
    onTransferSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MakeTransferViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val accountState by viewModel.accountsState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            MakeTransferEvent.OnNavigateBack -> navigateBack.invoke()
            MakeTransferEvent.OnTransferSuccess -> onTransferSuccess.invoke()
        }
    }

    MakeTransferDialogsV2(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(MakeTransferAction.DismissDialog) }
        },
    )

    MakeTransferScreen(
        state = state,
        accountState = accountState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun MakeTransferDialogsV2(
    dialogState: MakeTransferState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is MakeTransferState.DialogState.Error -> {
            val message = when (dialogState) {
                is MakeTransferState.DialogState.Error.StringMessage -> dialogState.message
                is MakeTransferState.DialogState.Error.ResourceMessage -> stringResource(dialogState.message)
            }
            MifosBasicDialog(
                visibilityState = BasicDialogState.Shown(
                    message = message,
                ),
                onDismissRequest = onDismissRequest,
            )
        }
        is MakeTransferState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )
        null -> Unit
    }
}