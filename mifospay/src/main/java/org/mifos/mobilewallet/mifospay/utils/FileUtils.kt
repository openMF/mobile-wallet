package org.mifos.mobilewallet.mifospay.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil.log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by ankur on 24/May/2018
 */
object FileUtils {
    private const val TAG = "FileUtils"
    fun readJson(context: Context, file: String?): JSONObject? {
        var jsonObject: JSONObject? = null
        try {
            val `is` = context.assets.open(file!!)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            jsonObject = JSONObject(String(buffer, charset("UTF-8")))
        } catch (e: Exception) {
            log(e.toString(), e.message!!)
        }
        return jsonObject
    }

    /**
     * Method for return file path of Gallery image/ Document / Video / Audio
     *
     * @return path of the selected image file from gallery
     */
    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {

                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return (Environment.getExternalStorageDirectory().toString() + "/"
                                + split[1])
                    }
                } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), id.toLong()
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) { // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(
                        context, contentUri, selection,
                        selectionArgs
                    )
                }
            } else if ("content".equals(
                    uri.scheme,
                    ignoreCase = true
                )
            ) { // MediaStore (and general)

                // Return the remote address
                return if (isGooglePhotosUri(uri)) {
                    uri.lastPathSegment
                } else getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )
            } else if ("file".equals(uri.scheme, ignoreCase = true)) { // File
                return uri.path
            }
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
    }

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

    fun saveImage(context: Context?, imageData: String?): File? {
        val imgBytesData = Base64.decode(
            imageData,
            Base64.DEFAULT
        )
        var file: File? = null
        try {
            val mifosDirectory = File(
                Environment.getExternalStorageDirectory(),
                Constants.MIFOSPAY
            )
            file = File(mifosDirectory, "kuch")
        } catch (e: Exception) {
            Log.e(TAG, "Unable to create file", e)
        }
        val fileOutputStream: FileOutputStream
        fileOutputStream = try {
            FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Unable to create output stream", e)
            return null
        }
        val bufferedOutputStream = BufferedOutputStream(
            fileOutputStream
        )
        try {
            bufferedOutputStream.write(imgBytesData)
        } catch (e: IOException) {
            Log.e(TAG, "Unable to write", e)
            return null
        } finally {
            try {
                bufferedOutputStream.close()
            } catch (e: IOException) {
                Log.e(TAG, "Unable to close the output stream", e)
            }
        }
        return file
    }
}