/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.mpay.qr.scan

import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.data.util.QrRouteResult
import org.mifospay.core.data.util.QrTransferRouter
import org.mifospay.core.model.utils.QrCodeData
import org.mifospay.core.model.utils.QrType
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [ScanQrViewModel].
 *
 * Tests cover:
 * - Torch toggle functionality
 * - Help dialog visibility
 * - Image processing states
 * - QR scan routing for intra-bank and inter-bank transfers
 * - Error handling for invalid QR codes
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ScanQrViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val qrTransferRouter: QrTransferRouter = mock()
    private lateinit var viewModel: ScanQrViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScanQrViewModel(
            qrTransferRouter = qrTransferRouter,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Torch Tests

    @Test
    fun givenInitialState_whenViewModelCreated_thenTorchIsDisabled() {
        assertFalse(viewModel.isTorchEnabled.value)
    }

    @Test
    fun givenTorchDisabled_whenToggleTorch_thenTorchIsEnabled() = runTest {
        viewModel.toggleTorch()
        advanceUntilIdle()
        assertTrue(viewModel.isTorchEnabled.value)
    }

    @Test
    fun givenTorchEnabled_whenToggleTorch_thenTorchIsDisabled() = runTest {
        viewModel.toggleTorch()
        advanceUntilIdle()
        assertTrue(viewModel.isTorchEnabled.value)

        viewModel.toggleTorch()
        advanceUntilIdle()
        assertFalse(viewModel.isTorchEnabled.value)
    }

    @Test
    fun givenAnyState_whenSetTorchEnabled_thenTorchStateIsUpdated() = runTest {
        viewModel.setTorchEnabled(true)
        advanceUntilIdle()
        assertTrue(viewModel.isTorchEnabled.value)

        viewModel.setTorchEnabled(false)
        advanceUntilIdle()
        assertFalse(viewModel.isTorchEnabled.value)
    }

    // endregion

    // region Help Dialog Tests

    @Test
    fun givenInitialState_whenViewModelCreated_thenHelpDialogIsHidden() {
        assertFalse(viewModel.showHelpDialog.value)
    }

    @Test
    fun givenHelpDialogHidden_whenShowHelpDialog_thenHelpDialogIsVisible() = runTest {
        viewModel.showHelpDialog()
        advanceUntilIdle()
        assertTrue(viewModel.showHelpDialog.value)
    }

    @Test
    fun givenHelpDialogVisible_whenHideHelpDialog_thenHelpDialogIsHidden() = runTest {
        viewModel.showHelpDialog()
        advanceUntilIdle()
        assertTrue(viewModel.showHelpDialog.value)

        viewModel.hideHelpDialog()
        advanceUntilIdle()
        assertFalse(viewModel.showHelpDialog.value)
    }

    // endregion

    // region Image Processing Tests

    @Test
    fun givenInitialState_whenViewModelCreated_thenNotProcessingImage() {
        assertFalse(viewModel.isProcessingImage.value)
    }

    @Test
    fun givenNotProcessing_whenSetProcessingTrue_thenIsProcessing() = runTest {
        viewModel.setProcessingImage(true)
        advanceUntilIdle()
        assertTrue(viewModel.isProcessingImage.value)
    }

    @Test
    fun givenProcessing_whenSetProcessingFalse_thenNotProcessing() = runTest {
        viewModel.setProcessingImage(true)
        advanceUntilIdle()
        assertTrue(viewModel.isProcessingImage.value)

        viewModel.setProcessingImage(false)
        advanceUntilIdle()
        assertFalse(viewModel.isProcessingImage.value)
    }

    @Test
    fun givenInitialState_whenViewModelCreated_thenSelectedImageBytesIsNull() {
        assertNull(viewModel.selectedImageBytes.value)
    }

    @Test
    fun givenNoImage_whenSetSelectedImageBytes_thenImageBytesAreSet() = runTest {
        val imageBytes = byteArrayOf(1, 2, 3, 4)
        viewModel.setSelectedImageBytes(imageBytes)
        advanceUntilIdle()
        assertEquals(imageBytes, viewModel.selectedImageBytes.value)
    }

    @Test
    fun givenImageSelected_whenClearSelectedImage_thenImageBytesAreNullAndNotProcessing() = runTest {
        viewModel.setSelectedImageBytes(byteArrayOf(1, 2, 3))
        viewModel.setProcessingImage(true)
        advanceUntilIdle()

        viewModel.clearSelectedImage()
        advanceUntilIdle()

        assertNull(viewModel.selectedImageBytes.value)
        assertFalse(viewModel.isProcessingImage.value)
    }

    // endregion

    // region Image QR Scan Tests

    @Test
    fun givenImageSelected_whenOnImageQrScannedWithNull_thenNoQrFoundEventEmitted() = runTest {
        viewModel.setSelectedImageBytes(byteArrayOf(1, 2, 3))
        viewModel.setProcessingImage(true)
        advanceUntilIdle()

        viewModel.eventFlow.test {
            viewModel.onImageQrScanned(null)
            advanceUntilIdle()

            val event = awaitItem()
            assertIs<ScanQrEvent.OnNoQrFound>(event)
        }

        assertFalse(viewModel.isProcessingImage.value)
        assertNull(viewModel.selectedImageBytes.value)
    }

    // endregion

    // region QR Routing Tests

    @Test
    fun givenIntraBankQr_whenOnScanned_thenNavigateToIntraBankTransfer() = runTest {
        val qrData = QrCodeData(
            fspId = "mifos-bank",
            type = QrType.INTRA_BANK,
            clientId = 123L,
            accountId = 456L,
        )

        every {
            qrTransferRouter.routeQrScan(qrData)
        } returns QrRouteResult.IntraBank(qrData)

        viewModel.eventFlow.test {
            val result = viewModel.onScanned(
                "MPAY:mifos-bank:INTRA_BANK:123:456",
            )
            advanceUntilIdle()

            assertTrue(result)
            // Skip OnScanSuccess and check for navigation event
            skipItems(1)
            val event = awaitItem()
            assertIs<ScanQrEvent.OnNavigateToIntraBankTransfer>(event)
            assertEquals(qrData, event.qrData)
        }
    }

    @Test
    fun givenInterBankQr_whenOnScanned_thenNavigateToInterBankTransfer() = runTest {
        val qrData = QrCodeData(
            fspId = "other-bank",
            type = QrType.INTER_BANK,
            accountExternalId = "ACC123",
            recipientName = "John Doe",
        )

        every {
            qrTransferRouter.routeQrScan(qrData)
        } returns QrRouteResult.InterBank(
            accountExternalId = "ACC123",
            recipientName = "John Doe",
            amount = "100",
        )

        viewModel.eventFlow.test {
            val result = viewModel.onScanned(
                "MPAY:other-bank:INTER_BANK:::ACC123:John Doe",
            )
            advanceUntilIdle()

            assertTrue(result)
            // Skip OnScanSuccess
            skipItems(1)
            val event = awaitItem()
            assertIs<ScanQrEvent.OnNavigateToInterbankTransfer>(event)
            assertEquals("ACC123", event.accountExternalId)
            assertEquals("John Doe", event.recipientName)
        }
    }

    @Test
    fun givenInvalidQrCode_whenOnScanned_thenShowErrorToast() = runTest {
        viewModel.eventFlow.test {
            val result = viewModel.onScanned("invalid-qr-data")
            advanceUntilIdle()

            assertFalse(result)
            val event = awaitItem()
            assertIs<ScanQrEvent.ShowToast>(event)
            assertEquals("Scan a Valid QR Code", event.message)
        }
    }

    // endregion
}
