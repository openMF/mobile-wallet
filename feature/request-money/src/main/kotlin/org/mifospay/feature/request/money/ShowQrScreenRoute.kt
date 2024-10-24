/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.request.money

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.WindowManager
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosBlue
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.feature.request.money.util.ImageUtils

@Composable
internal fun ShowQrScreenRoute(
    backPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowQrViewModel = koinViewModel(),
) {
    val uiState by viewModel.showQrUiState.collectAsStateWithLifecycle()

    UpdateBrightness()

    ShowQrScreen(
        uiState = uiState,
        backPress = backPress,
        generateQR = viewModel::generateQr,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun ShowQrScreen(
    uiState: ShowQrUiState,
    backPress: () -> Unit,
    generateQR: (RequestQrData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var amountDialogState by rememberSaveable { mutableStateOf(false) }
    var qrBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var amount by rememberSaveable { mutableStateOf<String?>(null) }
    var currency by rememberSaveable { mutableStateOf(context.getString(R.string.feature_request_money_usd)) }

    MifosScaffold(
        topBarTitle = R.string.feature_request_money_request,
        backPress = backPress,
        titleColor = Color.White,
        iconTint = Color.White,
        scaffoldContent = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (uiState) {
                    is ShowQrUiState.Loading -> {
                        MfLoadingWheel(
                            contentDesc = stringResource(R.string.feature_request_money_loading),
                            backgroundColor = MaterialTheme.colorScheme.surface,
                        )
                    }

                    is ShowQrUiState.Success -> {
                        if (uiState.qrDataBitmap == null) {
                            EmptyContentScreen(
                                title = stringResource(R.string.feature_request_money_nothing_to_notify),
                                subTitle = stringResource(R.string.feature_request_money_there_is_nothing_to_show),
                                modifier = Modifier,
                                iconTint = MaterialTheme.colorScheme.onSurface,
                                iconImageVector = MifosIcons.Info,
                            )
                        } else {
                            qrBitmap = uiState.qrDataBitmap
                            ShowQrContent(
                                qrDataBitmap = uiState.qrDataBitmap,
                                showAmountDialog = { amountDialogState = true },
                                onShare = {
                                    qrBitmap?.let {
                                        Log.d("yesyesyes", it.toString())
                                        val uri = ImageUtils.saveImage(context = context, bitmap = it)
                                        shareQr(context, uri = uri)
                                    }
                                },
                            )
                        }
                    }

                    is ShowQrUiState.Error -> {
                        EmptyContentScreen(
                            title = stringResource(id = R.string.feature_request_money_error_oops),
                            subTitle = stringResource(id = R.string.feature_request_money_unexpected_error_subtitle),
                            modifier = Modifier,
                            iconTint = MaterialTheme.colorScheme.onSurface,
                            iconImageVector = MifosIcons.Info,
                        )
                    }
                }
            }
        },
        modifier = modifier.background(MifosBlue),
    )

    if (amountDialogState) {
        SetAmountDialog(
            dismissDialog = { amountDialogState = false },
            prefilledAmount = amount ?: "",
            prefilledCurrency = currency,
            confirmAmount = { confirmedAmount, confirmedCurrency ->
                amount = if (confirmedAmount == "") {
                    null
                } else {
                    confirmedAmount
                }
                currency = confirmedCurrency
                generateQR(RequestQrData(amount = amount ?: "", currency = currency))
                amountDialogState = false
            },
        )
    }
}

@Composable
private fun UpdateBrightness() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        setBrightness(context, isFull = true)
        onDispose {
            setBrightness(context, isFull = false)
        }
    }
}

private fun setBrightness(context: Context, isFull: Boolean) {
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

internal class ShowQrUiStateProvider :
    PreviewParameterProvider<ShowQrUiState> {
    override val values: Sequence<ShowQrUiState>
        get() = sequenceOf(
            ShowQrUiState.Success(
                qrDataBitmap = Bitmap.createBitmap(
                    100,
                    100,
                    Bitmap.Config.ARGB_8888,
                ),
            ),
            ShowQrUiState.Error,
            ShowQrUiState.Loading,
        )
}

@Preview(showSystemUi = true)
@Composable
private fun ShowQrScreenPreview(
    @PreviewParameter(ShowQrUiStateProvider::class)
    uiState: ShowQrUiState,
) {
    ShowQrScreen(
        uiState = uiState,
        backPress = {},
        generateQR = {},
    )
}
