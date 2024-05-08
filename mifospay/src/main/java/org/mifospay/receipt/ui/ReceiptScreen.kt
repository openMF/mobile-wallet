package org.mifospay.receipt.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import org.mifospay.R
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.FloatingActionButtonContent
import org.mifospay.core.designsystem.component.MifosOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.PermissionBox
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.DevicePreviews
import org.mifospay.receipt.presenter.PassFileState
import org.mifospay.receipt.presenter.ReceiptUiState
import org.mifospay.receipt.presenter.ReceiptViewModel
import org.mifospay.theme.MifosTheme
import java.io.File

/**
 *  ReceiptActivity to compose migration
 *  PR link : https://github.com/openMF/mobile-wallet/pull/1618
 */
@Composable
fun ReceiptScreenRoute(
    viewModel: ReceiptViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
    openPassCodeActivity: () -> Unit,
    onBackClick: () -> Unit
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
        downloadReceipt = {
            viewModel.downloadReceipt(
                it
            )
        },
        openPassCodeActivity = openPassCodeActivity,
        onBackClick = onBackClick
    )
}

@Composable
fun ReceiptScreen(
    uiState: ReceiptUiState,
    viewFileState: PassFileState,
    downloadReceipt: (String) -> Unit,
    openPassCodeActivity: () -> Unit,
    onBackClick: () -> Unit
) {
    /**
     * This function renders the UI based on the ReceiptUiState and PassFileState.
     * The UI is rendered based on the ReceiptUiState using a when expression.
     */
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
                openPassCodeActivity.invoke()
            }

            is ReceiptUiState.Error -> {
                val message = uiState.message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            is ReceiptUiState.Success -> {
                val transaction = uiState.transaction
                val transferDetail = uiState.transferDetail
                val receiptLink = uiState.receiptLink
                val file = viewFileState.file
                ReceiptScreenContent(
                    transaction = transaction,
                    transferDetail = transferDetail,
                    receiptLink,
                    downloadData = downloadReceipt,
                    file,
                    onBackClick = onBackClick
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
fun ReceiptScreenContent(
    transaction: Transaction,
    transferDetail: TransferDetail,
    receiptLink: String,
    downloadData: (String) -> Unit,
    file: File,
    onBackClick: () -> Unit
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
                                openReceiptFile(context, file)
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
        backPress = { onBackClick.invoke() },
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
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mifospay_round_logo),
                        contentDescription = stringResource(R.string.pan_id),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    ReceiptHeaderBody(transaction, transferDetail)
                    Spacer(modifier = Modifier.size(height = 15.dp, width = 14.dp))
                    ReceiptDetailsBody(transaction, transferDetail, receiptLink)
                }
            }
        }
    )
}

/**
 * This function copies the given receiptLink to the system clipboard
 * and displays a snackbar message to indicate successful copying.
 * Used in ReceiptLinkActions.
 */
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
    onShowSnackbar(R.string.unique_receipt_link_copied_to_clipboard, context)
}

/**
 * This function displays a toast message with the provided string resource.
 * We will use onShowSnackbar(global Snackbar) when its added in navigation graph
 */
fun onShowSnackbar(string: Int, context: Context) {
    //onShowSnackbar(string,string)
    Toast.makeText(
        context,
        string,
        Toast.LENGTH_SHORT
    ).show()
}

/**
 * This function shares the given shareMessage using an Intent and is called in ReceiptLinkActions.
 * It displays a chooser to select the app for sharing.
 */
fun shareReceiptMessage(
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
        onShowSnackbar(R.string.sharing_link_failed, context)
    }
}

/**
 * This function opens the given receipt file pdf using an Intent and is called in ReceiptScreen.
 * It displays a chooser to select the app for opening the file.
 */
fun openReceiptFile(context: Context, file: File) {
    val data = FileProvider.getUriForFile(
        context, "org.mifospay.provider", file
    )
    var intent: Intent? = Intent(Intent.ACTION_VIEW)
        .setDataAndType(data, "application/pdf")
    intent = Intent.createChooser(intent, context.getString(R.string.view_receipt))

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onShowSnackbar(R.string.opening_pdf_failed, context)
    }
}

