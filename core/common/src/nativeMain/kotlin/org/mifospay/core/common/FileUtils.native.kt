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