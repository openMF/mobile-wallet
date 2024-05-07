package org.mifospay.receipt.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import kotlinx.coroutines.launch
import org.mifospay.R
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.FloatingActionButtonContent
import org.mifospay.core.designsystem.component.MifosOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.ui.DevicePreviews
import org.mifospay.receipt.presenter.PassFileState
import org.mifospay.receipt.presenter.ReceiptUiState
import org.mifospay.receipt.presenter.ReceiptViewModel
import org.mifospay.theme.MifosTheme
import org.mifospay.utils.PermissionBox
import java.io.File

/**
 *  ReceiptActivity to compose migration
 *  PR link : https://github.com/openMF/mobile-wallet/pull/1618
 */
@Composable
fun DownloadReceipt(
    viewModel: ReceiptViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
    openPassCodeActivity: (Uri?) -> Unit
) {

    val receiptUiState by viewModel.receiptUiState.collectAsState()
    val fileState by viewModel.fileState.collectAsState()

    var receiptLink = ""
    val context = LocalContext.current
    val activity = context as? Activity
    val intent = activity?.intent
    val data = intent?.data

    data?.let {
        val scheme = it.scheme // "https"
        val host = it.host // "receipt.mifospay.com"
        val params = it.pathSegments
        val transactionId = params.getOrNull(0)
        try {
            receiptLink = data.toString()
        } catch (e: IndexOutOfBoundsException) {
            Toast.makeText(context, stringResource(R.string.invalid_link), Toast.LENGTH_SHORT)
                .show()
        }
        viewModel.fetchTransaction(transactionId)
    }

    DownloadReceipt(
        uiState = receiptUiState,
        viewFileState = fileState,
        downloadData = {
            viewModel.downloadReceipt(
                it
            )
        },
        receiptLink,
        openPassCodeActivity = openPassCodeActivity
    )
}

@Composable
fun DownloadReceipt(
    uiState: ReceiptUiState,
    viewFileState: PassFileState,
    downloadData: (String) -> Unit,
    receiptLink: String,
    openPassCodeActivity: (Uri?) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (uiState) {
            ReceiptUiState.Loading -> {
                MifosOverlayLoadingWheel(contentDesc = stringResource(R.string.loading))
            }

            ReceiptUiState.OpenPassCodeActivity -> {
                openPassCodeActivity(Uri.parse(receiptLink))
            }

            is ReceiptUiState.Error -> {
                val message = uiState.message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            is ReceiptUiState.Success -> {
                val transaction = uiState.transaction
                val transferDetail = uiState.transferDetail
                val file = viewFileState.file
                Receipt(
                    transaction = transaction,
                    transferDetail = transferDetail,
                    receiptLink,
                    downloadData = downloadData,
                    file
                )
            }
        }
    }
}

@Composable
fun Receipt(
    transaction: Transaction,
    transferDetail: TransferDetail,
    receiptLink: String,
    downloadData: (String) -> Unit,
    file: File
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var needToHandlePermissions = false

    val floatingActionButtonContent = FloatingActionButtonContent(
        onClick = {
            if (file.exists()) {
                openFile(context, file)
            } else {
                needToHandlePermissions = true
            }
        },
        contentColor = Color.Black,
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_download),
                contentDescription = stringResource(R.string.downloading_receipt)
            )
        }
    )

    if (needToHandlePermissions) {
        PermissionBox(
            requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
                listOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            },
            title = R.string.approve_permission_storage,
            description = R.string.approve_permission_storage_receiptDescription,
            onGranted = {
                downloadData(transaction.transactionId.toString())
                LaunchedEffect(Unit) {
                    scope.launch {
                        val userAction = snackBarHostState.showSnackbar(
                            message = R.string.download_complete.toString(),
                            actionLabel = R.string.view_Receipt.toString(),
                            duration = SnackbarDuration.Indefinite,
                            withDismissAction = true
                        )
                        when (userAction) {
                            SnackbarResult.ActionPerformed -> {
                                openFile(context, file)
                            }

                            SnackbarResult.Dismissed -> {}
                        }
                    }
                }
            }
        )
    }

    MifosScaffold(
        topBarTitle = R.string.receipt,
        backPress = {/* back press handler */ },
        floatingActionButtonContent = floatingActionButtonContent,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        scaffoldContent = { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier) {
                    Image(
                        painter = painterResource(id = R.drawable.mifospay_round_logo),
                        contentDescription = stringResource(R.string.pan_id),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    ShowHeadtexts(transaction, transferDetail)
                    Spacer(modifier = Modifier.size(height = 15.dp, width = 14.dp))
                    ShowInfoTexts(transaction, transferDetail, receiptLink)
                }
            }
        }
    )
}

fun copyToClipboard(
    context: Context,
    receiptLink: String
) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(
        Constants.UNIQUE_RECEIPT_LINK, receiptLink.trim { it <= ' ' }
    )
    clipboardManager.setPrimaryClip(clip)
    Toast.makeText(
        context,
        R.string.unique_receipt_link_copied_to_clipboard,
        Toast.LENGTH_SHORT
    ).show()
}

