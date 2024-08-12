/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.receipt

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import kotlinx.coroutines.launch
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.FloatingActionButtonContent
import org.mifospay.core.designsystem.component.MifosOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.PermissionBox
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.DevicePreviews
import org.mifospay.receipt.R
import java.io.File

@Composable
internal fun ReceiptScreenRoute(
    openPassCodeActivity: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptViewModel = hiltViewModel(),
) {
    /**
     * This function serves as the main entry point for the Receipt screen UI.
     * It collects the receiptUiState and fileState from the ViewModel and
     * calls the ReceiptScreen function, passing the collected states and
     * other necessary parameters.
     */
    val receiptUiState by viewModel.receiptUiState.collectAsState()
    val fileState by viewModel.fileState.collectAsState()

    ReceiptScreen(
        uiState = receiptUiState,
        viewFileState = fileState,
        downloadReceipt = viewModel::downloadReceipt,
        openPassCodeActivity = openPassCodeActivity,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun ReceiptScreen(
    uiState: ReceiptUiState,
    viewFileState: PassFileState,
    downloadReceipt: (String) -> Unit,
    openPassCodeActivity: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    /**
     * This function renders the UI based on the ReceiptUiState and PassFileState.
     * The UI is rendered based on the ReceiptUiState using a when expression.
     */
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        when (uiState) {
            ReceiptUiState.Loading -> {
                MifosOverlayLoadingWheel(contentDesc = stringResource(R.string.feature_receipt_loading))
            }

            ReceiptUiState.OpenPassCodeActivity -> {
                openPassCodeActivity.invoke()
            }

            is ReceiptUiState.Error -> {
                val message = uiState.message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            is ReceiptUiState.Success -> {
                ReceiptScreenContent(
                    transaction = uiState.transaction,
                    transferDetail = uiState.transferDetail,
                    receiptLink = uiState.receiptLink,
                    downloadData = downloadReceipt,
                    file = viewFileState.file,
                    onBackClick = onBackClick,
                )
            }
        }
    }
}

/**
 * The following function renders the actual content of the Receipt screen.
 * It includes components like MifosScaffold, SnackbarHost, PermissionBox,
 * and various UI elements like Text, Image, and Icon.
 */
@Composable
private fun ReceiptScreenContent(
    transaction: Transaction,
    transferDetail: TransferDetail,
    receiptLink: String,
    file: File,
    downloadData: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var needToHandlePermissions = rememberSaveable { false }

    val floatingActionButtonContent = FloatingActionButtonContent(
        onClick = {
            if (file.exists()) {
                openReceiptFile(context, file)
            } else {
                needToHandlePermissions = true
            }
        },
        contentColor = MaterialTheme.colorScheme.onSurface,
        content = {
            Icon(
                painter = painterResource(id = R.drawable.feature_receipt_ic_download),
                contentDescription = stringResource(R.string.feature_receipt_downloading_receipt),
            )
        },
    )

    if (needToHandlePermissions) {
        PermissionBox(
            requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
                listOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            },
            title = R.string.feature_receipt_approve_permission_storage,
            description = R.string.feature_receipt_approve_permission_storage_receiptDescription,
            confirmButtonText = R.string.feature_receipt_proceed,
            dismissButtonText = R.string.feature_receipt_dismiss,
            onGranted = {
                downloadData(transaction.transactionId.toString())
                LaunchedEffect(Unit) {
                    scope.launch {
                        val userAction = snackBarHostState.showSnackbar(
                            message = R.string.feature_receipt_download_complete.toString(),
                            actionLabel = R.string.feature_receipt_view_Receipt.toString(),
                            duration = SnackbarDuration.Indefinite,
                            withDismissAction = true,
                        )
                        when (userAction) {
                            SnackbarResult.ActionPerformed -> {
                                openReceiptFile(context, file)
                            }

                            SnackbarResult.Dismissed -> {}
                        }
                    }
                }
            },
        )
    }

    MifosScaffold(
        topBarTitle = R.string.feature_receipt_receipt,
        backPress = onBackClick,
        floatingActionButtonContent = floatingActionButtonContent,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        scaffoldContent = { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.feature_receipt_mifospay_round_logo),
                        contentDescription = stringResource(R.string.feature_receipt_pan_id),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                    ReceiptHeaderBody(transaction, transferDetail)
                    Spacer(modifier = Modifier.size(height = 15.dp, width = 14.dp))
                    ReceiptDetailsBody(transaction, transferDetail, receiptLink)
                }
            }
        },
        modifier = modifier,
    )
}

