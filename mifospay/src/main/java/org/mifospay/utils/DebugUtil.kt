/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.utils

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
