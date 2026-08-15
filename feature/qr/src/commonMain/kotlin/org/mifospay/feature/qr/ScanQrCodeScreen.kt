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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun ScanQrCodeScreen(
    navigateBack: () -> Unit,
    navigateToSendScreen: (String) -> Unit,
    navigateToPayeeDetailsScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScanQrViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel) { event ->
        when (event) {
            is ScanQrEvent.OnNavigateToSendScreen -> {
                navigateToSendScreen.invoke(event.data)
            }

            is ScanQrEvent.OnNavigateToPayeeDetails -> {
                navigateToPayeeDetailsScreen.invoke(event.data)
            }

            is ScanQrEvent.ShowToast -> {
                snackbarHostState.showSnackbar(event.message)
            }
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