/**
 * This function copies the given receiptLink to the system clipboard
 * and displays a snackbar message to indicate successful copying.
 * Used in ReceiptLinkActions.
 */
private fun copyToClipboard(
    context: Context,
    receiptLink: String,
) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(
        Constants.UNIQUE_RECEIPT_LINK,
        receiptLink.trim { it <= ' ' },
    )
    clipboardManager.setPrimaryClip(clip)
    onShowSnackbar(R.string.feature_receipt_unique_receipt_link_copied_to_clipboard, context)
}

/**
 * This function displays a toast message with the provided string resource.
 * We will use onShowSnackbar(global Snackbar) when its added in navigation graph
 */
private fun onShowSnackbar(string: Int, context: Context) {
    // onShowSnackbar(string,string)
    Toast.makeText(
        context,
        string,
        Toast.LENGTH_SHORT,
    ).show()
}

/**
 * This function shares the given shareMessage using an Intent and is called in ReceiptLinkActions.
 * It displays a chooser to select the app for sharing.
 */
private fun shareReceiptMessage(
    shareMessage: String,
    context: Context,
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooserIntent =
        Intent.createChooser(intent, context.getString(R.string.feature_receipt_share_receipt))

    try {
        context.startActivity(chooserIntent)
    } catch (e: ActivityNotFoundException) {
        onShowSnackbar(R.string.feature_receipt_sharing_link_failed, context)
    }
}

/**
 * This function opens the given receipt file pdf using an Intent and is called in ReceiptScreen.
 * It displays a chooser to select the app for opening the file.
 */
private fun openReceiptFile(
    context: Context,
    file: File,
) {
    val data = FileProvider.getUriForFile(
        context,
        "org.mifospay.provider",
        file,
    )
    var intent: Intent? = Intent(Intent.ACTION_VIEW)
        .setDataAndType(data, "application/pdf")
    intent = Intent.createChooser(intent, context.getString(R.string.feature_receipt_view_receipt))

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onShowSnackbar(R.string.feature_receipt_opening_pdf_failed, context)
    }
}

