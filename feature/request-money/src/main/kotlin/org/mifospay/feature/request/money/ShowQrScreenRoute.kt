package org.mifospay.feature.request.money

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.Bitmap
import android.net.Uri
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.feature.request.money.util.ImageUtils

@Composable
fun ShowQrScreenRoute(
    viewModel: ShowQrViewModel = hiltViewModel(),
    backPress: () -> Unit,
    vpa: String
) {
    val uiState by viewModel.showQrUiState.collectAsStateWithLifecycle()
//    val vpaId by viewModel.vpaId.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.generateQr()
        viewModel.setQrData(vpa)
    }

    UpdateBrightness()

    ShowQrScreen(
        uiState = uiState,
        vpaId = vpa,
        backPress = backPress,
        generateQR = { viewModel.generateQr(requestQrData = it) }
    )
}

@Composable
fun ShowQrScreen(
    uiState: ShowQrUiState,
    vpaId: String,
    backPress: () -> Unit,
    generateQR: (RequestQrData) -> Unit,
) {
    val context = LocalContext.current
    var amountDialogState by rememberSaveable { mutableStateOf(false) }
    var qrBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var amount by rememberSaveable { mutableStateOf<String?>(null) }
    var currency by rememberSaveable { mutableStateOf(context.getString(R.string.feature_request_money_usd)) }

    MifosScaffold(
        topBarTitle = R.string.feature_request_money_request,
        backPress = { backPress.invoke() },
        scaffoldContent = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (uiState) {
                    is ShowQrUiState.Loading -> {
                        MfLoadingWheel(
                            contentDesc = stringResource(R.string.feature_request_money_loading),
                            backgroundColor = MaterialTheme.colorScheme.surface
                        )
                    }

                    is ShowQrUiState.Success -> {
                        if (uiState.qrDataBitmap == null) {
                            EmptyContentScreen(
                                modifier = Modifier,
                                title = stringResource(R.string.feature_request_money_nothing_to_notify),
                                subTitle = stringResource(R.string.feature_request_money_there_is_nothing_to_show),
                                iconTint = MaterialTheme.colorScheme.onSurface,
                                iconImageVector = MifosIcons.Info
                            )
                        } else {
                            qrBitmap = uiState.qrDataBitmap
                            ShowQrContent(
                                qrDataString = vpaId,
                                amount = amount,
                                qrDataBitmap = uiState.qrDataBitmap,
                                showAmountDialog = { amountDialogState = true }
                            )
                        }
                    }

                    is ShowQrUiState.Error -> {
                        EmptyContentScreen(
                            modifier = Modifier,
                            title = stringResource(id = R.string.feature_request_money_error_oops),
                            subTitle = stringResource(id = R.string.feature_request_money_unexpected_error_subtitle),
                            iconTint = MaterialTheme.colorScheme.onSurface,
                            iconImageVector = MifosIcons.Info
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = {
                    qrBitmap?.let {
                        val uri = ImageUtils.saveImage(context = context, bitmap = it)
                        shareQr(context, uri = uri)
                    }
                }
            ) { Icon(MifosIcons.Share, null) }
        }
    )

    if (amountDialogState) {
        SetAmountDialog(
            dismissDialog = { amountDialogState = false },
            prefilledAmount = amount ?: "",
            prefilledCurrency = currency,
            confirmAmount = { confirmedAmount, confirmedCurrency ->
                amount = if (confirmedAmount == "") null
                else confirmedAmount
                currency = confirmedCurrency
                generateQR(RequestQrData(amount = amount ?: "", currency = currency))
                amountDialogState = false
            }
        )
    }
}

@Composable
fun UpdateBrightness() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        setBrightness(context, isFull = true)
        onDispose {
            setBrightness(context, isFull = false)
        }
    }
}

fun setBrightness(context: Context, isFull: Boolean) {
    val activity = context as? Activity ?: return
    val layoutParams: WindowManager.LayoutParams = activity.window.attributes
    layoutParams.screenBrightness =
        if (isFull) 1f else WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    activity.window.attributes = layoutParams
}

private fun shareQr(context: Context, uri: Uri?) {
    if (uri != null) {
        var intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        intent = createChooser(intent, "Share Qr code")
        startActivity(context, intent, null)
    }
}

class ShowQrUiStateProvider :
    PreviewParameterProvider<ShowQrUiState> {
    override val values: Sequence<ShowQrUiState>
        get() = sequenceOf(
            ShowQrUiState.Success(
                qrDataBitmap = Bitmap.createBitmap(
                    100,
                    100,
                    Bitmap.Config.ARGB_8888
                )
            ),
            ShowQrUiState.Error,
            ShowQrUiState.Loading,
        )
}

@Preview(showSystemUi = true)
@Composable
fun ShowQrScreenPreview(@PreviewParameter(ShowQrUiStateProvider::class) uiState: ShowQrUiState) {
    ShowQrScreen(
        uiState = uiState,
        vpaId = "",
        backPress = {},
        generateQR = {}
    )
}
