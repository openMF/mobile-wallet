package org.mifospay.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    fun saveImage(context: Context, bitmap: Bitmap): Uri? {
        val imagesFolder = File(context.cacheDir, "codes")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_code.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                context,
                "org.mifospay.provider", file
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }
}