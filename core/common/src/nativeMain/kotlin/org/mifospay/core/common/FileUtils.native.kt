/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.common

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// iOS implementation
@OptIn(ExperimentalForeignApi::class)
actual fun createPlatformFileUtils(): FileUtils = object : FileUtils {
    override suspend fun writeInputStreamDataToFile(inputStream: ByteArray, filePath: String): Boolean =
        withContext(Dispatchers.Default) {
            try {
                val nsData = inputStream.toNSData()
                nsData.writeToFile(filePath, true)
                true
            } catch (e: Exception) {
                FileUtils.logger.e { "Error writing file: ${e.message}" }
                false
            }
        }

    private fun ByteArray.toNSData(): NSData = NSData.create(bytes = this.refTo(0), length = this.size.toULong())
}
