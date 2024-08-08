package org.mifospay.common

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object FileUtils {

    fun writeInputStreamDataToFile(inputStream: InputStream, file: File?): Boolean {
        return try {
            val out: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            inputStream.close()
            true
        } catch (e: Exception) {
            Log.e("Message",e.message.toString())
            false
        }
    }
}