fun shareReceiptLink(
    shareMessage: String,
    context: Context
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooserIntent = Intent.createChooser(intent, context.getString(R.string.share_receipt))

    try {
        context.startActivity(chooserIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            R.string.sharing_link_failed,
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun openFile(context: Context, file: File) {
    val data = FileProvider.getUriForFile(
        context, "org.mifospay.provider", file
    )
    var intent: Intent? = Intent(Intent.ACTION_VIEW)
        .setDataAndType(data, "application/pdf")
    intent = Intent.createChooser(intent, context.getString(R.string.view_receipt))

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            R.string.opening_pdf_failed,
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun ShowHeadtexts(
    transaction: Transaction,
    transferDetail: TransferDetail
) {

    Column(Modifier.fillMaxWidth()) {
        val centerWithPaddingModifier = Modifier
            .padding(horizontal = 8.dp)
            .align(Alignment.CenterHorizontally)

        Text(
            text = transaction.amount.toString(),
            fontSize = 34.sp,
            modifier = centerWithPaddingModifier.padding(top = 10.dp)
        )

        Text(
            text = when (transaction.transactionType) {
                TransactionType.DEBIT -> stringResource(R.string.paid_to)
                TransactionType.CREDIT -> stringResource(R.string.credited_by)
                TransactionType.OTHER -> stringResource(R.string.other)
            },
            color = when (transaction.transactionType) {
                TransactionType.DEBIT -> Color.Red
                TransactionType.CREDIT -> Color.Cyan
                TransactionType.OTHER -> Color.Black
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = centerWithPaddingModifier.padding(top = 8.dp)
        )

        Text(
            text =
            if (transaction.transactionType == TransactionType.DEBIT) {
                transferDetail.toClient.displayName
            } else {
                transferDetail.fromClient.displayName
            },
            fontSize = 20.sp,
            modifier = centerWithPaddingModifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ShowInfoTexts(
    transaction: Transaction,
    transferDetail: TransferDetail,
    receiptLink: String,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.transaction_id),
            color = colorResource(R.color.gray_5),
            fontSize = 16.sp
        )
        Text(
            text = transaction.transactionId.toString(),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.transaction_date),
            color = colorResource(R.color.gray_5),
            fontSize = 16.sp
        )
        Text(
            text = transaction.date.toString(),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.to),
            color = colorResource(R.color.gray_5),
            fontSize = 16.sp
        )
        Text(
            text = stringResource(R.string.name) + transferDetail.toClient.displayName,
            fontSize = 16.sp
        )
        Text(
            text = stringResource(R.string.account_no) + transferDetail.toAccount.accountNo,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.size(height = 50.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.from),
            color = colorResource(R.color.gray_5),
            fontSize = 16.sp
        )
        Text(
            text = stringResource(R.string.name) + transferDetail.fromClient.displayName,
            fontSize = 16.sp
        )
        Text(
            text = stringResource(R.string.account_no) + transferDetail.fromAccount.accountNo,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.size(height = 50.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.unique_receipt_link),
            color = colorResource(R.color.gray_5),
            fontSize = 16.sp
        )
    }
    BottomIcons(transferDetail, receiptLink)
}

@Composable
fun BottomIcons(
    transferDetail: TransferDetail,
    receiptLink: String,
) {
    val context = LocalContext.current
    val prepareShareMessage =
        Constants.RECEIPT_SHARING_MESSAGE + transferDetail.fromClient.displayName +
                Constants.TO +
                transferDetail.toClient.displayName +
                Constants.COLON +
                receiptLink.trim { it <= ' ' }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = receiptLink,
            color = Color.Blue,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.size(height = 0.dp, width = 5.dp))

        Icon(
            Icons.Filled.ContentCopy,
            contentDescription = stringResource(R.string.copy_link),
            modifier = Modifier
                .size(25.dp)
                .clickable {
                    copyToClipboard(context, receiptLink)
                },
            tint = Color.Black
        )

        Icon(
            Icons.Filled.Share,
            contentDescription = stringResource(R.string.share_receipt),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(25.dp)
                .clickable {
                    shareReceiptLink(prepareShareMessage, context)
                },
            tint = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithDummyData() {
    Receipt(
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
            "12345"
        ),
        transferDetail = TransferDetail(),
        receiptLink = "https://receipt.mifospay.com/12345",
        downloadData = {},
        file = File("/path/to/receipt.pdf")
    )
}

@DevicePreviews
@Composable
fun MultiScreenReceiptPreviewWithDummyData() {
    Receipt(
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
            "12345"
        ),
        transferDetail = TransferDetail(),
        receiptLink = "https://receipt.mifospay.com/12345",
        downloadData = {},
        file = File("/path/to/receipt.pdf")
    )
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithLoading() {
    MifosTheme {
        DownloadReceipt(
            uiState = ReceiptUiState.Loading,
            viewFileState = PassFileState(file = File(" ")),
            downloadData = {},
            receiptLink = "https://receipt.mifospay.com/12345",
            openPassCodeActivity = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithErrorMessage() {
    MifosTheme {
        DownloadReceipt(
            uiState = ReceiptUiState.Error(stringResource(R.string.error_specific_transactions)),
            viewFileState = PassFileState(file = File(" ")),
            downloadData = {},
            receiptLink = "https://receipt.mifospay.com/12345",
            openPassCodeActivity = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithSuccess() {
    MifosTheme {
        DownloadReceipt(
            uiState = ReceiptUiState.Success(Transaction(), TransferDetail()),
            viewFileState = PassFileState(file = File(" ")),
            downloadData = {},
            receiptLink = "https://receipt.mifospay.com/12345",
            openPassCodeActivity = {}
        )
    }
}

