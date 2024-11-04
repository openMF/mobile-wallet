/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.qr

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosScaffold

@Composable
internal fun ScanQrCodeScreen(
    navigateBack: () -> Unit,
    navigateToSendScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScanQrViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val eventFlow by viewModel.eventFlow.collectAsStateWithLifecycle(null)

    LaunchedEffect(key1 = eventFlow) {
        when (eventFlow) {
            is ScanQrEvent.OnNavigateToSendScreen -> {
                navigateToSendScreen.invoke((eventFlow as ScanQrEvent.OnNavigateToSendScreen).data)
            }

            is ScanQrEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar((eventFlow as ScanQrEvent.ShowToast).message)
                }
            }

            null -> Unit
        }
    }

    ScanQrCodeScreenContent(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onScanned = viewModel::onScanned,
        backPress = navigateBack,
    )
}

@Composable
fun ScanQrCodeScreenContent(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onScanned: (String) -> Boolean,
    backPress: () -> Unit,
) {
    MifosScaffold(
        topBarTitle = null,
        backPress = backPress,
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            QrScannerWithPermissions(
                types = listOf(CodeType.QR),
                modifier = Modifier.padding(it),
                onScanned = onScanned,
            )
        }
    }
}
