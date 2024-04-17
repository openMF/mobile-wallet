package org.mifospay.common

import android.util.Log

// TODO Move into separate module
object DebugUtil {

    fun log(vararg objects: Any): Array<Any> {
        var stringToPrint = ""
        for (`object` in objects) {
            stringToPrint += "$`object`, "
        }
        stringToPrint = stringToPrint.substring(0, stringToPrint.lastIndexOf(','))
        Log.d("QXZ:: ", stringToPrint)
        return objects as Array<Any>
    }
}
