package org.mifospay.feature.read.qr

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.PermissionBox
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.feature.qr.R
import org.mifospay.feature.read.qr.utils.QrCodeAnalyzer


@Composable
fun ShowQrScreenRoute(
    viewModel: ReadQrViewModel = hiltViewModel(),
    backPress: () -> Unit
) {
    val uiState = viewModel.readQrUiState.collectAsStateWithLifecycle()

    ReadQrScreen(
        uiState = uiState.value,
        backPress = backPress,
        scanQR = { viewModel.scanQr(it) }
    )
}

@Composable
fun ReadQrScreen(
    uiState: ReadQrUiState,
    backPress: () -> Unit,
    scanQR: (Bitmap) -> Unit,
) {
    var isFlashOn by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var scannedQrcode by rememberSaveable {
        mutableStateOf("")
    }
    val cameraProviderFuture = rememberSaveable {
        ProcessCameraProvider.getInstance(context)
    }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = loadBitmapFromUri(context, uri)
                bitmap?.let { scanQR(it) }
            }
        }
    val lifecycleOwner = LocalLifecycleOwner.current

    PermissionBox(
        requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
            listOf(
                Manifest.permission.CAMERA
            )
        } else {
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        },
        title = R.string.feature_qr_permission_required,
        description = R.string.feature_qr_approve_permission_description_camera,
        confirmButtonText =  R.string.feature_qr_proceed,
        dismissButtonText = R.string.feature_qr_dismiss,
    )

    MifosScaffold(
        topBarTitle = R.string.feature_qr_scan_code,
        backPress = { backPress.invoke() },
        scaffoldContent = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (uiState) {
                    is ReadQrUiState.Loading -> {
                        MfLoadingWheel(
                            contentDesc = stringResource(R.string.feature_qr_loading),
                            backgroundColor = MaterialTheme.colorScheme.surface
                        )
                    }

                    is ReadQrUiState.Success -> {
                        Text("QR Data: ${uiState.qrData}")
                    }

                    is ReadQrUiState.Error -> {
                        EmptyContentScreen(
                            modifier = Modifier,
                            title = stringResource(R.string.feature_qr_oops),
                            subTitle = stringResource(id = R.string.feature_qr_unexpected_error_subtitle),
                            iconTint = MaterialTheme.colorScheme.onSurface,
                            iconImageVector = MifosIcons.Info
                        )
                    }
                }
            }
        }
    )
    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(
                        Size(
                            previewView.width,
                            previewView.height
                        )
                    )
                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QrCodeAnalyzer { result ->
                        scannedQrcode = result
                    }
                )
                try {
                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                previewView
            },
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { isFlashOn = !isFlashOn }) {
                Icon(
                    imageVector = if (isFlashOn) MifosIcons.FlashOff else MifosIcons.FlashOn,
                    contentDescription = null
                )
            }

            IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(imageVector = MifosIcons.Photo, contentDescription = null)
            }
        }
    }
}

fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val stream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(stream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

class ReadQrUiStateProvider :
    PreviewParameterProvider<ReadQrUiState> {
    override val values: Sequence<ReadQrUiState>
        get() = sequenceOf(
            ReadQrUiState.Success(
                qrData = "This is QR data"
            ),
            ReadQrUiState.Error,
            ReadQrUiState.Loading,
        )
}

@androidx.compose.ui.tooling.preview.Preview(showSystemUi = true)
@Composable
fun ShowQrScreenPreview(@PreviewParameter(ReadQrUiStateProvider::class) uiState: ReadQrUiState) {
    MifosTheme {
        ReadQrScreen(
            uiState = uiState,
            backPress = {},
            scanQR = {
                Bitmap.createBitmap(
                    100,
                    100,
                    Bitmap.Config.ARGB_8888
                )
            }
        )
    }
}