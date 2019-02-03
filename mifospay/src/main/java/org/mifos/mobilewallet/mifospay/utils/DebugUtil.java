package org.mifos.mobilewallet.mifospay.utils;

import android.util.Log;

/**
 * Created by ankur on 26/June/2018
 */

public class DebugUtil {

    public static Object[] log(Object... objects) {

        StringBuilder stringBuilderToPrint = new StringBuilder();
        for (Object object : objects) {
            stringBuilderToPrint.append(object).append(", ");
        }
        String stringToPrint = stringBuilderToPrint.toString();
        stringToPrint = stringToPrint.substring(0, stringToPrint.lastIndexOf(','));
        Log.d("QXZ:: ", stringToPrint);
        return objects;
    }
}
