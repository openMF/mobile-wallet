package org.mifospay.feature.kyc

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.kyc.R


@Composable
fun KYCLevel2Screen(
    viewModel: KYCLevel2ViewModel = hiltViewModel(),
    onSuccessKyc2: () -> Unit
) {
    val kyc2uiState by viewModel.kyc2uiState.collectAsStateWithLifecycle()

    KYCLevel2Screen(
        uiState = kyc2uiState,
        uploadData = { idType,
                       uri ->
            viewModel.uploadKYCDocs(
                idType,
                uri
            )
        },
        onSuccessKyc2 = onSuccessKyc2
    )
}

@Composable
fun KYCLevel2Screen(
    uiState: KYCLevel2UiState,
    uploadData: (
        String,
        Uri,
    ) -> Unit,
    onSuccessKyc2: () -> Unit
) {
    val context = LocalContext.current

    Kyc2Form(
        modifier = Modifier,
        uploadData = uploadData
    )

    when (uiState) {
        KYCLevel2UiState.Loading -> {
            MfOverlayLoadingWheel(stringResource(id = R.string.feature_kyc_submitting))
        }

        KYCLevel2UiState.Error -> {
            Toast.makeText(
                context,
                stringResource(R.string.feature_kyc_error_adding_KYC_Level_2_details),
                Toast.LENGTH_SHORT
            ).show()
        }

        KYCLevel2UiState.Success -> {
            Toast.makeText(
                context,
                stringResource(R.string.feature_kyc_successkyc2),
                Toast.LENGTH_SHORT
            ).show()
            onSuccessKyc2.invoke()
        }
    }

}

@Composable
fun Kyc2Form(
    modifier: Modifier,
    uploadData: (
        String,
        Uri,
    ) -> Unit
) {
    var idType by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var result by rememberSaveable { mutableStateOf<Uri?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val docLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            result = it
        }

    var storagePermissionGranted by remember {
        mutableStateOf(
            if (SDK_INT >= 33) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) ==
                        PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                        PackageManager.PERMISSION_GRANTED
            }
        )
    }

    var shouldShowPermissionRationale =
        if (SDK_INT >= 33) {
            shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
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

    val permission = if (SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            storagePermissionGranted = isGranted

            if (!isGranted) {
                shouldShowPermissionRationale =
                    if (SDK_INT >= 33) {
                        shouldShowRequestPermissionRationale(
                            context,
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    } else {
                        shouldShowRequestPermissionRationale(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
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
                storagePermissionLauncher.launch(permission)
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
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                MifosOutlinedTextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    value = idType,
                    onValueChange = {
                        idType = it
                    },
                    label = R.string.feature_kyc_id_type
                )

                Spacer(modifier = Modifier.width(20.dp))

                Row {
                    Button(
                        onClick = {
                            if (storagePermissionGranted) {
                                docLauncher.launch(arrayOf("application/pdf", "image/*"))
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.feature_kyc_approve_permission,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.feature_kyc_browse))
                    }
                    result?.let { doc ->
                        val fileName = doc.path?.substringAfterLast("/").toString()
                        Text(
                            text = stringResource(id = R.string.feature_kyc_file_name) + fileName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        result?.let { uri ->
                            uploadData(idType, uri)
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.feature_kyc_submit))
                }
            }
        }

        if (shouldShowPermissionRationale) {
            LaunchedEffect(Unit) {
                scope.launch {
                    val userAction = snackBarHostState.showSnackbar(
                        message = R.string.feature_kyc_approve_permission.toString(),
                        actionLabel = R.string.feature_kyc_approve.toString(),
                        duration = SnackbarDuration.Indefinite,
                        withDismissAction = true
                    )
                    when (userAction) {
                        SnackbarResult.ActionPerformed -> {
                            shouldShowPermissionRationale = false
                            storagePermissionLauncher.launch(permission)
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
}

@Preview(showBackground = true)
@Composable
fun EmptyKyc2FormPreview() {
    MifosTheme {
        Kyc2Form(
            modifier = Modifier,
            uploadData = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Kyc2FormPreviewWithLoading() {
    MifosTheme {
        KYCLevel2Screen(
            uiState = KYCLevel2UiState.Loading,
            uploadData = { _, _ -> },
            onSuccessKyc2 = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Kyc2FormPreviewWithError() {
    MifosTheme {
        KYCLevel2Screen(
            uiState = KYCLevel2UiState.Error,
            uploadData = { _, _ -> },
            onSuccessKyc2 = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Kyc2FormPreviewWithSuccess() {
    MifosTheme {
        KYCLevel2Screen(
            uiState = KYCLevel2UiState.Success,
            uploadData = { _, _ -> },
            onSuccessKyc2 = {}
        )
    }
}