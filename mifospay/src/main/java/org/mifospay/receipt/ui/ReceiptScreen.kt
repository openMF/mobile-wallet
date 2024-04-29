package org.mifospay.receipt.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mifos.mobile.passcode.utils.PassCodeConstants
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import kotlinx.coroutines.launch
import org.mifospay.R
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MifosOverlayLoadingWheel
import org.mifospay.feature.passcode.PassCodeActivity
import org.mifospay.receipt.presenter.PassFileState
import org.mifospay.receipt.presenter.ReceiptUiState
import org.mifospay.receipt.presenter.ReceiptViewModel
import org.mifospay.theme.MifosTheme
import java.io.File


@Composable
fun ReceiptScreen(
    viewModel: ReceiptViewModel = hiltViewModel()
) {

    val receiptUiState by viewModel.receiptUiState.collectAsState()
    val fileState by viewModel.fileState.collectAsState()

    var receiptLink = " "
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

    ReceiptScreen(
        uiState = receiptUiState,
        viewFileState = fileState,
        downloadData = {
            viewModel.downloadReceipt(
                it
            )
        },
        receiptLink
    )
}

@Composable
fun ReceiptScreen(
    uiState: ReceiptUiState,
    viewFileState: PassFileState,
    downloadData: (String) -> Unit,
    receiptLink: String
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
                PassCodeActivity.startPassCodeActivity(
                    context = context,
                    bundle = bundleOf(
                        Pair("uri", receiptLink),
                        Pair(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
                    ),
                )
            }

            is ReceiptUiState.Error -> {
                val message = uiState.message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            is ReceiptUiState.Success -> {
                val transaction = uiState.transaction
                val writeReceiptToPDFisSuccess = viewFileState.writeReceiptToPDFisSuccess
                val file = viewFileState.file
                Receipt(
                    transaction = transaction,
                    receiptLink,
                    downloadData = downloadData,
                    writeReceiptToPDFisSuccess,
                    file
                )
            }

            else -> {}
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Receipt(
    transaction: Transaction,
    receiptLink: String,
    downloadData: (String) -> Unit,
    writeReceiptToPDFisSuccess: Boolean,
    file: File
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val requiredPermissionsBelow33 = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    var storagePermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= 33) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) ==
                        PackageManager.PERMISSION_GRANTED
            } else {
                requiredPermissionsBelow33.all {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }
        )
    }

    var shouldShowPermissionRationale =
        if (Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            requiredPermissionsBelow33.all {
                ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
            }
        }

    var shouldDirectUserToApplicationSettings by remember {
        mutableStateOf(false)
    }

    val decideCurrentPermissionStatus: (Boolean, Boolean) -> String =
        { storagePermissionGranted, shouldShowPermissionRationale ->
            if (storagePermissionGranted) "Granted"
            else if (shouldShowPermissionRationale) "Rejected"
            else "Denied"
        }

    var currentPermissionStatus by remember {
        mutableStateOf(
            decideCurrentPermissionStatus(
                storagePermissionGranted,
                shouldShowPermissionRationale
            )
        )
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionResults ->
            val isGranted = if (Build.VERSION.SDK_INT >= 33) {
                permissionResults[Manifest.permission.READ_MEDIA_IMAGES] ?: false
            } else {
                requiredPermissionsBelow33.all { permissionResults[it] ?: false }
            }
            storagePermissionGranted = isGranted

            if (!isGranted) {
                shouldShowPermissionRationale =
                    if (Build.VERSION.SDK_INT >= 33) {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    } else {
                        requiredPermissionsBelow33.all {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                context as Activity,
                                it
                            )
                        }
                    }
            }
            shouldDirectUserToApplicationSettings =
                !shouldShowPermissionRationale && !storagePermissionGranted
            currentPermissionStatus = decideCurrentPermissionStatus(
                storagePermissionGranted,
                shouldShowPermissionRationale
            )
        })

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START &&
                !storagePermissionGranted &&
                !shouldShowPermissionRationale
            ) {
                if (Build.VERSION.SDK_INT >= 33) {
                    storagePermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                } else {
                    storagePermissionLauncher.launch(requiredPermissionsBelow33)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },

        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.receipt)) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigateBack)
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (file.exists()) {
                        openFile(context, file)
                    } else {
                        if (storagePermissionGranted) {
                            Toast.makeText(
                                context,
                                R.string.downloading_receipt,
                                Toast.LENGTH_SHORT
                            ).show()
                            downloadData(transaction.transactionId.toString())
                            if (!writeReceiptToPDFisSuccess) {
                                Toast.makeText(
                                    context,
                                    R.string.error_downloading_receipt,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                R.string.approve_permission,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
        }
    ) { contentPadding ->
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
                ShowHeadtexts(transaction)
                Spacer(modifier = Modifier.size(height = 15.dp, width = 14.dp))
                ShowInfoTexts(transaction, receiptLink)
            }
        }
    }

    if (writeReceiptToPDFisSuccess) {
        LaunchedEffect(Unit) {
            scope.launch {
                val userAction = snackBarHostState.showSnackbar(
                    message = R.string.approve_permission.toString(),
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

    if (shouldShowPermissionRationale) {
        LaunchedEffect(Unit) {
            scope.launch {
                val userAction = snackBarHostState.showSnackbar(
                    message = R.string.approve_permission.toString(),
                    actionLabel = R.string.approve.toString(),
                    duration = SnackbarDuration.Indefinite,
                    withDismissAction = true
                )
                when (userAction) {
                    SnackbarResult.ActionPerformed -> {
                        shouldShowPermissionRationale = false
                        if (Build.VERSION.SDK_INT >= 33) {
                            storagePermissionLauncher.launch(
                                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                            )
                        } else {
                            storagePermissionLauncher.launch(requiredPermissionsBelow33)
                        }
                    }

                    SnackbarResult.Dismissed -> {
                        shouldShowPermissionRationale = false
                    }
                }
            }
        }
    }

    if (shouldDirectUserToApplicationSettings) {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).also {
            context.startActivity(it)
        }
    }

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
    context.grantUriPermission(
        context.packageName, data, Intent.FLAG_GRANT_READ_URI_PERMISSION
    )
    var intent: Intent? = Intent(Intent.ACTION_VIEW)
        .setDataAndType(data, "application/pdf")
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
fun ShowHeadtexts(transaction: Transaction) {

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
                transaction.transferDetail.toClient.displayName
            } else {
                transaction.transferDetail.fromClient.displayName
            },
            fontSize = 20.sp,
            modifier = centerWithPaddingModifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ShowInfoTexts(
    transaction: Transaction,
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
            text = stringResource(R.string.name) + transaction.transferDetail.toClient.displayName,
            fontSize = 16.sp
        )
        Text(
            text = stringResource(R.string.account_no) + transaction.transferDetail.toAccount.accountNo,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.size(height = 50.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.from),
            color = colorResource(R.color.gray_5),
            fontSize = 16.sp
        )
        Text(
            text = stringResource(R.string.name) + transaction.transferDetail.fromClient.displayName,
            fontSize = 16.sp
        )
        Text(
            text = stringResource(R.string.account_no) + transaction.transferDetail.fromAccount.accountNo,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.size(height = 50.dp, width = 14.dp))

        Text(
            text = stringResource(R.string.unique_receipt_link),
            color = colorResource(R.color.gray_5),
            fontSize = 16.sp
        )
    }
    BottomIcons(transaction, receiptLink)
}

@Composable
fun BottomIcons(
    transaction: Transaction,
    receiptLink: String,
) {
    val context = LocalContext.current
    val prepareShareMessage =
        Constants.RECEIPT_SHARING_MESSAGE + transaction.transferDetail.fromClient.displayName +
                Constants.TO +
                transaction.transferDetail.toClient.displayName +
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

@Preview
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
        receiptLink = "https://receipt.mifospay.com/12345",
        downloadData = {},
        writeReceiptToPDFisSuccess = false,
        file = File("/path/to/receipt.pdf")
    )
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithLoading() {
    MifosTheme {
        ReceiptScreen(
            uiState = ReceiptUiState.Loading,
            viewFileState = PassFileState(file = File(" ")),
            downloadData = {},
            receiptLink = "https://receipt.mifospay.com/12345"
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
            downloadData = {},
            receiptLink = "https://receipt.mifospay.com/12345"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptPreviewWithSuccess() {
    MifosTheme {
        ReceiptScreen(
            uiState = ReceiptUiState.Success(Transaction()),
            viewFileState = PassFileState(file = File(" ")),
            downloadData = {},
            receiptLink = "https://receipt.mifospay.com/12345"
        )
    }
}

