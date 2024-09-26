package org.mifospay.core.common

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM


interface FileUtils {
    suspend fun writeInputStreamDataToFile(inputStream: ByteArray, filePath: String): Boolean

    companion object {
        val logger = Logger.withTag("FileUtils")
    }
}

expect fun createPlatformFileUtils(): FileUtils

class CommonFileUtils : FileUtils {
    override suspend fun writeInputStreamDataToFile(
        inputStream: ByteArray,
        filePath: String,
    ): Boolean =
        withContext(Dispatchers.Default) {
            try {
                val path = filePath.toPath()
                FileSystem.SYSTEM.write(path) {
                    write(inputStream)
                }

                true
            } catch (e: Exception) {
                FileUtils.logger.e { "Error writing file: ${e.message}" }
                false
            }
        }
}