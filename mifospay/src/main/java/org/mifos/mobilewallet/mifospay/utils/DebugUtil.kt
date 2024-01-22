package org.mifos.mobilewallet.mifospay.utils

import android.util.Log

/**
 * Created by ankur on 26/June/2018
 */
object DebugUtil {
    @JvmStatic
    fun log(vararg objects: Any): Array<out Any> {
        var stringToPrint = ""
        for (`object` in objects) {
            stringToPrint += "$`object`, "
        }
        stringToPrint = stringToPrint.substring(0, stringToPrint.lastIndexOf(','))
        Log.d("QXZ:: ", stringToPrint)
        return objects
    }
}