@Composable
fun ReceiptHeaderBody(
    transaction: Transaction,
    transferDetail: TransferDetail
) {

    /**
     * This function renders the header section of the Receipt screen,
     * displaying the transaction amount, type, and the client names involved
     * in the transaction.
     */
    Column(Modifier.fillMaxWidth()) {
        val centerWithPaddingModifier = Modifier
            .padding(horizontal = 8.dp)
            .align(Alignment.CenterHorizontally)

        Text(
            text = transaction.amount.toString(),
            style = TextStyle(
                Color.Black,
                MaterialTheme.typography.headlineLarge.fontSize
            ),
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
fun ReceiptDetailsBody(
    transaction: Transaction,
    transferDetail: TransferDetail,
    receiptLink: String,
) {

    /**
     * This function renders the transaction details and receipt link section,
     * displaying information such as transaction ID, date, account details, and the
     * receipt link.
     */
    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.transaction_id),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )
        Text(
            text = transaction.transactionId.toString(),
            style = TextStyle(
                Color.Black,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.transaction_date),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )
        Text(
            text = transaction.date.toString(),
            style = TextStyle(
                Color.Black,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.to),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )
        Text(
            text = stringResource(R.string.name) + transferDetail.toClient.displayName,
            style = TextStyle(
                Color.Black,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )
        Text(
            text = stringResource(R.string.account_no) + transferDetail.toAccount.accountNo,
            style = TextStyle(
                Color.Black,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.from),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )
        Text(
            text = stringResource(R.string.name) + transferDetail.fromClient.displayName,
            style = TextStyle(
                Color.Black,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )
        Text(
            text = stringResource(R.string.account_no) + transferDetail.fromAccount.accountNo,
            style = TextStyle(
                Color.Black,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )

        Spacer(modifier = Modifier.size(height = 30.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.unique_receipt_link),
            style = TextStyle(
                Color.Gray,
                MaterialTheme.typography.bodyLarge.fontSize
            )
        )
    }
    ReceiptLinkActions(transferDetail, receiptLink)
}

@Composable
fun ReceiptLinkActions(
    transferDetail: TransferDetail,
    receiptLink: String,
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            text = receiptLink,
            style = TextStyle(
                Color.Blue,
                MaterialTheme.typography.bodyMedium.fontSize
            )
        )

        Spacer(modifier = Modifier.size(height = 0.dp, width = 5.dp))

        Icon(
            MifosIcons.Copy,
            contentDescription = stringResource(R.string.copy_link),
            modifier = Modifier
                .size(25.dp)
                .clickable {
                    copyToClipboard(context, receiptLink)
                },
            tint = Color.Black
        )

        Icon(
            MifosIcons.Share,
            contentDescription = stringResource(R.string.share_receipt),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(25.dp)
                .clickable {
                    shareReceiptMessage(prepareShareMessage, context)
                },
            tint = Color.Black
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ReceiptPreviewWithDummyData() {
//    ReceiptScreenContent(
//        transaction = Transaction(
//            "12345",
//            12345,
//            12345,
//            312.0,
//            "01/04/2024",
//            com.mifospay.core.model.domain.Currency(),
//            TransactionType.DEBIT,
//            12345,
//            TransferDetail(),
//            "12345"
//        ),
//        transferDetail = TransferDetail(),
//        receiptLink = "https://receipt.mifospay.com/12345",
//        downloadData = {},
//        file = File("/path/to/receipt.pdf"),
//        onBackClick = {}
//    )
//}

@DevicePreviews
@Composable
fun MultiScreenReceiptPreviewWithDummyData() {
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
            "12345"
        ),
        transferDetail = TransferDetail(),
        receiptLink = "https://receipt.mifospay.com/12345",
        downloadData = {},
        file = File("/path/to/receipt.pdf"),
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithLoading() {
    MifosTheme {
        ReceiptScreen(
            uiState = ReceiptUiState.Loading,
            viewFileState = PassFileState(file = File(" ")),
            downloadReceipt = {},
            openPassCodeActivity = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithErrorMessage() {
    MifosTheme {
        ReceiptScreen(
            uiState = ReceiptUiState.Error(stringResource(R.string.error_specific_transactions)),
            viewFileState = PassFileState(file = File(" ")),
            downloadReceipt = {},
            openPassCodeActivity = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithSuccess() {
    MifosTheme {
        ReceiptScreen(
            uiState = ReceiptUiState.Success(
                Transaction(),
                TransferDetail(),
                receiptLink = "https://receipt.mifospay.com/12345"
            ),
            viewFileState = PassFileState(file = File(" ")),
            downloadReceipt = {},
            openPassCodeActivity = {},
            onBackClick = {}
        )
    }
}

