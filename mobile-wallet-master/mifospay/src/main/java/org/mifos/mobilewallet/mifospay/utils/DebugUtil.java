package org.mifos.mobilewallet.mifospay.utils;

import android.util.Log;

/**
 * Created by ankur on 26/June/2018
 */

public class DebugUtil {

    public static Object[] log(Object... objects) {

        String stringToPrint = "";
        for (Object object : objects) {
            stringToPrint += object + ", ";
        }
        stringToPrint = stringToPrint.substring(0, stringToPrint.lastIndexOf(','));
        Log.d("QXZ:: ", stringToPrint);
        return objects;
    }
}
