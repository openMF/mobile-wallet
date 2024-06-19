package org.mifospay.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import org.mifospay.BuildConfig
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
                BuildConfig.APPLICATION_ID + ".provider", file
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}