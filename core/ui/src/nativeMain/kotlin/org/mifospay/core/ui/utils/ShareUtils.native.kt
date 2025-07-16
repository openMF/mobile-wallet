/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.ui.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

/**
 * Actual implementation of [ShareUtils] for iOS platform.
 *
 * Provides functionality to share text and files using iOS native `UIActivityViewController`.
 */
actual object ShareUtils {

    /**
     * Shares plain text using the iOS share sheet (`UIActivityViewController`).
     *
     * @param text The text content to be shared.
     */
    actual fun shareText(text: String) {
        val currentViewController = UIApplication.sharedApplication().keyWindow?.rootViewController
        val activityViewController = UIActivityViewController(listOf(text), null)
        currentViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null,
        )
    }

    /**
     * Shares a file using the iOS share sheet (`UIActivityViewController`).
     *
     * If the file is an image, it is optionally compressed before sharing.
     * The file is temporarily saved to disk before being shared.
     *
     * @param file A [ShareFileModel] containing the file to be shared.
     */
    actual suspend fun shareFile(file: ShareFileModel) {
        runCatching {
            val url = withContext(Dispatchers.IO) {
                val compressedBytes = if (file.mime == MimeType.IMAGE) {
                    compressImage(file.bytes)
                } else {
                    file.bytes
                }
                saveFile(bytes = compressedBytes, name = file.fileName)
            }
            val activityViewController = UIActivityViewController(listOf(url), null)
            UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
                activityViewController,
                animated = true,
                completion = null,
            )
        }
    }

    /**
     * Saves the given byte array to a temporary file in the device's cache directory.
     *
     * @param bytes The content of the file to save.
     * @param name The desired filename (including extension).
     * @return An [NSURL] pointing to the saved file, or `null` if saving failed.
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun saveFile(bytes: ByteArray, name: String): NSURL? {
        val tempDir = NSTemporaryDirectory()
        val sharedFile = tempDir + name
        val saved = bytes.usePinned {
            val nsData = NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong())
            nsData.writeToFile(sharedFile, true)
        }
        return if (saved) NSURL.fileURLWithPath(sharedFile) else null
    }

    /**
     * Compresses an image file using [FileKit] logic.
     *
     * @param imageBytes The original image byte array.
     * @return A compressed image as a byte array.
     */
    private suspend fun compressImage(imageBytes: ByteArray): ByteArray {
        return FileKit.compressImage(
            bytes = imageBytes,
            // Compression quality (0–100)
            quality = 100,
            // Max width in pixels
            maxWidth = 1024,
            // Max height in pixels
            maxHeight = 1024,
            // Image format (e.g., PNG or JPEG)
            imageFormat = ImageFormat.PNG,
        )
    }
}