@Composable
private fun ReceiptHeaderBody(
    transaction: Transaction,
    transferDetail: TransferDetail,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        val centerWithPaddingModifier = Modifier
            .padding(horizontal = 8.dp)
            .align(Alignment.CenterHorizontally)

        Text(
            text = transaction.amount.toString(),
            style = TextStyle(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.typography.headlineLarge.fontSize,
            ),
            modifier = centerWithPaddingModifier.padding(top = 10.dp),
        )

        Text(
            text = when (transaction.transactionType) {
                TransactionType.DEBIT -> stringResource(R.string.feature_receipt_paid_to)
                TransactionType.CREDIT -> stringResource(R.string.feature_receipt_credited_by)
                TransactionType.OTHER -> stringResource(R.string.feature_receipt_other)
            },
            color = when (transaction.transactionType) {
                TransactionType.DEBIT -> Color.Red
                TransactionType.CREDIT -> Color.Cyan
                TransactionType.OTHER -> Color.Black
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = centerWithPaddingModifier.padding(top = 8.dp),
        )

        Text(
            text =
            if (transaction.transactionType == TransactionType.DEBIT) {
                transferDetail.toClient.displayName
            } else {
                transferDetail.fromClient.displayName
            },
            fontSize = 20.sp,
            modifier = centerWithPaddingModifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun ReceiptDetailsBody(
    transaction: Transaction,
    transferDetail: TransferDetail,
    receiptLink: String,
    modifier: Modifier = Modifier,
) {
    /**
     * This function renders the transaction details and receipt link section,
     * displaying information such as transaction ID, date, account details, and the
     * receipt link.
     */
    Column(
        modifier = modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.feature_receipt_transaction_id),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )
        Text(
            text = transaction.transactionId.toString(),
            style = TextStyle(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.feature_receipt_transaction_date),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )
        Text(
            text = transaction.date.toString(),
            style = TextStyle(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.feature_receipt_to),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )
        Text(
            text = stringResource(R.string.feature_receipt_name) + transferDetail.toClient.displayName,
            style = TextStyle(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )
        Text(
            text = stringResource(R.string.feature_receipt_account_no) + transferDetail.toAccount.accountNo,
            style = TextStyle(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.feature_receipt_from),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )
        Text(
            text = stringResource(R.string.feature_receipt_name) + transferDetail.fromClient.displayName,
            style = TextStyle(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )
        Text(
            text = stringResource(R.string.feature_receipt_account_no) + transferDetail.fromAccount.accountNo,
            style = TextStyle(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.feature_receipt_unique_receipt_link),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize,
            ),
        )

        ReceiptLinkActions(transferDetail, receiptLink)
    }
}

@Composable
private fun ReceiptLinkActions(
    transferDetail: TransferDetail,
    receiptLink: String,
    modifier: Modifier = Modifier,
) {
    /**
     * This function renders the copy and share icons at the bottom of the screen,
     * allowing users to copy the receipt link or share the receipt message.
     */
    val context = LocalContext.current
    val prepareShareMessage =
        Constants.RECEIPT_SHARING_MESSAGE + transferDetail.fromClient.displayName +
            Constants.TO +
            transferDetail.toClient.displayName +
            Constants.COLON +
            receiptLink.trim { it <= ' ' }

    Row(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = receiptLink,
            style = TextStyle(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.typography.bodyMedium.fontSize,
            ),
        )

        Spacer(modifier = Modifier.size(height = 0.dp, width = 5.dp))

        Icon(
            MifosIcons.Copy,
            contentDescription = stringResource(R.string.feature_receipt_copy_link),
            modifier = Modifier
                .size(25.dp)
                .clickable {
                    copyToClipboard(context, receiptLink)
                },
            tint = MaterialTheme.colorScheme.onSurface,
        )

        Icon(
            MifosIcons.Share,
            contentDescription = stringResource(R.string.feature_receipt_share_receipt),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(25.dp)
                .clickable {
                    shareReceiptMessage(prepareShareMessage, context)
                },
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@DevicePreviews
@Composable
private fun MultiScreenReceiptPreviewWithDummyData() {
    ReceiptScreenContent(
        transaction = Transaction(
            "12345",
            12345,
            12345,
            312.0,
            "01/04/2024",
            com.mifospay.core.model.domain.Currency(),
            TransactionType.DEBIT,
            12345,
            TransferDetail(),
            "12345",
        ),
        transferDetail = TransferDetail(),
        receiptLink = "https://receipt.mifospay.com/12345",
        downloadData = {},
        file = File("/path/to/receipt.pdf"),
        onBackClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ReceiptPreviewWithLoading() {
    MifosTheme {
        ReceiptScreen(
            uiState = ReceiptUiState.Loading,
            viewFileState = PassFileState(file = File(" ")),
            downloadReceipt = {},
            openPassCodeActivity = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptPreviewWithErrorMessage() {
    MifosTheme {
        ReceiptScreen(
            uiState = ReceiptUiState.Error(stringResource(R.string.feature_receipt_error_specific_transactions)),
            viewFileState = PassFileState(file = File(" ")),
            downloadReceipt = {},
            openPassCodeActivity = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptPreviewWithSuccess() {
    MifosTheme {
        ReceiptScreen(
            uiState = ReceiptUiState.Success(
                Transaction(),
                TransferDetail(),
                receiptLink = "https://receipt.mifospay.com/12345",
            ),
            viewFileState = PassFileState(file = File(" ")),
            downloadReceipt = {},
            openPassCodeActivity = {},
            onBackClick = {},
        )
    }
}
