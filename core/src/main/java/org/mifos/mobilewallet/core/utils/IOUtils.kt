package org.mifos.mobilewallet.core.utils

import android.content.Context
import java.io.IOException

object IOUtils {

    fun loadJSONFromAsset(context: Context, fileName: String): String? {
        var json: String? = null
        try {
            val `is` = context.getAssets().open(fileName)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }
}