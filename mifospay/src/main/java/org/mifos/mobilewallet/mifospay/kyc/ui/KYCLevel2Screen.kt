package org.mifos.mobilewallet.mifospay.kyc.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MfOverlayLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosOutlinedTextField
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel2UiState
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel2ViewModel
import org.mifos.mobilewallet.mifospay.theme.MifosTheme


@Composable
fun KYCLevel2Screen(
    viewModel: KYCLevel2ViewModel = hiltViewModel(),
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
        }
    )
}

@Composable
fun KYCLevel2Screen(
    uiState: KYCLevel2UiState,
    uploadData: (
        String,
        Uri,
    ) -> Unit
) {
    val context = LocalContext.current

    Kyc2Form(
        modifier = Modifier,
        uploadData = uploadData
    )

    when (uiState) {
        KYCLevel2UiState.Loading -> {
            MfOverlayLoadingWheel(stringResource(id = R.string.submitting))
        }

        KYCLevel2UiState.Error -> {
            Toast.makeText(
                context,
                stringResource(R.string.error_adding_KYC_Level_2_details),
                Toast.LENGTH_SHORT
            ).show()
        }

        KYCLevel2UiState.Success -> {
            Toast.makeText(
                context,
                stringResource(R.string.successkyc2),
                Toast.LENGTH_SHORT
            ).show()
            //Todo: Navigate To KycLevel3
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

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            result = it
        }

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val hasPermissions =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                launcher.launch(arrayOf("application/pdf", "image/*"))
            } else {
                scope.launch {
                    snackBarHostState.showSnackbar(context.getString(R.string.storage_permission_is_required_to_access_documents))
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .padding(20.dp)
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
                label = R.string.id_type
            )

            Spacer(modifier = Modifier.width(20.dp))

            Row {
                Button(
                    onClick = {
                        if (hasPermissions || SDK_INT >= Build.VERSION_CODES.Q) {
                            launcher.launch(arrayOf("application/pdf", "image/*"))
                        } else {
                            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.browse))
                }
                result?.let { doc ->
                    val fileName = doc.path?.substringAfterLast("/").toString()
                    Text(
                        text = stringResource(id = R.string.file_name) + fileName,
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
                Text(text = stringResource(id = R.string.submit))
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
            uploadData = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Kyc2FormPreviewWithError() {
    MifosTheme {
        KYCLevel2Screen(
            uiState = KYCLevel2UiState.Error,
            uploadData = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Kyc2FormPreviewWithSuccess() {
    MifosTheme {
        KYCLevel2Screen(
            uiState = KYCLevel2UiState.Success,
            uploadData = { _, _ -> }
        )
    }
}