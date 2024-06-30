package org.mifospay.common

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object FileUtils {

    fun writeInputStreamDataToFile(`in`: InputStream, file: File?): Boolean {
        return try {
            val out: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            `in`.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}