/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.compose.rememberAsyncImagePainter
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch
import mobile_wallet.feature.kyc.generated.resources.Res
import mobile_wallet.feature.kyc.generated.resources.feature_kyc_file_name
import mobile_wallet.feature.kyc.generated.resources.feature_kyc_submit
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun KYCLevel2Screen(
    navigateBack: () -> Unit,
    navigateToLevel3: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KYCLevel2ViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is KycLevel2Event.OnNavigateBack -> navigateBack.invoke()
            KycLevel2Event.OnNavigateToLevel3 -> navigateToLevel3.invoke()
            is KycLevel2Event.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    KycLevel2Dialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(KycLevel2Action.DismissDialog) }
        },
    )

    KYCLevel2ScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier.fillMaxSize(),
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
internal fun KYCLevel2ScreenContent(
    state: KycLevel2State,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (KycLevel2Action) -> Unit,
) {
    MifosScaffold(
        topBarTitle = "Upload Documents",
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        backPress = {
            onAction(KycLevel2Action.NavigateBack)
        },
    ) { paddingValues ->
        KYCLevel2ScreenContent(
            state = state,
            onAction = onAction,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun KYCLevel2ScreenContent(
    state: KycLevel2State,
    modifier: Modifier = Modifier,
    onAction: (KycLevel2Action) -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DocumentPicker(
            onChooseDocument = {
                onAction(KycLevel2Action.FileChanged(it))
            },
        )

        MifosTextField(
            value = state.name,
            label = stringResource(Res.string.feature_kyc_file_name),
            onValueChange = {
                onAction(KycLevel2Action.NameChanged(it))
            },
            onClickClearIcon = {
                onAction(KycLevel2Action.NameChanged(""))
            },
        )

        MifosTextField(
            value = state.description,
            label = "Description",
            onValueChange = {
                onAction(KycLevel2Action.DescriptionChanged(it))
            },
            onClickClearIcon = {
                onAction(KycLevel2Action.DescriptionChanged(""))
            },
        )

        MifosButton(
            onClick = {
                onAction(KycLevel2Action.SubmitClicked)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(Res.string.feature_kyc_submit))
        }
    }
}

@Composable
private fun DocumentPicker(
    modifier: Modifier = Modifier,
    onChooseDocument: (PlatformFile) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    var uploadedImage by remember { mutableStateOf<ByteArray?>(null) }

    val painter = rememberAsyncImagePainter(
        model = uploadedImage,
        imageLoader = ImageLoader(context),
    )

    val filePicker = rememberFilePickerLauncher(
        mode = PickerMode.Single,
    ) {
        scope.launch {
            it?.let { file ->
                onChooseDocument(file)

                uploadedImage = if (file.supportsStreams()) {
                    val size = file.getSize()
                    if (size != null && size > 0L) {
                        val buffer = ByteArray(size.toInt())
                        val tmpBuffer = ByteArray(1000)
                        var totalBytesRead = 0
                        file.getStream().use {
                            while (it.hasBytesAvailable()) {
                                val numRead = it.readInto(tmpBuffer, 1000)
                                tmpBuffer.copyInto(
                                    buffer,
                                    destinationOffset = totalBytesRead,
                                    endIndex = numRead,
                                )
                                totalBytesRead += numRead
                            }
                        }
                        buffer
                    } else {
                        file.readBytes()
                    }
                } else {
                    file.readBytes()
                }
            }
        }
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
        onClick = filePicker::launch,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            SubcomposeAsyncImage(
                model = uploadedImage,
                imageLoader = ImageLoader(context),
                contentScale = ContentScale.None,
                contentDescription = "Uploaded Image",
                modifier = Modifier.align(Alignment.Center),
            ) {
                val painterState by painter.state.collectAsStateWithLifecycle()

                when (painterState) {
                    is AsyncImagePainter.State.Empty -> {
                        AvatarBox(
                            icon = MifosIcons.Add,
                            size = 120,
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        if (uploadedImage == null) {
                            AvatarBox(
                                icon = MifosIcons.Add,
                                size = 120,
                            )
                        } else {
                            Text(
                                text = "Unsupported Media Type for Preview",
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }

                    is AsyncImagePainter.State.Loading -> {
                        MifosLoadingWheel(
                            contentDesc = "Loading Image",
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent(
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KycLevel2Dialogs(
    dialogState: KycLevel2State.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is KycLevel2State.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is KycLevel2State.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}
